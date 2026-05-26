package com.example.demo.testing;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class RegisterSuccessTest {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testRegister() throws InterruptedException {
        driver.get("http://localhost:8080/register");

        WebElement name =
                driver.findElement(By.id("name"));
        WebElement email =
                driver.findElement(By.id("email"));
        WebElement fingerprint =
                driver.findElement(By.id("fingerprint"));
        WebElement button =
                driver.findElement(By.cssSelector("button[type='submit']"));

        name.sendKeys("Cepin Marsal");
        email.sendKeys("marsal@gmail.com");
        fingerprint.sendKeys("12345");
        Thread.sleep(1000);
        button.click();
        Thread.sleep(3000);
        System.out.println("REGISTER TEST BERHASIL");
    }

    @AfterEach
    public void close() {
        driver.quit();
    }
}