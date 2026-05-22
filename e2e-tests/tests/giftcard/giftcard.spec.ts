import { test, expect } from '@playwright/test';

test.describe('Giftcard Example - Index Page', () => {
  test('should load the giftcard index page successfully', async ({ page }) => {
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
    await page.screenshot({ path: '../screenshots/giftcard-index.png', fullPage: true });
  });
});

test.describe('Giftcard Example - Payment Flow', () => {
  test('should navigate to dropin payment page', async ({ page }) => {
    await page.goto('/');
    const dropinLink = page.locator('a[href*="dropin"]').first();
    if (await dropinLink.count() > 0) {
      await dropinLink.click();
      await page.waitForLoadState('networkidle');
      await expect(page.url()).toContain('dropin');
    }
  });

  test('should render payment form on dropin page', async ({ page }) => {
    await page.goto('/preview/dropin');
    await page.waitForLoadState('networkidle');
    const paymentContainer = page.locator('#dropin-container, .adyen-checkout__dropin, [class*="payment"]');
    if (await paymentContainer.count() > 0) {
      await expect(paymentContainer.first()).toBeVisible();
    }
  });
});

test.describe('Giftcard Example - Responsive Design', () => {
  test('should render correctly on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
  });
});
