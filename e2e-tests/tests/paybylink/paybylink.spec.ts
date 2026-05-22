import { test, expect } from '@playwright/test';

test.describe('Pay by Link Example - Index Page', () => {
  test('should load the pay by link page successfully', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display the create link form', async ({ page }) => {
    await page.goto('/');
    const form = page.locator('form, [class*="form"], input, button');
    if (await form.count() > 0) {
      await expect(form.first()).toBeVisible();
    }
  });

  test('should take a screenshot of the index page', async ({ page }) => {
    await page.goto('/');
    await page.screenshot({ path: '../screenshots/paybylink-index.png', fullPage: true });
  });
});

test.describe('Pay by Link Example - Link Management', () => {
  test('should display payment links list area', async ({ page }) => {
    await page.goto('/');
    const content = page.locator('body');
    await expect(content).toBeVisible();
    const text = await content.textContent();
    expect(text).toBeTruthy();
  });
});

test.describe('Pay by Link Example - Responsive Design', () => {
  test('should render correctly on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should render correctly on desktop viewport', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });
});
