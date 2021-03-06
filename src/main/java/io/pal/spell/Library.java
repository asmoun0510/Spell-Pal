package io.pal.spell;

import java.time.Duration;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * @author asmou
 */
public class Library {

    public Library() {

    }

    // start browser for user
    public WebDriver initilizeBrowser(String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--incognito");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://" + url);
        return driver;
    }


    // start browser based on selected language
    public WebDriver startBrowserChecker(String language) {
        System.out.println(language + "///////////////");
        ChromeOptions optionsHeadless = new ChromeOptions();
        optionsHeadless.addArguments("--start-maximized");
        optionsHeadless.addArguments("--disable-extensions");
        optionsHeadless.addArguments("--incognito");
        optionsHeadless.addArguments("--disable-popup-blocking");
        optionsHeadless.addArguments("--no-sandbox");
        optionsHeadless.addArguments("--ignore-certificate-errors");
        // optionsHeadless.addArguments("--headless");
        WebDriver driverSpellCheck = new ChromeDriver(optionsHeadless);
        switch (language) {
            case "EN" -> driverSpellCheck.get("https://www.scribens.com/");
            case "FR" -> driverSpellCheck.get("https://www.scribens.fr/");
            case "AR" -> driverSpellCheck.get("https://www.modakik.fr/");
        }
        switch (language) {
            case "EN", "FR" -> {
                WebDriverWait wait = new WebDriverWait(driverSpellCheck, Duration.ofSeconds(5));
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='Cor-RedButton' and contains(., 'Cancel')]"))).click();
                } catch (Exception ex) {
                   System.out.println("confirm pop up no found");
                }
            }
        }
        return driverSpellCheck;
    }

    // return text elements of a web page
    public String getContentPage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        return element.getText();
    }

    // return HTML of a web page
    public String getSourcePage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        //System.out.println (driver.getPageSource()) ;
        return driver.getPageSource();
    }

    // parse String element 
    public Vector<Element> parseElements(String e, Vector<Element> list) {
        Element newElement;
        boolean exists;
        String[] lines = e.split("[\\r\\n]+");

        // traitement
        // les fihier
        // action => les page
        for (String line : lines) {
            if (filterData(line)) {
                exists = false;
                for (int i = 0; i < list.size(); i++) {
                    if (line.equals(list.get(i).text)) {
                        exists = true;
                        continue;
                    }
                }

                if (!exists) {
                    newElement = new Element();
                    newElement.setText(line);
                    newElement.setState("waiting");
                    //add element to vector
                    list.add(newElement);
                }
            }
        }
        return list;
    }

    private boolean filterData(String line) {
        return line.length() > 0 && !line.matches("[0-9]+") && !line.contains("@");
    }
    
    /*public String checkAlert (WebDriver driver) {
        if (alertAppeared(driver)) {
            Alert alert = driver.switchTo().alert();
            alert.getText();
        }
        return reult ;

    }*/

    public String getTextHTML(String e) {
        return Jsoup.parse(e).text();
    }


}
