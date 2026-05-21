#!/usr/bin/env node

/**
 * Slack Notification Simulator for CI/CD Pipeline
 *
 * Demonstrates how Devin-driven CI can integrate with enterprise communication
 * workflows. In production, replace the console output with a real Slack
 * webhook POST (SLACK_WEBHOOK_URL environment variable).
 *
 * Usage:
 *   node slack-notify.js \
 *     --event "pull_request" \
 *     --ref "feature-branch" \
 *     --commit "abc1234" \
 *     --gradle-result "success" \
 *     --playwright-result "success" \
 *     --run-url "https://github.com/..." \
 *     --pr-number "42" \
 *     --pr-title "Add test coverage" \
 *     --actor "devin-ai"
 */

function parseArgs(args) {
  const parsed = {};
  for (let i = 0; i < args.length; i += 2) {
    const key = args[i].replace(/^--/, '').replace(/-/g, '_');
    parsed[key] = args[i + 1] || '';
  }
  return parsed;
}

function statusEmoji(result) {
  if (result === 'success') return ':white_check_mark:';
  if (result === 'skipped') return ':fast_forward:';
  return ':x:';
}

function buildSlackPayload(params) {
  const allPassed = params.gradle_result === 'success' &&
    (params.playwright_result === 'success' || params.playwright_result === 'skipped');

  const headerEmoji = allPassed ? ':large_green_circle:' : ':red_circle:';
  const headerText = allPassed ? 'All checks passed' : 'CI checks failed — action required';

  const blocks = [
    {
      type: 'header',
      text: { type: 'plain_text', text: `${headerEmoji} CI: checkout-example` }
    },
    {
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `*${headerText}*\n` +
          `*Event:* \`${params.event}\` on \`${params.ref}\`\n` +
          `*Commit:* \`${params.commit?.substring(0, 7)}\`\n` +
          `*Actor:* ${params.actor}`
      }
    },
    {
      type: 'section',
      fields: [
        {
          type: 'mrkdwn',
          text: `*Backend (JUnit)*\n${statusEmoji(params.gradle_result)} ${params.gradle_result}`
        },
        {
          type: 'mrkdwn',
          text: `*E2E (Playwright)*\n${statusEmoji(params.playwright_result)} ${params.playwright_result}`
        }
      ]
    }
  ];

  if (params.pr_number) {
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `*PR #${params.pr_number}:* ${params.pr_title}`
      }
    });
  }

  if (!allPassed) {
    const failedSuites = [];
    if (params.gradle_result !== 'success') failedSuites.push('Backend (JUnit)');
    if (params.playwright_result !== 'success' && params.playwright_result !== 'skipped') {
      failedSuites.push('E2E (Playwright)');
    }
    blocks.push({
      type: 'section',
      text: {
        type: 'mrkdwn',
        text: `:mag: *Failed suites:* ${failedSuites.join(', ')}\n` +
          `<${params.run_url}|View full CI run for debugging context>`
      }
    });
  }

  blocks.push({
    type: 'actions',
    elements: [
      {
        type: 'button',
        text: { type: 'plain_text', text: 'View CI Run' },
        url: params.run_url || '#'
      }
    ]
  });

  return { blocks };
}

async function sendToSlack(payload, webhookUrl) {
  const response = await fetch(webhookUrl, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  if (!response.ok) {
    console.error(`Slack webhook failed: ${response.status} ${response.statusText}`);
    process.exit(1);
  }
  console.log('Slack notification sent successfully.');
}

// --- Main ---
const params = parseArgs(process.argv.slice(2));
const payload = buildSlackPayload(params);

console.log('=== Slack Notification Payload ===');
console.log(JSON.stringify(payload, null, 2));
console.log('=================================');

const webhookUrl = process.env.SLACK_WEBHOOK_URL;
if (webhookUrl) {
  sendToSlack(payload, webhookUrl).catch(err => {
    console.error('Failed to send Slack notification:', err);
    process.exit(1);
  });
} else {
  console.log(
    '\nSLACK_WEBHOOK_URL not set — running in simulation mode.\n' +
    'To enable real Slack notifications, add SLACK_WEBHOOK_URL as a repository secret.\n' +
    'See: https://api.slack.com/messaging/webhooks'
  );
}
