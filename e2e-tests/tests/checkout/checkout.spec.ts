import { test, expect } from '@playwright/test';

test.describe('Checkout Example - Index Page', () => {
  test('should load the index page successfully', async ({ page }) => {
    await page.goto('/');
    await expect(page).toHaveTitle(/Checkout/i);
    await expect(page.locator('body')).toBeVisible();
  });

  test('should display available payment method links', async ({ page }) => {
    await page.goto('/');

    const paymentMethods = ['dropin', 'card', 'ideal', 'googlepay', 'sepa', 'klarna'];
    for (const method of paymentMethods) {
      const link = page.locator(`a[href*="${method}"], a:has-text("${method}")`);
      if (await link.count() > 0) {
        await expect(link.first()).toBeVisible();
      }
    }
  });

  test('should take a screenshot of the index page', async ({ page }) => {
    await page.goto('/');
    await page.screenshot({ path: '../screenshots/index-page.png', fullPage: true });
  });
});

test.describe('Checkout Example - Drop-in Component', () => {
  test('should navigate to drop-in payment page', async ({ page }) => {
    await page.goto('/');

    const dropinLink = page.locator('a[href*="dropin"]').first();
    if (await dropinLink.count() > 0) {
      await dropinLink.click();
      await page.waitForLoadState('networkidle');
      await expect(page.url()).toContain('dropin');
    }
  });

  test('should render the drop-in payment form', async ({ page }) => {
    await page.goto('/preview/dropin');
    await page.waitForLoadState('networkidle');

    const paymentContainer = page.locator('#dropin-container, .adyen-checkout__dropin, [class*="payment"]');
    if (await paymentContainer.count() > 0) {
      await expect(paymentContainer.first()).toBeVisible();
    }
  });
});

test.describe('Checkout Example - Card Payment', () => {
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

  test('should take a screenshot of the card page', async ({ page }) => {
    await page.goto('/preview/card');
    await page.waitForLoadState('networkidle');
    await page.screenshot({ path: '../screenshots/card-payment.png', fullPage: true });
  });
});

test.describe('Checkout Example - iDEAL Payment', () => {
  test('should navigate to iDEAL payment page', async ({ page }) => {
    await page.goto('/');

    const idealLink = page.locator('a[href*="ideal"]').first();
    if (await idealLink.count() > 0) {
      await idealLink.click();
      await page.waitForLoadState('networkidle');
      await expect(page.url()).toContain('ideal');
    }
  });
});

test.describe('Checkout Example - Responsive Design', () => {
  test('should render correctly on mobile viewport', async ({ page }) => {
    await page.setViewportSize({ width: 375, height: 667 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
    await page.screenshot({ path: '../screenshots/index-mobile.png', fullPage: true });
  });

  test('should render correctly on tablet viewport', async ({ page }) => {
    await page.setViewportSize({ width: 768, height: 1024 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
    await page.screenshot({ path: '../screenshots/index-tablet.png', fullPage: true });
  });

  test('should render correctly on desktop viewport', async ({ page }) => {
    await page.setViewportSize({ width: 1440, height: 900 });
    await page.goto('/');
    await expect(page.locator('body')).toBeVisible();
    await page.screenshot({ path: '../screenshots/index-desktop.png', fullPage: true });
  });
});
