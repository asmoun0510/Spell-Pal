package io.beyn.spell;

import java.util.Iterator;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.github.pemistahl.lingua.api.*;

import static com.github.pemistahl.lingua.api.Language.*;
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

    // start Scribensin headless mode ENG
    public WebDriver initilizeScribensENG() {
        ChromeOptions optionsHeadless = new ChromeOptions();
        optionsHeadless.addArguments("--start-maximized");
        optionsHeadless.addArguments("--disable-extensions");
        optionsHeadless.addArguments("--incognito");
        optionsHeadless.addArguments("--disable-popup-blocking");
        optionsHeadless.addArguments("--no-sandbox");
        optionsHeadless.addArguments("--ignore-certificate-errors");
        // optionsHeadless.addArguments("--headless");
        WebDriver driverScribensENG = new ChromeDriver(optionsHeadless);
        driverScribensENG.get("https://www.scribens.com/");
        return driverScribensENG;
    }


    // start Scribensin headless mode FR
    public WebDriver initilizeScribensFR() {
        ChromeOptions optionsHeadless = new ChromeOptions();
        optionsHeadless.addArguments("--start-maximized");
        optionsHeadless.addArguments("--disable-extensions");
        optionsHeadless.addArguments("--incognito");
        optionsHeadless.addArguments("--disable-popup-blocking");
        optionsHeadless.addArguments("--no-sandbox");
        optionsHeadless.addArguments("--ignore-certificate-errors");
        // optionsHeadless.addArguments("--headless");
        WebDriver driverScribensFR = new ChromeDriver(optionsHeadless);
        driverScribensFR.get("https://www.scribens.fr/");
        return driverScribensFR;
    }

    // return text elements of a web page
    public String getContentPage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        return element.getText();
    }

    // return HTML of a web page
    public String getSourcePage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 60);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        //System.out.println (driver.getPageSource()) ;
        return driver.getPageSource();
    }

    // parse String element 
    public Vector<Element> parseElements(String e, Vector<Element> list) {
        Element newElement;
        Boolean exists;
        String[] lines = e.split("[\\r\\n]+");

        // traitement
        // les fihier
        // action => les page
        for (String line : lines) {
            //  System.out.println(lines[i]);
            if (filterData(line)) {
                exists = false;
                // take itterator to begining of vector
                Iterator<Element> itr = list.iterator();
                //searcj if already exists 
                while (itr.hasNext() && exists == false) {
                    exists = itr.next().getText().equals(line);
                }
                /*
                ellemn 1

                zedzed
                zedzed


                zlkz,frefref


                 */
                if (!exists) {
                    //creat element
                    /*
                    state => waiting means not treated yet  (black) 100 100 100
                    => correct means treated and passed (Green) 60 200 80
                    => spell means treated and spell error  (Red) 250 85 85
                    => grammar means treated and grammar  error  (Orange) 250 130 50
                    suggestion => editting sugestions 
                     */
                    //    String language =


                    /* Java */


                    newElement = new Element(line, "waiting", "nothing", detctedLanguage(line));
                    //add element to vector
                    list.add(newElement);
                }
            }
        }
        return list;
    }

    private boolean filterData(String line) {
        if (line.length() > 0 && !line.matches("[0-9]+") && !line.contains("@") && !line.equals("FR") && !line.equals("EN") && !line.equals("AR"))
            return true;
        return false;
    }
    
    /*public String checkAlert (WebDriver driver) {
        if (alertAppeared(driver)) {
            Alert alert = driver.switchTo().alert();
            alert.getText();
        }
        return reult ;

    }*/

    public String getSugesstion(String e) {

        return Jsoup.parse(e).text();

    }

    public WebDriver changeToENG(WebDriver driver) {


        return driver;
    }


    public String detctedLanguage(String myString) {
        LanguageDetector detector = LanguageDetectorBuilder.fromLanguages(ENGLISH, FRENCH, ARABIC).build();
        Language detectedLanguage = detector.detectLanguageOf(myString);
        return detectedLanguage.toString();

    }

}
