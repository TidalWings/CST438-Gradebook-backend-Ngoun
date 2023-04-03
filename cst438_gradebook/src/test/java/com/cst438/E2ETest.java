package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.beans.Transient;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class E2ETest {
	public static final String CHROME_DRIVER_FILE_LOCATION = "/Users/warrenngoun/Downloads/chromedriver";
	public static final String URL = "http://localhost:3000";
    public static final int SLEEP_DURATION = 1000;
    public static final String SUCCESS_MESSAGE = "Assignment Added Successfully. Please Return to Home Page.";
    public static final String MISSING_MESSAGE = "Error: Missing Assignment Details";
    public static final String FAILURE_MESSAGE = "Error When Adding Assignment: Invalid Info";

    @Test
    public void correctAssignmentAdd() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(ops);

        try {
            WebElement webEle;
            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("add"));
            webEle.click();
            Thread.sleep(SLEEP_DURATION);
            
            webEle = driver.findElement(By.id("assignmentNameField"));
            String message = "Selenium Assignment 1";
            webEle.sendKeys(message);
            
            webEle = driver.findElement(By.id("dueDateField"));
            String date = "2022-12-23";
            webEle.sendKeys(date);
            
            webEle = driver.findElement(By.id("courseIDField"));
            String course = "999001";
            webEle.sendKeys(course);

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("submitButton"));
            webEle.click();

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("message"));
            String response = webEle.getText();
            assertEquals(response, SUCCESS_MESSAGE);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            driver.close();
            driver.quit();
        }
    }

    @Test
    public void invalidAssignmentAdd() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(ops);

        try {
            WebElement webEle;
            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("add"));
            webEle.click();
            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("assignmentNameField"));
            String message = "Selenium Assignment 1";
            webEle.sendKeys(message);

            webEle = driver.findElement(By.id("dueDateField"));
            String date = "2022-12-23";
            webEle.sendKeys(date);

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("submitButton"));
            webEle.click();

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("message"));
            String response = webEle.getText();
            assertEquals(response, MISSING_MESSAGE);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            driver.close();
            driver.quit();
        }
    }

    @Test
    public void missingAssignmentAdd() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
        ChromeOptions ops = new ChromeOptions();
        ops.addArguments("--remote-allow-origins=*");

        WebDriver driver = new ChromeDriver(ops);

        try {

            WebElement webEle;
            driver.get(URL);
            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("add"));
            webEle.click();
            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("assignmentNameField"));
            String message = "Selenium Assignment 1";
            webEle.sendKeys(message);

            webEle = driver.findElement(By.id("dueDateField"));
            String date = "2022-12-23";
            webEle.sendKeys(date);

            webEle = driver.findElement(By.id("courseIDField"));
            String course = "990001";
            webEle.sendKeys(course);

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("submitButton"));
            webEle.click();

            Thread.sleep(SLEEP_DURATION);

            webEle = driver.findElement(By.id("message"));
            String response = webEle.getText();
            assertEquals(response, FAILURE_MESSAGE);

        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        } finally {
            driver.close();
            driver.quit();
        }
    }
}
