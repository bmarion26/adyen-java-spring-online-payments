const { test, expect } = require('@playwright/test');

// Navigation and page loading tests — these validate the UI layer
// without requiring Adyen credentials for payment processing.

test.describe('Homepage and Navigation', () => {
  test('should display the homepage with payment method links', async ({ page }) => {
    await page.goto('/');
    await expect(page.locator('h1')).toContainText('Select a demo');
    await expect(page.locator('.integration-list-item')).toHaveCount(6);
  });

  test('should navigate to Card preview page', async ({ page }) => {
    await page.goto('/');
    await page.click('a[href="/preview?type=card"]');
    await expect(page).toHaveURL(/\/preview\?type=card/);
    await expect(page.locator('.cart')).toBeVisible();
    await expect(page.locator('.cart-footer-amount')).toContainText('100.00');
  });

  test('should navigate to Drop-in preview page', async ({ page }) => {
    await page.goto('/');
    await page.click('a[href="/preview?type=dropin"]');
    await expect(page).toHaveURL(/\/preview\?type=dropin/);
    await expect(page.locator('.cart')).toBeVisible();
  });

  test('should navigate to iDEAL preview page', async ({ page }) => {
    await page.goto('/');
    await page.click('a[href="/preview?type=ideal"]');
    await expect(page).toHaveURL(/\/preview\?type=ideal/);
  });

  test('should navigate to SEPA preview page', async ({ page }) => {
    await page.goto('/');
    await page.click('a[href="/preview?type=sepa"]');
    await expect(page).toHaveURL(/\/preview\?type=sepa/);
  });

  test('should navigate to Klarna preview page', async ({ page }) => {
    await page.goto('/');
    await page.click('a[href="/preview?type=klarna"]');
    await expect(page).toHaveURL(/\/preview\?type=klarna/);
  });
});

test.describe('Preview Page', () => {
  test('should show cart items on preview page', async ({ page }) => {
    await page.goto('/preview?type=card');
    await expect(page.locator('.order-summary-list-list-item')).toHaveCount(2);
    await expect(page.locator('text=Sunglasses')).toBeVisible();
    await expect(page.locator('text=Headphones')).toBeVisible();
  });

  test('should have Continue to checkout button linking to correct type', async ({ page }) => {
    await page.goto('/preview?type=card');
    const checkoutLink = page.locator('a[href="/checkout/card"]');
    await expect(checkoutLink).toBeVisible();
  });

  test('should navigate from preview to checkout', async ({ page }) => {
    await page.goto('/preview?type=card');
    await page.click('a[href="/checkout/card"]');
    await expect(page).toHaveURL(/\/checkout\/card/);
  });
});

test.describe('Result Pages', () => {
  test('should display success result page', async ({ page }) => {
    await page.goto('/result/success');
    await expect(page.locator('.status-message')).toContainText('Your order has been successfully placed');
    await expect(page.locator('a.button')).toContainText('Return Home');
  });

  test('should display failed result page', async ({ page }) => {
    await page.goto('/result/failed');
    await expect(page.locator('.status-message')).toContainText('The payment was refused');
  });

  test('should display error result page', async ({ page }) => {
    await page.goto('/result/error');
    await expect(page.locator('.status-message')).toContainText('Error');
  });

  test('should display pending result page', async ({ page }) => {
    await page.goto('/result/pending');
    await expect(page.locator('.status-message')).toContainText('Payment completion pending');
  });

  test('should navigate back to home from result page', async ({ page }) => {
    await page.goto('/result/success');
    await page.click('a.button');
    await expect(page).toHaveURL('/');
  });
});
