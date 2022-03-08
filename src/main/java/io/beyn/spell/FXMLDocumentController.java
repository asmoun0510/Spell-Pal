package io.beyn.spell;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openqa.selenium.*;
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
    private Label labelBleu;
    @FXML
    private Label labelGreen;
    @FXML
    private Label labelLanguage;
    @FXML
    private Label labelOrange;
    @FXML
    private Label labelPink;
    @FXML
    private Label labelWhite;
    @FXML
    private Label labelYellow;


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
        labelGreen.setText("Correcte : 0");
        labelWhite.setText("Total : 0");
        labelPink.setText("À Examiner / Suggestions : 0");
        labelOrange.setText("Orthographe : 0");
        labelBleu.setText("Topographie / ponctuation : 0");
        labelYellow.setText("Grammaire / Verbes : 0");
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
                            int numCorrect = 0, numPink = 0, numOrange = 0, numBleu = 0, numYellow = 0;
                            for (int w = 0; w < webElements.size(); w++) {
                                String myString;
                                // myString = myString + " => " + webElements.elementAt(w).getSuggest();
                                myString = "\n " + webElements.elementAt(w).getText();
                                if (webElements.elementAt(w).getState().equals("error")) {
                                    // get list of errors
                                }
                                text = new Text(myString);
                                text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                if (webElements.elementAt(w).getState().equals("correct")) {
                                    text.setFill(Color.web("#2cff00"));
                                    numCorrect++;
                                } else if (webElements.elementAt(w).getState().equals("waiting")) {
                                    text.setFill(Color.web("#ffffff"));

                                } else if (webElements.elementAt(w).getState().equals("wrong")) {
                                    text.setFill(Color.web("#ffffff"));
                                }
                                
                                Text tempText = text;
                                //update new Gui
                                String newLabelGreen = String.valueOf(numCorrect);
                                String newLabelWhite = String.valueOf(numTotal);
                                String newLabelPink = String.valueOf(numPink);
                                String newLabelOrange = String.valueOf(numOrange);
                                String newLabelBleu = String.valueOf(numBleu);
                                String newLabelYellow = String.valueOf(numYellow);

                                Platform.runLater(() -> {
                                    areaResult.getChildren().add(tempText);
                                    labelGreen.setText("Correcte : " + newLabelGreen);
                                    labelWhite.setText("Total : " + newLabelWhite);
                                    labelPink.setText("À Examiner / Suggestions : " + newLabelPink);
                                    labelOrange.setText("Orthographe : " + newLabelOrange);
                                    labelBleu.setText("Topographie / ponctuation : " + newLabelBleu);
                                    labelYellow.setText("Grammaire / Verbes : " + newLabelYellow);
                                  });
                            }
                            initialPage = newPage;
                            //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
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
                            if (currentLanguage.equals("FR"))
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Vérifier')]")).click();
                            else if (currentLanguage.equals("EN"))
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Check')]")).click();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //switch to iframe
                            driverChecker.switchTo().frame(iframe);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            //print after check
                            System.out.println(driverChecker.findElement(By.tagName("p")).getAttribute("innerHTML"));
                            // get number of errors

                            int numberOfErrors = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']")).size();
                            System.out.println("number of errors => " + numberOfErrors);

                            if (numberOfErrors == 0) {
                                webElement.setState("correct");
                            } else {
                                webElement.setState("wrong");
                                List<WebElement> listOfErrors = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']"));
                                for (int er = 0; er < listOfErrors.size(); er++) {
                                    Error error = new Error();
                                    String typeOfError = listOfErrors.get(er).getAttribute("class");
                                    if (currentLanguage.equals("FR")) {
                                        if (typeOfError.equals("s-rg")) {
                                            error.setType("red");
                                        } else if (typeOfError.equals("s-bl")) {
                                            error.setType("bleu");
                                        } else if (typeOfError.equals("s-ve")) {
                                            error.setType("yellow");
                                        } else if (typeOfError.equals("s-or")) {
                                            error.setType("pink");
                                        }
                                    } else if (currentLanguage.equals("EN")) {
                                        if (typeOfError.equals("s-rg")) {
                                            error.setType("yellow");
                                        } else if (typeOfError.equals("s-bl")) {
                                            error.setType("bleu");
                                        } else if (typeOfError.equals("s-ve")) {
                                            error.setType("yellow");
                                        } else if (typeOfError.equals("s-or")) {
                                            error.setType("pink");
                                        }
                                    }
                                    driverChecker.findElement(By.id(listOfErrors.get(er).getAttribute("id"))).click();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    if (driverChecker.findElements(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).size()>0){
                                        String var = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue with element " + var);
                                        String var2 = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue explained " + var2);
                                        error.setExplication(var2);
                                        error.setCorrection(var);
                                    }
                                    else {
                                        String var = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-ListSolTr']")).getAttribute("innerHTML"));
                                        System.out.println("issue with element " + var);
                                        String var2 = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue explained " + var2);
                                        error.setExplication(var2);
                                        error.setCorrection(var);
                                    }

                                    webElement.addError(error);
                                }
                            }
                        }
                    }

                    Platform.runLater(() -> areaResult.getChildren().clear());
                    int numTotal = webElements.size();
                    int numCorrect = 0, numPink = 0, numOrange = 0, numBleu = 0, numYellow = 0;
                    for (int w = 0; w < webElements.size(); w++) {
                        String myString;
                        myString = "\n " + webElements.elementAt(w).getText();
                        if (webElements.elementAt(w).getState().equals("wrong")) {
                        //    myString = myString + " => " + webElements.elementAt(w).getSuggest();
                        }

                        text = new Text(myString);
                        text.setStyle(" -fx-font-size: 12pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                        if (webElements.elementAt(w).getState().equals("typo")) {
                            text.setFill(Color.web("#5e80fc"));
                        } else if (webElements.elementAt(w).getState().equals("correct")) {
                            text.setFill(Color.web("#2cff00"));
                            numCorrect++;
                        } else if (webElements.elementAt(w).getState().equals("ortographe")) {
                            text.setFill(Color.web("#e86868"));
                            numOrange++;
                        } else if (webElements.elementAt(w).getState().equals("grammaire")) {
                            text.setFill(Color.web("#ddb83e"));
                            numCorrect++;
                        } else if (webElements.elementAt(w).getState().equals("examiner")) {
                            text.setFill(Color.web("#f64dff"));
                            numOrange++;
                        } else if (webElements.elementAt(w).getState().equals("waiting")) {
                            text.setFill(Color.web("#ffffff"));
                            numOrange++;
                        }
                        Text tempText = text;
                        //update new Gui
                        String newLabelGreen = String.valueOf(numCorrect);
                        String newLabelWhite = String.valueOf(numTotal);
                        String newLabelPink = String.valueOf(numPink);
                        String newLabelOrange = String.valueOf(numOrange);
                        String newLabelBleu = String.valueOf(numBleu);
                        String newLabelYellow = String.valueOf(numYellow);

                        Platform.runLater(() -> {
                            areaResult.getChildren().add(tempText);
                            labelGreen.setText("Correcte : " + newLabelGreen);
                            labelWhite.setText("Total : " + newLabelWhite);
                            labelPink.setText("À Examiner / Suggestions : " + newLabelPink);
                            labelOrange.setText("Orthographe : " + newLabelOrange);
                            labelBleu.setText("Topographie / ponctuation : " + newLabelBleu);
                            labelYellow.setText("Grammaire / Verbes : " + newLabelYellow);
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
