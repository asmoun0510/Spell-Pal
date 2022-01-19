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
    private TextFlow areaResult;

    Library lib = new Library();
    Vector<Element> webElements = new Vector<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebDriverManager.chromedriver().browserVersion("97.0.4692.71").setup();
        labelCorrect.setText("Nombre Correcte : 0");
        labelError.setText("Nombre Erreur : 0");
        labelParsed.setText("Nombre Total : 0");
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

                            Platform.runLater(() -> areaResult.getChildren().clear());
                            int numTotal = webElements.size();
                            int numCorecte = 0, numError = 0;
                            for (int w = 0; w < webElements.size(); w++) {
                                String myString;
                                // myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                myString = "\n " + webElements.elementAt(w).getText();
                                if (webElements.elementAt(w).getState().equals("error")) {
                                    myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                }
                                text = new Text(myString);
                                text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
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
                                Platform.runLater(() -> {
                                    areaResult.getChildren().add(tempText);
                                    labelCorrect.setText("Nombre Correcte : " + newLabelCorrect);
                                    labelError.setText("Nombre Erreurs : " + newLabelError);
                                    labelParsed.setText("Nombre Total :" + newLabelTotal);
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
                    for (int j = 0; j < webElements.size(); j++) {
                        if (webElements.get(j).getState().equals("waiting")) {
                            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnClear"))).click();
                            try {
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startText"))).sendKeys(webElements.get(j).getText());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSpell"))).click();
                            try {
                                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                            List<WebElement> mistakes;
                            try {
                                mistakes = driverReverso.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                            } catch (Exception e) {
                                mistakes = null;
                                e.printStackTrace();
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

                        Platform.runLater(() -> areaResult.getChildren().clear());
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
                            Platform.runLater(() -> {
                                areaResult.getChildren().add(tempText);
                                labelCorrect.setText("Nombre Correcte : " + newLabelCorrect);
                                labelError.setText("Nombre Erreurs : " + newLabelError);
                                labelParsed.setText("Nombre Total :" + newLabelTotal);
                            });

                        }

                    }

                    //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );

                }

            });

            threadReverso.start();
            threadBrowse.start();

        }
    }

}
