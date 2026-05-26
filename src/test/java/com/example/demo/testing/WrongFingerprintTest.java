package com.example.demo.testing;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WrongFingerprintTest {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testWrongFingerprint() throws InterruptedException {
        driver.get("http://localhost:8080/login");
        
        WebElement email =
                driver.findElement(By.id("email"));
        WebElement fingerprint =
                driver.findElement(By.id("fingerprint"));
        WebElement button =
                driver.findElement(By.className("btn-primary"));

        email.sendKeys("jul@gmail.com");
        fingerprint.sendKeys("fingerprint_salah");
        button.click();
        Thread.sleep(3000);
        System.out.println("FINGERPRINT SALAH BERHASIL DIUJI");
    }

    @AfterEach
    public void close() {
        driver.quit();
    }
}