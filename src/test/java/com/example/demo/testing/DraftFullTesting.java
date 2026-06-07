package com.example.demo.testing;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DraftFullTesting {

private static WebDriver driver;

// ==========================
// SETUP
// ==========================

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
    Thread.sleep(3000);
}

// ==========================
// REGISTER TEST
// ==========================

@Test
@Order(1)
void testRegisterEmptyInput() throws Exception {

    driver.get("http://localhost:8080/register");

    driver.findElement(
            By.cssSelector("button[type='submit']")
    ).click();

    waitPage();

    System.out.println("✓ REGISTER INPUT KOSONG");
}

@Test
@Order(2)
void testDuplicateEmailRegister() throws Exception {

    driver.get("http://localhost:8080/register");

    driver.findElement(By.id("name"))
            .sendKeys("Cepin Marsal");

    driver.findElement(By.id("email"))
            .sendKeys("jul@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("12345");

    Thread.sleep(1000);

    driver.findElement(
            By.cssSelector("button[type='submit']")
    ).click();

    waitPage();

    System.out.println("✓ DUPLICATE EMAIL");
}

@Test
@Order(3)
void testDuplicateFingerprint() throws Exception {

    driver.get("http://localhost:8080/register");

    driver.findElement(By.id("name"))
            .sendKeys("User Baru");

    driver.findElement(By.id("email"))
            .sendKeys("baru@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("123");

    Thread.sleep(1000);

    driver.findElement(
            By.cssSelector("button[type='submit']")
    ).click();

    waitPage();

    System.out.println("✓ DUPLICATE FINGERPRINT");
}

@Test
@Order(4)
void testRegisterSuccess() throws Exception {

    driver.get("http://localhost:8080/register");

    driver.findElement(By.id("name"))
            .sendKeys("Cecep");

    driver.findElement(By.id("email"))
            .sendKeys("cecep@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("12345678");

    Thread.sleep(1000);

    driver.findElement(
            By.cssSelector("button[type='submit']")
    ).click();

    waitPage();

    System.out.println("✓ REGISTER SUCCESS");
}

// ==========================
// LOGIN TEST
// ==========================

@Test
@Order(5)
void testLoginEmptyInput() throws Exception {

    driver.get("http://localhost:8080/login");

    driver.findElement(
            By.className("btn-primary")
    ).click();

    waitPage();

    System.out.println("✓ LOGIN INPUT KOSONG");
}

@Test
@Order(6)
void testLoginFailed() throws Exception {

    driver.get("http://localhost:8080/login");

    driver.findElement(By.id("email"))
            .sendKeys("salah@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("salah123");

    driver.findElement(
            By.className("btn-primary")
    ).click();

    waitPage();

    System.out.println("✓ LOGIN GAGAL");
}

@Test
@Order(7)
void testWrongFingerprint() throws Exception {

    driver.get("http://localhost:8080/login");

    driver.findElement(By.id("email"))
            .sendKeys("jul@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("fingerprint_salah");

    driver.findElement(
            By.className("btn-primary")
    ).click();

    waitPage();

    System.out.println("✓ FINGERPRINT SALAH");
}

@Test
@Order(8)
void testLoginSuccess() throws Exception {

    driver.get("http://localhost:8080/login");

    driver.findElement(By.id("email"))
            .sendKeys("jul@gmail.com");

    driver.findElement(By.id("fingerprint"))
            .sendKeys("123");

    driver.findElement(
            By.className("btn-primary")
    ).click();

    waitPage();

    System.out.println("✓ LOGIN SUCCESS");
}

// ==========================
// FORE TESTING
// ==========================

@Test
@Order(9)
void testOpenFore() throws Exception {

    // GANTI INI
    driver.findElement(
            By.xpath("//*[@id=\"appsGrid\"]/a[1]")
    ).click();

    waitPage();

    System.out.println("✓ OPEN FORE");
}

@Test
@Order(10)
void testForeAddProductToCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[2]/button")
    ).click();
        driver.findElement(
                By.xpath("//*[@id=\"btnPersonalCartModal\"]")
        ).click();
    waitPage();

    System.out.println("✓ FORE ADD PRODUCT TO CART");
}

// ==========================
// MY CART
// ==========================

@Test
@Order(11)
void testOpenMyCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.id("nav-cart")
    ).click();

    waitPage();

    System.out.println("✓ OPEN MY CART");
}

@Test
@Order(12)
void testApplyVoucher() throws Exception {

    // GANTI INI
    Select voucher = new Select(
            driver.findElement(
                    By.id("cartVoucher")
            )
    );

    voucher.selectByIndex(2);

    waitPage();

    System.out.println("✓ APPLY VOUCHER");
}

@Test
@Order(13)
void testCheckoutCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.linkText("Confirm & Checkout")
    ).click();

    waitPage();

    System.out.println("✓ CHECKOUT CART");
}

// ==========================
// GROUP CART
// ==========================

@Test
@Order(14)
void testOpenGroupCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.id("nav-groupcart")
    ).click();

    waitPage();

    System.out.println("✓ OPEN GROUP CART");
}

@Test
@Order(15)
void testCreateGroupCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[1]/button")
    ).click();

    waitPage();

    System.out.println("✓ CREATE GROUP CART");
}

@Test
@Order(16)
void testInviteFriend() throws Exception {

// GANTI INI
    driver.findElement(
            By.xpath("//*[@id=\"mainContent\"]/div/div[1]/div[2]/div[2]")
    ).click();

    // GANTI INI
    driver.findElement(
            By.id("INVITE_EMAIL_FIELD")
    ).sendKeys("marsal@gmail.com");

    // GANTI INI
    driver.findElement(
            By.id("inviteEmail")
    ).click();

    driver.findElement(
            By.linkText("Undang")
    ).click();

    waitPage();

    System.out.println("✓ INVITE FRIEND");
}

@Test
@Order(17)
void testAddProductToGroupCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.xpath("//*[@id=\"groupCatalog\"]/div[1]/button")
    ).click();

    driver.findElement(
            By.xpath("//*[@id=\"btnGroupCartModal\"]")
    ).click();

    waitPage();

    System.out.println("✓ ADD PRODUCT TO GROUP CART");
}

@Test
@Order(18)
void testCheckoutGroupCart() throws Exception {

    // GANTI INI
    driver.findElement(
            By.linkText("Checkout Grup (Traktir Semua Anggota)")
    ).click();

    waitPage();

    System.out.println("✓ GROUP CART CHECKOUT");
}
}