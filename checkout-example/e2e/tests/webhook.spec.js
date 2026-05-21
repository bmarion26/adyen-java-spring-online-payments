const { test, expect } = require('@playwright/test');

// Webhook endpoint tests using direct HTTP calls via Playwright's request context.
// These validate webhook processing behavior without needing a browser.

test.describe('Webhook Endpoint', () => {
  test('should reject GET requests to webhook endpoint', async ({ request }) => {
    const response = await request.get('/api/webhooks/notifications');
    expect(response.status()).toBe(405);
  });

  test('should reject empty body', async ({ request }) => {
    const response = await request.post('/api/webhooks/notifications', {
      headers: { 'Content-Type': 'application/json' },
      data: '',
    });
    // Empty body results in a 400 Bad Request
    expect(response.status()).toBeGreaterThanOrEqual(400);
  });

  test('should reject invalid HMAC signature', async ({ request }) => {
    const response = await request.post('/api/webhooks/notifications', {
      headers: { 'Content-Type': 'application/json' },
      data: JSON.stringify({
        live: 'false',
        notificationItems: [
          {
            NotificationRequestItem: {
              eventCode: 'AUTHORISATION',
              merchantAccountCode: 'TestMerchant',
              merchantReference: 'ref-123',
              pspReference: 'psp-456',
              amount: { currency: 'EUR', value: 10000 },
              success: 'true',
              additionalData: {
                alias: 'test-alias',
                hmacSignature: 'clearly-invalid-signature',
              },
            },
          },
        ],
      }),
    });
    // Invalid HMAC should result in 500 (RuntimeException thrown)
    expect(response.status()).toBe(500);
  });

  test('should reject empty notification items', async ({ request }) => {
    const response = await request.post('/api/webhooks/notifications', {
      headers: { 'Content-Type': 'application/json' },
      data: JSON.stringify({
        live: 'false',
        notificationItems: [],
      }),
    });
    expect(response.status()).toBe(500);
  });

  test('should reject malformed JSON', async ({ request }) => {
    const response = await request.post('/api/webhooks/notifications', {
      headers: { 'Content-Type': 'application/json' },
      data: '{invalid json}',
    });
    expect(response.status()).toBeGreaterThanOrEqual(400);
  });
});
