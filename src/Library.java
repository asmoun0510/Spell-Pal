
import java.util.Iterator;
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
    public Vector<Element> parseElements(String e, Vector<Element> list) {
        Element newElement;
        Boolean exists;
        String[] lines = e.split(System.getProperty("line.separator"));
        for (int i = 0; i < lines.length; i++) {
            System.out.println(lines[i]);
            if (lines[i].length() > 0) {
                exists = false;
                // take itterator to begining of vector
                Iterator<Element> itr = list.iterator();
                //searcj if already exists 
                while (itr.hasNext() && exists == false) {
                    exists = itr.next().getText().equals(lines[i]);
                }

                if (!exists) {
                    System.out.println("not ecist");
                    //creat element
                    /*
                    state => waiting means not treated yet  (black) 100 100 100
                          => correct means treated and passed (Green) 60 200 80
                          => spell means treated and spell error  (Red) 250 85 85
                          => grammar means treated and grammar  error  (Orange) 250 130 50
                    
                    suggestion => editting sugestions 
                     */
                    newElement = new Element(lines[i], "waiting", "nothing");
                    //add element to vector
                    list.add(newElement);
                }

            }
        }
        return list;
    }

}
