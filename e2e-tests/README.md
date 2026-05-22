# E2E Tests — Adyen Spring Online Payments

Local Playwright end-to-end tests for the checkout examples.

## Prerequisites

- Node.js 18+
- A running checkout-example instance on `http://localhost:8080`

## Quick Start

```bash
# Install dependencies
cd e2e-tests
npm install

# Install Playwright browsers
npx playwright install --with-deps

# Start the checkout-example app (in another terminal)
cd checkout-example
ADYEN_API_KEY=<key> ADYEN_MERCHANT_ACCOUNT=<account> ADYEN_CLIENT_KEY=<client_key> ADYEN_HMAC_KEY=<hmac_key> ./gradlew bootRun

# Run all tests
npm test

# Run tests for a specific browser
npm run test:chromium
npm run test:firefox
npm run test:webkit

# Run tests in headed mode (visible browser)
npm run test:headed

# Debug tests
npm run test:debug
```

## Viewing Reports

After running tests:

```bash
npm run report
```

This opens the HTML report in your browser.

## Test Structure

```
e2e-tests/
├── package.json              # Dependencies and scripts
├── playwright.config.ts      # Playwright configuration
├── tests/
│   └── checkout/
│       └── checkout.spec.ts  # Checkout flow tests
├── screenshots/              # Visual regression baseline images
├── test-results/             # Test output (gitignored)
└── playwright-report/        # HTML reports (gitignored)
```

## Test Coverage

| Test Suite | Description |
|---|---|
| Index Page | Verifies the landing page loads and displays payment method links |
| Drop-in Component | Tests navigation to and rendering of the drop-in payment UI |
| Card Payment | Tests card payment page navigation and form rendering |
| iDEAL Payment | Tests iDEAL payment method navigation |
| Responsive Design | Visual regression tests on mobile, tablet, and desktop viewports |

## Adding New Tests

1. Create a new `.spec.ts` file in `tests/` (or a subdirectory)
2. Use the Playwright `test` and `expect` APIs
3. Run `npx playwright test --update-snapshots` to generate baseline screenshots for visual tests

Example:

```typescript
import { test, expect } from '@playwright/test';

test('my new test', async ({ page }) => {
  await page.goto('/');
  await expect(page).toHaveTitle(/Adyen/i);
});
```

## Environment Variables

The checkout-example app requires these environment variables:

| Variable | Description |
|---|---|
| `ADYEN_API_KEY` | Your Adyen API key |
| `ADYEN_MERCHANT_ACCOUNT` | Your Adyen merchant account name |
| `ADYEN_CLIENT_KEY` | Your Adyen client key (for frontend components) |
| `ADYEN_HMAC_KEY` | HMAC key for webhook validation |

## JaCoCo Code Coverage

Each module includes JaCoCo for code coverage. After running unit tests:

```bash
cd <module-name>
./gradlew test
```

Coverage reports are generated at:
```
<module-name>/build/reports/jacoco/test/html/index.html
```

## CI/CD

Tests run automatically via GitHub Actions on pushes and PRs to `main` that modify `checkout-example/`. The workflow:

1. Builds the checkout-example with Gradle
2. Starts the app in a Docker container
3. Installs Playwright and runs all E2E tests
4. Uploads test reports and screenshots as artifacts
