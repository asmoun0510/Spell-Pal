
import java.util.Vector;
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
 *
 * @author asmou
 */
public class Library {

    public Library() {

    }

    // start browser for user
    public WebDriver initilizeBrowser(WebDriver driver, String url) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-extensions");
        options.addArguments("--incognito");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--no-sandbox");
        options.addArguments("--ignore-certificate-errors");
        driver = new ChromeDriver(options);
        driver.get("https://" + url);
        return driver;
    }

    // start scribensin headless mode
    public WebDriver initilizeScribens(WebDriver driverScribens) {
        ChromeOptions optionsHeadless = new ChromeOptions();
        optionsHeadless.addArguments("--start-maximized");
        optionsHeadless.addArguments("--disable-extensions");
        optionsHeadless.addArguments("--incognito");
        optionsHeadless.addArguments("--disable-popup-blocking");
        optionsHeadless.addArguments("--no-sandbox");
        optionsHeadless.addArguments("--ignore-certificate-errors");
        // optionsHeadless.addArguments("--headless");
        driverScribens = new ChromeDriver(optionsHeadless);
        driverScribens.get("https://www.scribens.com/");
        return driverScribens;
    }

    // return text elements of a web page
    public String getContentPage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        //System.out.println(element.getText());
        return element.getText();
    }

    // return HTML of a web page
    public String getSourcePage(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("body")));
        //System.out.println (driver.getPageSource()) ;
        return driver.getPageSource();
    }

    // parse String element 
    public Vector<Element> parseElements(String e) {
        Vector<Element> v = new Vector<Element>();
        String[] lines = e.split(System.getProperty("line.separator"));

        return v;
    }

}
