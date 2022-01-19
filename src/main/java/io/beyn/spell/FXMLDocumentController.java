package io.beyn.spell;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

import io.github.bonigarcia.wdm.WebDriverManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * FXML Controller class
 *
 * @author asmou
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Button buttonRun;
    @FXML
    private Label labelParsed;
    @FXML
    private Label labelCorrect;
    @FXML
    private Label labelError;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow areaResult;

    Library lib = new Library();

    Vector<Element> webElements = new Vector<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
// driverReverso, driverBrowser;
           WebDriverManager.chromedriver().browserVersion("97.0.4692.71").setup();
        labelCorrect.setText("Nombre Correcte : 0");
        labelError.setText("Nombre Erreur : 0");
        labelParsed.setText("Nombre Total : 0");

        /*
        WebElement l= driver.findElement(By.tagName("body"));
      String p = l.getText();
      System.out.println("Page Source is : " + p);S
         */
    }

    @FXML
    private void Event(ActionEvent event) {
        if (event.getSource() == buttonRun) {
            Thread threadBrowse = new Thread(() -> {
                //  start browser for user
                String newPage, contentPage;
                WebDriver driverBrowser = lib.initilizeBrowser("www.google.com");
                String initialPage = lib.getSourcePage(driverBrowser);
                Text text;
                while (true) {
                    try {
                        Thread.sleep(300);
                        newPage = lib.getSourcePage(driverBrowser);
                        if (!initialPage.equals(newPage)) {
                            contentPage = lib.getContentPage(driverBrowser);
                            webElements = lib.parseElements(contentPage, webElements);
                            //System.out.print(webElements.size() + "rrrr");
                            //clear Gui
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    areaResult.getChildren().clear();
                                }
                            });
                            int numTotal = webElements.size();
                            int numCorecte = 0, numError = 0;
                            for (int w = 0; w < webElements.size(); w++) {
                                String myString;

                                myString = "\n " + webElements.elementAt(w).getText();
                                if (webElements.elementAt(w).getState().equals("error")) {
                                    myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                }
                                text = new Text(myString);
                                text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                /*
                      state => waiting means not treated yet  (black) 100 100 100
                      => correct means treated and passed (Green) 60 200 80
                      => spell means treated and spell error  (Red) 250 85 85

                suggestion => editting sugestions
                                 */
                                if (webElements.elementAt(w).getState().equals("waiting")) {
                                    text.setFill(Color.rgb(162, 136, 211));
                                } else if (webElements.elementAt(w).getState().equals("correct")) {
                                    text.setFill(Color.rgb(36, 221, 123));
                                    numCorecte++;
                                } else if (webElements.elementAt(w).getState().equals("wrong")) {
                                    text.setFill(Color.rgb(255, 93, 93));
                                    numError++;
                                }
                                Text tempText = text;
                                //update new Gui
                                String newLabelCorrect = String.valueOf(numCorecte);
                                String newLabelError = String.valueOf(numError);
                                String newLabelTotal = String.valueOf(numTotal);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        areaResult.getChildren().add(tempText);
                                        labelCorrect.setText("Nombre Correcte : " + newLabelCorrect);
                                        labelError.setText("Nombre Erreurs : " + newLabelError);
                                        labelParsed.setText("Nombre Total :" + newLabelTotal);
                                    }
                                });

                            }
                            initialPage = newPage;
                            //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                        } else {

                        }
                    } catch (InterruptedException ex) {
                       // Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            });

            //  start browser and visit Reverso
            Thread threadReverso = new Thread(() -> {
                WebDriver driverReverso = lib.initilizeReverso();
                WebDriverWait wait = new WebDriverWait(driverReverso, 50);
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));

                Text text;
                while (true) {
                    try {

                        for (int j = 0; j < webElements.size(); j++) {
                            if (webElements.get(j).state.equals("waiting")) {

                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnClear"))).click();
                                Thread.sleep(300);
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startText"))).sendKeys(webElements.get(j).text);
                                Thread.sleep(300);
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSpell"))).click();
                                Thread.sleep(300);
                                //  wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSpell"))).click();
                                List<WebElement> mistakes;
                                try {
                                    mistakes = driverReverso.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                                } catch (Exception e) {
                                    mistakes = null;
                                    System.out.print("error");
                                }
                                if (!mistakes.isEmpty()) {

                                    String suggestion = "";
                                    for (int m = 0; m < mistakes.size(); m++) {
                                        suggestion = suggestion + lib.getSugesstion(mistakes.get(m).getAttribute("tooltip"));

                                    }
                                    webElements.get(j).setState("wrong");
                                    webElements.get(j).setSuggest(suggestion);
                                } else {
                                    webElements.get(j).setState("correct");
                                }

                            }
                            //System.out.print(webElements.size() + "rrrr");
                            //clear Gui
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    areaResult.getChildren().clear();
                                }
                            });
                            int numTotal = webElements.size();
                            int numCorecte = 0, numError = 0;
                            for (int w = 0; w < webElements.size(); w++) {

                                String myString;
                                myString = "\n " + webElements.elementAt(w).getText();
                                if (webElements.elementAt(w).getState().equals("wrong")) {
                                    myString = myString + " => " + webElements.elementAt(w).getSuggest();

                                }
                                text = new Text(myString);
                                text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                /*
                            /*
                state => waiting means not treated yet  (black) 100 100 100
                      => correct means treated and passed (Green) 60 200 80
                      => spell means treated and spell error  (Red) 250 85 85
                      => grammar means treated and grammar  error  (Orange) 250 130 50

                suggestion => editting sugestions
                                 */
                                if (webElements.elementAt(w).getState().equals("waiting")) {
                                    text.setFill(Color.rgb(162, 136, 211));
                                } else if (webElements.elementAt(w).getState().equals("correct")) {
                                    text.setFill(Color.rgb(36, 221, 123));
                                    numCorecte++;
                                } else if (webElements.elementAt(w).getState().equals("wrong")) {
                                    text.setFill(Color.rgb(255, 93, 93));
                                    numError++;
                                }
                                Text tempText = text;
                                //update new Gui
                                String newLabelCorrect = String.valueOf(numCorecte);
                                String newLabelError = String.valueOf(numError);
                                String newLabelTotal = String.valueOf(numTotal);
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        areaResult.getChildren().add(tempText);
                                        labelCorrect.setText("Nombre Correcte : " + newLabelCorrect);
                                        labelError.setText("Nombre Erreurs : " + newLabelError);
                                        labelParsed.setText("Nombre Total :" + newLabelTotal);
                                    }
                                });

                            }

                        }

                        //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                    } catch (InterruptedException ex) {
                   //     Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            });

            threadReverso.start();
            threadBrowse.start();

        }
    }

}
