package io.beyn.spell;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.net.URL;
import java.time.Duration;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * FXML Controller class
 *
 * @author asmou
 */
public class FXMLDocumentController implements Initializable {
    @FXML
    private AnchorPane ForeignPan;
    @FXML
    private TextFlow areaResult;
    @FXML
    private Button buttonAR;
    @FXML
    private Button buttonEN;
    @FXML
    private Button buttonFR;
    @FXML
    private Button buttonResult;
    @FXML
    private Button buttonRun;
    @FXML
    private Label labelCorrect;
    @FXML
    private Label labelExamine;
    @FXML
    private Label labelGram;
    @FXML
    private Label labelLanguage;
    @FXML
    private Label labelOrto;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelTypog;
    @FXML
    private ScrollPane myScrollPane;

    String currentLanguage = "FR";
    Library lib = new Library();
    Vector<Element> webElements = new Vector<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // setting the icons for language buttons
        ImageView imageViewFR = new ImageView(getClass().getResource("french_icon.png").toExternalForm());
        ImageView imageViewEN = new ImageView(getClass().getResource("english_icon.png").toExternalForm());
        ImageView imageViewAR = new ImageView(getClass().getResource("arabic_icon.png").toExternalForm());
        buttonFR.setGraphic(imageViewFR);
        buttonEN.setGraphic(imageViewEN);
        buttonAR.setGraphic(imageViewAR);
        WebDriverManager.chromedriver().browserVersion("99.0.4844.51").setup();
        labelCorrect.setText("Correcte : 0");
        labelTotal.setText("Total : 0");
        labelExamine.setText("A Examiner : 0");
        labelOrto.setText("Ortographe : 0");
        labelTypog.setText("Typographi et ponctuation : 0");
        labelGram.setText("Grammaire : 0");
    }

    @FXML
    private void Event(ActionEvent event) {
        if (event.getSource() == buttonFR) {
            labelLanguage.setText("Language selectioné :  FR");
            currentLanguage = "FR";
        } else if (event.getSource() == buttonEN) {
            labelLanguage.setText("Language selectioné :  EN");
            currentLanguage = "EN";
        }
        if (event.getSource() == buttonAR) {
            labelLanguage.setText("Language selectioné :  AR");
            currentLanguage = "AR";
        }
        if (event.getSource() == buttonResult) {

        } else if (event.getSource() == buttonRun) {
            Thread threadBrowse = new Thread(() -> {
                //  start browser for user
                String newPage, contentPage;
                WebDriver driverBrowser = lib.initilizeBrowser("www.google.com");
                String initialPage = lib.getSourcePage(driverBrowser);
                Text text;
                while (true) {
                    try {
                        Thread.sleep(500);
                        newPage = lib.getSourcePage(driverBrowser);
                        if (!initialPage.equals(newPage)) {
                            contentPage = lib.getContentPage(driverBrowser);
                            webElements = lib.parseElements(contentPage, webElements);
                            Platform.runLater(() -> areaResult.getChildren().clear());
                            int numTotal = webElements.size();
                            int numCorecte = 0, numExamine = 0, numOrto = 0, numTypog = 0, numGram = 0;
                            for (int w = 0; w < webElements.size(); w++) {
                                String myString;
                                // myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                myString = "\n " + webElements.elementAt(w).getText();
                                if (webElements.elementAt(w).getState().equals("error")) {
                                    myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                }
                                text = new Text(myString);
                                text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                if (webElements.elementAt(w).getState().equals("typo")) {
                                    text.setFill(Color.web("#5e80fc"));
                                } else if (webElements.elementAt(w).getState().equals("correct")) {
                                    text.setFill(Color.web("#3fdd7b"));
                                    numCorecte++;
                                } else if (webElements.elementAt(w).getState().equals("ortographe")) {
                                    text.setFill(Color.web("#e86868"));
                                    numOrto++;
                                } else if (webElements.elementAt(w).getState().equals("grammaire")) {
                                    text.setFill(Color.web("#ddb83e"));
                                    numCorecte++;
                                } else if (webElements.elementAt(w).getState().equals("examiner")) {
                                    text.setFill(Color.web("#f64dff"));
                                    numOrto++;
                                } else if (webElements.elementAt(w).getState().equals("waiting")) {
                                    text.setFill(Color.web("#ffffff"));
                                    numOrto++;
                                }
                                Text tempText = text;
                                //update new Gui
                                String newLabelCorrect = String.valueOf(numCorecte);
                                String newLabelTotal = String.valueOf(numTotal);
                                String newLabelExamine = String.valueOf(numExamine);
                                String newLabelOrto = String.valueOf(numOrto);
                                String newLabelTypog = String.valueOf(numTypog);
                                String newLabelGram = String.valueOf(numGram);

                                Platform.runLater(() -> {
                                    areaResult.getChildren().add(tempText);
                                    labelCorrect.setText("Correcte : " + newLabelCorrect);
                                    labelTotal.setText("Total : " + newLabelTotal);
                                    labelExamine.setText("A Examiner : " + newLabelExamine);
                                    labelOrto.setText("Ortographe : " + newLabelOrto);
                                    labelTypog.setText("Typographi et ponctuation : " + newLabelTypog);
                                    labelGram.setText("Grammaire : " + newLabelGram);
                                });
                            }
                            initialPage = newPage;
                            //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                        } else {

                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            });

            //  start browser based on language chosen by user and visit
            Thread threadChecker = new Thread(() -> {
                WebDriver driverChecker = lib.startBrowserChecker(currentLanguage);
                WebDriverWait wait = new WebDriverWait(driverChecker, Duration.ofSeconds(30));
                // wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));
                Text text;
                List<WebElement> mistakes, correct;

                while (true) {
                    for (Element webElement : webElements) {
                        if (webElement.getState().equals("waiting")) {
                            try {
                                Thread.sleep(6000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("get the iframe ");
                            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='TextArea']//iframe")));

                            WebElement iframe = driverChecker.findElement(By.xpath("//div[@id='TextArea']//iframe"));
                            // switch to iframe
                            driverChecker.switchTo().frame(iframe);
                            WebElement textArea = driverChecker.findElement(By.tagName("p"));
                            textArea.clear();
                            textArea.sendKeys(webElement.getText());
                            //switch to main
                            driverChecker.switchTo().defaultContent();
                            if( currentLanguage.equals("FR")) driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Vérifier')]")).click();
                            else  if( currentLanguage.equals("EN")) driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Check')]")).click();
                            //switch to iframe
                            driverChecker.switchTo().frame(iframe);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }


                            System.out.println(driverChecker.findElement(By.tagName("p")).getAttribute("innerHTML"));

                            driverChecker.findElement(By.id("btnSpell")).click();
                            System.out.println("3. btnSpell clicked");
                            correct = driverChecker.findElements(By.xpath("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]"));
                            if (correct.size() > 0) {
                                System.out.println("correct");
                                webElement.setState("correct");
                            }
                            // not correct => wrong$x("*//label[contains(@class, 'correctmsg')]");
                            //$x("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]");
                            else {
                                mistakes = driverChecker.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                                System.out.println(mistakes.size() + "////" + mistakes.isEmpty());
                                if (mistakes.size() > 0) {
                                    StringBuilder suggestion = new StringBuilder();
                                    for (WebElement m : mistakes) {
                                        suggestion.append(lib.getSuggestion(m.getAttribute("tooltip")));
                                    }
                                    webElement.setState("wrong");
                                    webElement.setSuggest(suggestion.toString());
                                }
                            }
                        }
                    }

                    Platform.runLater(() -> areaResult.getChildren().clear());
                    int numTotal = webElements.size();
                    int numCorecte = 0, numExamine = 0, numOrto = 0, numTypog = 0, numGram = 0;
                    for (int w = 0; w < webElements.size(); w++) {

                        String myString;
                        myString = "\n " + webElements.elementAt(w).getText();
                        if (webElements.elementAt(w).getState().equals("wrong")) {
                            myString = myString + " => " + webElements.elementAt(w).getSuggest();
                        }

                        text = new Text(myString);
                        text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                        if (webElements.elementAt(w).getState().equals("typo")) {
                            text.setFill(Color.web("#5e80fc"));
                        } else if (webElements.elementAt(w).getState().equals("correct")) {
                            text.setFill(Color.web("#3fdd7b"));
                            numCorecte++;
                        } else if (webElements.elementAt(w).getState().equals("ortographe")) {
                            text.setFill(Color.web("#e86868"));
                            numOrto++;
                        } else if (webElements.elementAt(w).getState().equals("grammaire")) {
                            text.setFill(Color.web("#ddb83e"));
                            numCorecte++;
                        } else if (webElements.elementAt(w).getState().equals("examiner")) {
                            text.setFill(Color.web("#f64dff"));
                            numOrto++;
                        } else if (webElements.elementAt(w).getState().equals("waiting")) {
                            text.setFill(Color.web("#ffffff"));
                            numOrto++;
                        }
                        Text tempText = text;
                        //update new Gui
                        String newLabelCorrect = String.valueOf(numCorecte);
                        String newLabelTotal = String.valueOf(numTotal);
                        String newLabelExamine = String.valueOf(numExamine);
                        String newLabelOrto = String.valueOf(numOrto);
                        String newLabelTypog = String.valueOf(numTypog);
                        String newLabelGram = String.valueOf(numGram);

                        Platform.runLater(() -> {
                            areaResult.getChildren().add(tempText);
                            labelCorrect.setText("Correcte : " + newLabelCorrect);
                            labelTotal.setText("Total : " + newLabelTotal);
                            labelExamine.setText("A Examiner : " + newLabelExamine);
                            labelOrto.setText("Ortographe : " + newLabelOrto);
                            labelTypog.setText("Typographi et ponctuation : " + newLabelTypog);
                            labelGram.setText("Grammaire : " + newLabelGram);
                        });
                    }
                }
                //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
            });


            threadChecker.start();
            threadBrowse.start();

        }
    }

}
