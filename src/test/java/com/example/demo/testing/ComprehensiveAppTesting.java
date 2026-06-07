package com.example.demo.testing;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ComprehensiveAppTesting {
    private static WebDriver driver;
    private static final String BASE_URL = "http://localhost:8080";

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterAll
    static void close() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void waitPage() throws Exception {
        Thread.sleep(2000);
    }

    private void waitShort() throws Exception {
        Thread.sleep(1000);
    }

    private void waitForElementVisible(By locator, int secondsTimeout) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(secondsTimeout));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            System.out.println("DEBUG: Element found and visible: " + locator);
        } catch (Exception e) {
            System.out.println("DEBUG: Element NOT found after " + secondsTimeout + " seconds: " + locator);
            throw e;
        }
    }

    private void acceptAlertIfPresent() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            System.out.println("DEBUG: Accepted unexpected alert");
        } catch (Exception ignored) {
            // No alert present
        }
    }

    private void openApp(String appName) throws Exception {
        System.out.println("DEBUG: Looking for app: " + appName);

        waitForElementVisible(By.id("appsGrid"), 20);
        waitPage();
        
        java.util.List<WebElement> appLinks = driver.findElements(By.cssSelector("a.app-tile"));
        System.out.println("DEBUG: Found " + appLinks.size() + " app tiles");
        
        if (appLinks.isEmpty()) {
            System.out.println("DEBUG: No app tiles found! Checking page source...");
            String pageSource = driver.getPageSource();
            if (pageSource.contains("app-tile")) {
                System.out.println("DEBUG: Page contains 'app-tile' class");
            } else {
                System.out.println("DEBUG: Page does NOT contain 'app-tile' class");
            }
            throw new Exception("No app tiles found on dashboard");
        }
        
        for (int i = 0; i < appLinks.size(); i++) {
            try {
                WebElement link = appLinks.get(i);
                WebElement nameElement = link.findElement(By.className("app-name"));
                String appNameText = nameElement.getText().trim();
                System.out.println("DEBUG: [" + i + "] App found: '" + appNameText + "'");
                
                if (appNameText.equalsIgnoreCase(appName)) {
                    System.out.println("DEBUG: Clicking on " + appName);
                    link.click();
                    waitPage();
                    
                    Thread.sleep(3000);
                    System.out.println("DEBUG: App page loaded, URL: " + driver.getCurrentUrl());
                    return;
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Error with app tile " + i + ": " + e.getMessage());
            }
        }
        throw new Exception("App '" + appName + "' not found in " + appLinks.size() + " tiles");
    }

    // 1. REGISTER & LOGIN TESTING
    @Test
    @Order(1)
    @DisplayName("Test 1: Register dengan input kosong")
    void testRegisterEmptyInput() throws Exception {
        acceptAlertIfPresent();
        driver.get(BASE_URL + "/register");
        waitPage();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        acceptAlertIfPresent();
        System.out.println("Test 1: Register dengan input kosong - PASSED");
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: Register dengan email duplikat")
    void testDuplicateEmailRegister() throws Exception {
        acceptAlertIfPresent();
        driver.get(BASE_URL + "/register");
        waitPage();
        driver.findElement(By.id("name")).sendKeys("User Duplikat");
        driver.findElement(By.id("email")).sendKeys("jul@gmail.com");
        driver.findElement(By.id("fingerprint")).sendKeys("12345678");
        waitShort();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        acceptAlertIfPresent();
        System.out.println("Test 2: Register dengan email duplikat - PASSED");
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: Register dengan fingerprint duplikat")
    void testDuplicateFingerprintRegister() throws Exception {
        acceptAlertIfPresent();
        driver.get(BASE_URL + "/register");
        waitPage();
        driver.findElement(By.id("name")).sendKeys("User Duplikat");
        driver.findElement(By.id("email")).sendKeys("userbaru@gmail.com");
        driver.findElement(By.id("fingerprint")).sendKeys("123");
        waitShort();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        acceptAlertIfPresent();
        System.out.println("Test 3: Register dengan fingerprint duplikat - PASSED");
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: Register sukses")
    void testRegisterSuccess() throws Exception {
        acceptAlertIfPresent();
        driver.get(BASE_URL + "/register");
        waitPage();
        driver.findElement(By.id("name")).sendKeys("Cecep Handoko");
        driver.findElement(By.id("email")).sendKeys("cecep@gmail.com");
        driver.findElement(By.id("fingerprint")).sendKeys("12345678");
        waitShort();
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        acceptAlertIfPresent();
        System.out.println("Test 4: Register sukses - PASSED");
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: Login sukses")
    void testLoginSuccess() throws Exception {
        acceptAlertIfPresent();
        driver.get(BASE_URL + "/login");
        waitPage();
        driver.findElement(By.id("email")).sendKeys("cecep@gmail.com");
        driver.findElement(By.id("fingerprint")).sendKeys("12345678");
        waitShort();
        driver.findElement(By.className("btn-primary")).click();
        acceptAlertIfPresent();
        System.out.println("DEBUG: Waiting for dashboard after login...");
        try {
            waitForElementVisible(By.id("userName"), 20);
            System.out.println("DEBUG: Dashboard loaded! User logged in.");
            waitForElementVisible(By.id("appsGrid"), 20);
            System.out.println("DEBUG: Apps grid visible. Ready to open apps.");
        } catch (Exception e) {
            System.out.println("DEBUG: Dashboard load failed: " + e.getMessage());
            System.out.println("DEBUG: Current URL: " + driver.getCurrentUrl());
            if (driver.getCurrentUrl().contains("/login")) {
                System.out.println("ERROR: Still at login page after attempting login!");
                throw new Exception("Login failed - still at login page after 20 seconds");
            }
            throw e;
        }
        System.out.println("✓ Test 5: Login sukses - PASSED");
    }

    // 2. SHOPEE TESTING
    @Test
    @Order(6)
    @DisplayName("Test 6: Buka aplikasi Shopee")
    void testOpenShopee() throws Exception {
        // Verifikasi sudah di dashboard
        System.out.println("DEBUG: Current URL: " + driver.getCurrentUrl());
        if (!driver.getCurrentUrl().contains("/dashboard")) {
            System.out.println("DEBUG: Not at dashboard, navigating...");
            driver.get(BASE_URL + "/dashboard");
            waitForElementVisible(By.id("appsGrid"), 20);
        }
        openApp("Shopee");
        System.out.println("Test 6: Buka aplikasi Shopee - PASSED");
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: Add product ke Shopee cart")
    void testShopeeAddProduct() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[2]/button")).click();
        waitShort();
        driver.findElement(By.xpath("//*[@id=\"btnPersonalCartModal\"]")).click();
        waitPage();
        System.out.println("Test 7: Add product ke Shopee cart - PASSED");
    }

    @Test
    @Order(8)
    @DisplayName("Test 8: Input destination address untuk Shopee")
    void testShopeeAddDestinationAddress() throws Exception {
        driver.findElement(By.id("nav-cart")).click();
        waitPage();
        WebElement destInput = driver.findElement(By.id("cartDestination"));
        destInput.clear();
        destInput.sendKeys("Bandung");
        waitShort();
        System.out.println("Test 8: Input destination address untuk Shopee - PASSED");
    }

    @Test
    @Order(9)
    @DisplayName("Test 9: Apply voucher Shopee")
    void testShopeeApplyVoucher() throws Exception {
        Select voucher = new Select(driver.findElement(By.id("cartVoucher")));
        int optionCount = voucher.getOptions().size();
        if (optionCount > 1) {
            voucher.selectByIndex(1);
        }
        waitShort();
        System.out.println("Test 9: Apply voucher Shopee - PASSED");
    }

    @Test
    @Order(10)
    @DisplayName("Test 10: Checkout Shopee")
    void testShopeeCheckout() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[2]/button")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 10: Checkout Shopee - PASSED");
    }

    // 3. FORE TESTING
    @Test
    @Order(11)
    @DisplayName("Test 11: Buka aplikasi Fore")
    void testOpenFore() throws Exception {
        System.out.println("DEBUG: Current URL: " + driver.getCurrentUrl());
        if (!driver.getCurrentUrl().contains("/dashboard")) {
            System.out.println("DEBUG: Not at dashboard, navigating...");
            driver.get(BASE_URL + "/dashboard");
            waitForElementVisible(By.id("appsGrid"), 20);
        }
        openApp("Fore");
        System.out.println("Test 11: Buka aplikasi Fore - PASSED");
    }

    @Test
    @Order(12)
    @DisplayName("Test 12: Add product ke Fore cart")
    void testForeAddProduct() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[2]/button")).click();
        waitShort();
        driver.findElement(By.xpath("//*[@id=\"btnPersonalCartModal\"]")).click();
        waitPage();
        System.out.println("Test 12: Add product ke Fore cart - PASSED");
    }

    @Test
    @Order(13)
    @DisplayName("Test 13: Masuk ke Cart Fore")
    void testForeCart() throws Exception {
        driver.findElement(By.id("nav-cart")).click();
        waitPage();
    }

    @Test
    @Order(14)
    @DisplayName("Test 14: Checkout Fore langsung dengan voucher")
    void testForeCheckout() throws Exception {
        Select voucher = new Select(driver.findElement(By.id("cartVoucher")));
        int optionCount = voucher.getOptions().size();
        if (optionCount > 1) {
            voucher.selectByIndex(1);
            waitShort();
        }
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[2]/button")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 14: Checkout Fore langsung dengan voucher - PASSED");
    }

    @Test
    @Order(15)
    @DisplayName("Test 15: Open Group Cart")
    void testOpenGroupCart() throws Exception {
        driver.findElement(By.id("nav-groupcart")).click();
        waitPage();
        System.out.println("Test 15: Open Group Cart - PASSED");
    }

    @Test
    @Order(16)
    @DisplayName("Test 16: Create Group Cart")
    void testCreateGroupCart() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[1]/button")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 16: Create Group Cart - PASSED");
    }

    @Test
    @Order(17)
    @DisplayName("Test 17: Invite Friend to Group Cart")
    void testInviteFriend() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[2]/div[2]")).click();
        acceptAlertIfPresent();
        driver.findElement(By.id("inviteEmail")).sendKeys("marsal@gmail.com");
        acceptAlertIfPresent();
        driver.findElement(By.xpath("//*[@id=\"grupDetail\"]/div[1]/div[3]/button")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 17: Invite Friend to Group Cart - PASSED");
    }

    @Test
    @Order(18)
    @DisplayName("Test 18: Add Product To Group Cart")
    void testAddProductToGroupCart() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"groupCatalog\"]/div[1]/button")).click();
        acceptAlertIfPresent();
        driver.findElement(By.xpath("//*[@id=\"btnGroupCartModal\"]")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 18: Add Product To Group Cart - PASSED");
    }

    @Test
    @Order(19)
    @DisplayName("Test 19: Checkout Group Cart")
    void testCheckoutGroupCart() throws Exception {
        driver.findElement(By.xpath("//*[@id=\"grupDetail\"]/button")).click();
        acceptAlertIfPresent();
        waitPage();
        System.out.println("Test 19: Checkout Group Cart - PASSED");
    }

    // 4. GOJEK TESTING
    @Test
    @Order(20)
    @DisplayName("Test 20: Buka aplikasi Gojek")
    void testOpenGojek() throws Exception {
        System.out.println("DEBUG: Current URL: " + driver.getCurrentUrl());
        if (!driver.getCurrentUrl().contains("/dashboard")) {
            System.out.println("DEBUG: Not at dashboard, navigating...");
            driver.get(BASE_URL + "/dashboard");
            waitForElementVisible(By.id("appsGrid"), 20);
        }
        openApp("Gojek");
        System.out.println("Test 20: Buka aplikasi Gojek - PASSED");
    }

    @Test
    @Order(21)
    @DisplayName("Test 21: Pilih layanan Gojek")
    void testGojekSelectService() throws Exception {
        WebElement pickupInput = driver.findElement(By.id("pickup"));
        pickupInput.sendKeys("Kampus");
        waitShort();
        WebElement destInput = driver.findElement(By.id("dest"));
        destInput.sendKeys("Rumah");
        waitShort();
        System.out.println("Test 21: Pilih layanan Gojek - PASSED");
    }

    @Test
    @Order(22)
    @DisplayName("Test 22: Confirm order Gojek")
    void testGojekConfirmOrder() throws Exception {
        Select voucher = new Select(driver.findElement(By.xpath("//*[@id=\"gVoucher\"]")));
        int optionCount = voucher.getOptions().size();
        if (optionCount > 1) {
            voucher.selectByIndex(1);
            waitShort();
        }

        java.util.List<WebElement> buttons = driver.findElements(By.xpath("//*[@id=\"gojekOrderCard\"]/button"));
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
        acceptAlertIfPresent();
        waitPage();

        System.out.println("Test 22: Confirm order Gojek - PASSED");
    }
}