package io.beyn.spell;

/*
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
    private TextFlow areaResult;
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
    private Label labelOrto;
    @FXML
    private Label labelTotal;
    @FXML
    private Label labelTypog;
    Library lib = new Library();
    Vector<Element> webElements = new Vector<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebDriverManager.chromedriver().browserVersion("97.0.4692.99").setup();
        labelCorrect.setText("Correcte : 0");
        labelTotal.setText("Total : 0");
        labelExamine.setText("A Examiner : 0");
        labelOrto.setText("Ortographe : 0");
        labelTypog.setText("Typographi et ponctuation : 0");
        labelGram.setText("Grammaire : 0");
    }

    @FXML
    private void Event(ActionEvent event) {
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
                        //  Thread.sleep(300);
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
            //  start browser and visit Scribens FR
            Thread threadScribensFR = new Thread(() -> {
                WebDriver driverScribensFR = lib.initilizeScribensFR();
                WebDriverWait wait = new WebDriverWait(driverScribensFR, 10);
                // wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));
                Text text;
                List<WebElement> mistakes, correct;
                while (true) {
                    for (int j = 0; j < webElements.size(); j++) {
                        if (webElements.get(j).getState().equals("waiting") && webElements.get(j).getLanguage().equals("FRENCH")) {
                            try {
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//body//p"))).clear();
                                System.out.println("cleared");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensFR.get("https://www.Scribens.fr");
                                j--;
                                break;
                            }
                            try {
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//body//p"))).sendKeys(webElements.get(j).getText());
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensFR.get("https://www.Scribens.fr");
                                j--;
                                break;
                            }

                            try {
                                wait.until(ExpectedConditions.and(
                                        ExpectedConditions.visibilityOfElementLocated(By.id("startText")),
                                        ExpectedConditions.elementToBeClickable(By.id("btnSpell"))));
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensFR.get("https://www.Scribens.fr");
                                j--;
                                break;
                            }
                            try {
                                driverScribensFR.findElement(By.id("startText")).sendKeys(webElements.get(j).getText());
                                System.out.println("2.startText sendkeys");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensFR.get("https://www.Scribens.fr");
                                j--;
                                break;
                            }

                            try {
                                driverScribensFR.findElement(By.id("btnSpell")).click();
                                System.out.println("3. btnSpell cliked");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensFR.get("https://www.Scribens.fr");
                                j--;
                                break;
                            }

                            try {
                                Thread.sleep(400);
                                correct = driverScribensFR.findElements(By.xpath("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]"));
                                if (correct.size() > 0) {
                                    System.out.println("corrrecr");
                                    webElements.get(j).setState("correct");
                                }
                                // not correct => wrong$x("*//label[contains(@class, 'correctmsg')]");
                                //$x("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]");
                                else {
                                    mistakes = driverScribensFR.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                                    System.out.println(mistakes.size() + "////" + mistakes.isEmpty());
                                    if (mistakes.size() > 0) {
                                        String suggestion = "";
                                        for (int m = 0; m < mistakes.size(); m++) {
                                            suggestion = suggestion + lib.getSugesstion(mistakes.get(m).getAttribute("tooltip"));
                                        }
                                        webElements.get(j).setState("wrong");
                                        webElements.get(j).setSuggest(suggestion);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                j--;
                                break;
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

                }

            });
            //  start browser and visit Scribens ENG
            Thread threadScribensENG = new Thread(() -> {
                WebDriver driverScribensENG = lib.initilizeScribensENG();
                WebDriverWait wait = new WebDriverWait(driverScribensENG, 10);
                // wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));
                Text text;
                List<WebElement> mistakes, correct;

                while (true) {
                    for (int j = 0; j < webElements.size(); j++) {
                        if (webElements.get(j).getState().equals("waiting")) {
                            try {
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//body//p"))).clear();
                                System.out.println("cleared");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensENG.get("https://www.Scribens.com");
                                j--;
                                break;
                            }
                            try {
                                wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//body//p"))).sendKeys(webElements.get(j).getText());
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensENG.get("https://www.Scribens.com");
                                j--;
                                break;
                            }
                            try {
                                driverScribensENG.findElement(By.id("startText")).sendKeys(webElements.get(j).getText());
                                System.out.println("2.startText sendkeys");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensENG.get("https://www.Scribens.com");
                                j--;
                                break;
                            }

                            try {
                                driverScribensENG.findElement(By.id("btnSpell")).click();
                                System.out.println("3. btnSpell cliked");
                            } catch (Exception e) {
                                e.printStackTrace();
                                driverScribensENG.get("https://www.Scribens.com");
                                j--;
                                break;
                            }

                            try {
                                Thread.sleep(400);
                                correct = driverScribensENG.findElements(By.xpath("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]"));
                                if (correct.size() > 0) {
                                    System.out.println("corrrecr");
                                    webElements.get(j).setState("correct");
                                }
                                // not correct => wrong$x("*//label[contains(@class, 'correctmsg')]");
                                //$x("*//label[contains(@class, 'correctmsg') and contains(@style, 'display: inline;')]");
                                else {
                                    mistakes = driverScribensENG.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                                    System.out.println(mistakes.size() + "////" + mistakes.isEmpty());
                                    if (mistakes.size() > 0) {
                                        String suggestion = "";
                                        for (int m = 0; m < mistakes.size(); m++) {
                                            suggestion = suggestion + lib.getSugesstion(mistakes.get(m).getAttribute("tooltip"));
                                        }
                                        webElements.get(j).setState("wrong");
                                        webElements.get(j).setSuggest(suggestion);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                j--;
                                break;
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

                }

            });

            threadScribensFR.start();
            threadScribensENG.start();
            threadBrowse.start();

        }
    }

}
