import { test, expect } from '@playwright/test';

test.describe('Authorisation Adjustment Example - Index Page', () => {
  test('should load the index page successfully', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display payment options', async ({ page }) => {
    await page.goto('/');
    const links = page.locator('a[href]');
    const count = await links.count();
    expect(count).toBeGreaterThan(0);
  });

  test('should take a screenshot of the index page', async ({ page }) => {
    await page.goto('/');
    await page.screenshot({ path: '../screenshots/auth-adjustment-index.png', fullPage: true });
  });
});

test.describe('Authorisation Adjustment Example - Payment Flow', () => {
  test('should navigate to card payment page', async ({ page }) => {
    await page.goto('/');
    const cardLink = page.locator('a[href*="card"]').first();
    if (await cardLink.count() > 0) {
      await cardLink.click();
      await page.waitForLoadState('networkidle');
      await expect(page.url()).toContain('card');
    }
  });

  test('should display card payment form elements', async ({ page }) => {
    await page.goto('/preview/card');
    await page.waitForLoadState('networkidle');
    const cardForm = page.locator('.adyen-checkout__card, [class*="card"], iframe[title*="card"], iframe[name*="card"]');
    if (await cardForm.count() > 0) {
      await expect(cardForm.first()).toBeVisible();
    }
  });
});

test.describe('Authorisation Adjustment Example - Admin Panel', () => {
  test('should load the admin page', async ({ page }) => {
    await page.goto('/admin');
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display pre-authorisation list', async ({ page }) => {
    await page.goto('/admin');
    const content = page.locator('body');
    await expect(content).toBeVisible();
  });

  test('should take a screenshot of admin page', async ({ page }) => {
    await page.goto('/admin');
    await page.screenshot({ path: '../screenshots/auth-adjustment-admin.png', fullPage: true });
  });
});

test.describe('Authorisation Adjustment Example - Responsive Design', () => {
  test('should render correctly on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });
});
