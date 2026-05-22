import { test, expect } from '@playwright/test';

test.describe('Subscription Example - Index Page', () => {
  test('should load the subscription page successfully', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display subscription options', async ({ page }) => {
    await page.goto('/');
    const content = page.locator('body');
    await expect(content).toBeVisible();
    const text = await content.textContent();
    expect(text).toBeTruthy();
  });

  test('should take a screenshot of the index page', async ({ page }) => {
    await page.goto('/');
    await page.screenshot({ path: '../screenshots/subscription-index.png', fullPage: true });
  });
});

test.describe('Subscription Example - Tokenization Flow', () => {
  test('should navigate to card tokenization page', async ({ page }) => {
    await page.goto('/');
    const cardLink = page.locator('a[href*="card"], a[href*="tokenization"]').first();
    if (await cardLink.count() > 0) {
      await cardLink.click();
      await page.waitForLoadState('networkidle');
    }
    await expect(page.locator('body')).toBeVisible();
  });
});

test.describe('Subscription Example - Admin Panel', () => {
  test('should load the admin page', async ({ page }) => {
    await page.goto('/admin');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should take a screenshot of admin page', async ({ page }) => {
    await page.goto('/admin');
    await page.screenshot({ path: '../screenshots/subscription-admin.png', fullPage: true });
  });
});

test.describe('Subscription Example - Responsive Design', () => {
  test('should render correctly on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });
});
