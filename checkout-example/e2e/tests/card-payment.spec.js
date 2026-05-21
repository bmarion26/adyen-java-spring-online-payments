const { test, expect } = require('@playwright/test');

// Card payment e2e tests using Adyen test cards.
// These tests require ADYEN_* environment variables and a running application.
// Official Adyen test cards: https://docs.adyen.com/development-resources/testing/test-card-numbers/

// Helper to fill in card details within the Adyen secured iframe
async function fillCardNumber(page, cardNumber) {
  const cardNumberFrame = page.frameLocator('iframe[title="Iframe for secured card number"]');
  await cardNumberFrame.locator('input[data-fieldtype="encryptedCardNumber"]').fill(cardNumber);
}

async function fillExpiryDate(page, expiry) {
  const expiryFrame = page.frameLocator('iframe[title="Iframe for secured card expiry date"]');
  await expiryFrame.locator('input[data-fieldtype="encryptedExpiryDate"]').fill(expiry);
}

async function fillCvc(page, cvc) {
  const cvcFrame = page.frameLocator('iframe[title="Iframe for secured card security code"]');
  await cvcFrame.locator('input[data-fieldtype="encryptedSecurityCode"]').fill(cvc);
}

async function fillHolderName(page, name) {
  await page.locator('input[name="holderName"]').fill(name);
}

async function navigateToCardCheckout(page) {
  await page.goto('/preview?type=card');
  await page.click('a[href="/checkout/card"]');
  await page.waitForURL(/\/checkout\/card/);
  // Wait for the Adyen component to mount
  await page.waitForSelector('#component-container iframe', { timeout: 15000 });
}

test.describe('Card Payment - Happy Path', () => {
  test('should complete payment with valid Visa test card', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Shopper');
    await fillCardNumber(page, '4111 1111 1111 1111');
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    // Click pay button
    await page.locator('button.adyen-checkout__button--pay').click();

    // Should redirect to success
    await page.waitForURL(/\/result\/success/, { timeout: 30000 });
    await expect(page.locator('.status-message')).toContainText('Your order has been successfully placed');
  });

  test('should complete payment with valid Mastercard test card', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Shopper');
    await fillCardNumber(page, '5454 5454 5454 5454');
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    await page.waitForURL(/\/result\/success/, { timeout: 30000 });
    await expect(page.locator('.status-message')).toContainText('Your order has been successfully placed');
  });
});

test.describe('Card Payment - Refused Card', () => {
  test('should show failed page when using refused test card', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Refused');
    // Adyen test card that triggers REFUSED
    await fillCardNumber(page, '4000 0200 0000 0000');
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    // Should redirect to failed result
    await page.waitForURL(/\/result\/failed/, { timeout: 30000 });
    await expect(page.locator('.status-message')).toContainText('The payment was refused');
  });
});

test.describe('Card Payment - Invalid Input Validation', () => {
  test('should show validation error for empty card number', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Shopper');
    // Leave card number empty, fill other fields
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    // Adyen shows inline validation error — payment should not proceed
    const cardNumberFrame = page.frameLocator('iframe[title="Iframe for secured card number"]');
    await expect(cardNumberFrame.locator('.adyen-checkout-input--error')).toBeVisible({ timeout: 5000 });
  });

  test('should show validation error for empty holder name', async ({ page }) => {
    await navigateToCardCheckout(page);

    // Leave holder name empty
    await fillCardNumber(page, '4111 1111 1111 1111');
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    // The holderName field should show validation error (it's required)
    await expect(page.locator('.adyen-checkout-input--error[name="holderName"]')).toBeVisible({ timeout: 5000 });
  });

  test('should show validation error for expired card', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Shopper');
    await fillCardNumber(page, '4111 1111 1111 1111');
    await fillExpiryDate(page, '01/20'); // Past date
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    const expiryFrame = page.frameLocator('iframe[title="Iframe for secured card expiry date"]');
    await expect(expiryFrame.locator('.adyen-checkout-input--error')).toBeVisible({ timeout: 5000 });
  });

  test('should show validation error for incomplete card number', async ({ page }) => {
    await navigateToCardCheckout(page);

    await fillHolderName(page, 'Test Shopper');
    await fillCardNumber(page, '4111 1111'); // Incomplete
    await fillExpiryDate(page, '03/30');
    await fillCvc(page, '737');

    await page.locator('button.adyen-checkout__button--pay').click();

    const cardNumberFrame = page.frameLocator('iframe[title="Iframe for secured card number"]');
    await expect(cardNumberFrame.locator('.adyen-checkout-input--error')).toBeVisible({ timeout: 5000 });
  });
});

test.describe('Card Payment - Checkout Flow Navigation', () => {
  test('should load card checkout page with Adyen component', async ({ page }) => {
    await page.goto('/checkout/card');
    await page.waitForSelector('#component-container', { timeout: 15000 });
    await expect(page.locator('#component-container')).toBeVisible();
  });

  test('should show pay button after component loads', async ({ page }) => {
    await navigateToCardCheckout(page);
    await expect(page.locator('button.adyen-checkout__button--pay')).toBeVisible();
  });
});
