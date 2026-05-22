import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 1 : undefined,
  reporter: [
    ['html'],
    ['list'],
  ],
  use: {
    baseURL: process.env.BASE_URL || 'http://localhost:8080',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'on-first-retry',
  },
  projects: [
    {
      name: 'checkout',
      testDir: './tests/checkout',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'giftcard',
      testDir: './tests/giftcard',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'paybylink',
      testDir: './tests/paybylink',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'subscription',
      testDir: './tests/subscription',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'authorisation-adjustment',
      testDir: './tests/authorisation-adjustment',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'chromium',
      testDir: './tests',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      testDir: './tests',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'webkit',
      testDir: './tests',
      use: { ...devices['Desktop Safari'] },
    },
  ],
  outputDir: './test-results',
});
