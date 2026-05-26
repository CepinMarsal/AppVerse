package com.example.demo.testing;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class LoginEmptyInputTest {
    WebDriver driver;

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @Test
    public void testLoginEmptyInput() throws InterruptedException {
        driver.get("http://localhost:8080/login");
        
        WebElement button =
                driver.findElement(By.className("btn-primary"));

        button.click();
        Thread.sleep(3000);
        System.out.println("LOGIN INPUT KOSONG BERHASIL DIUJI");
    }

    @AfterEach
    public void close() {
        driver.quit();
    }
}