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
    Vector<Element> myElements = new Vector<>();

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
                while (true) {
                    try {
                        Thread.sleep(500);
                        newPage = lib.getSourcePage(driverBrowser);
                        if (!initialPage.equals(newPage)) {
                            contentPage = lib.getContentPage(driverBrowser);
                            myElements = lib.parseElements(contentPage, myElements);
                            initialPage = newPage;
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
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Text text, textError;
                while (true) {
                    for (int e = 0; e < myElements.size(); e++) {
                        if (myElements.get(e).getState().equals("waiting")) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            driverChecker.switchTo().defaultContent();
                            System.out.println("get the iframe ");
                            WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='TextArea']//iframe")));
                            // switch to iframe
                            driverChecker.switchTo().frame(iframe);
                            WebElement textArea = driverChecker.findElement(By.tagName("p"));
                            textArea.clear();
                            textArea.sendKeys(myElements.get(e).getText());
                            //switch to main
                            driverChecker.switchTo().defaultContent();
                            if (currentLanguage.equals("FR"))
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Vérifier')]")).click();
                            else if (currentLanguage.equals("EN"))
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Check')]")).click();
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            //switch to iframe
                            driverChecker.switchTo().frame(iframe);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            //print after check
                            System.out.println(driverChecker.findElement(By.tagName("p")).getAttribute("innerHTML"));
                            // get number of errors

                            int numberOfErrors = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']")).size();
                            System.out.println("number of errors found 11 => " + numberOfErrors);

                            if (numberOfErrors == 0) {
                                myElements.get(e).setState("correct");
                                driverChecker.switchTo().defaultContent();
                            } else {
                                myElements.get(e).setState("wrong");
                                List<WebElement> listOfErrorsElement = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']"));
                                System.out.println("number of errors true error fro loop => " + numberOfErrors);
                                // span[@class='s-bl'] or //span[@class='s-ve'] or//span[@class='s-or']"));
                                for (int er = 0; er < listOfErrorsElement.size(); er++) {
                                    Error error = new Error();
                                    WebElement TempElement = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']")).get(er);
                                    String typeOfError = TempElement.getAttribute("class");
                                    if (currentLanguage.equals("FR")) {
                                        switch (typeOfError) {
                                            case "s-rg" -> error.setType("orange");
                                            case "s-bl" -> error.setType("bleu");
                                            case "s-ve" -> error.setType("yellow");
                                            case "s-or" -> error.setType("pink");
                                        }
                                    } else if (currentLanguage.equals("EN")) {
                                        switch (typeOfError) {
                                            case "s-rg", "s-ve" -> error.setType("yellow");
                                            case "s-bl" -> error.setType("bleu");
                                            case "s-or" -> error.setType("pink");
                                        }
                                    }
                                    driverChecker.findElement(By.id(listOfErrorsElement.get(er).getAttribute("id"))).click();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ex) {
                                        ex.printStackTrace();
                                    }
                                    driverChecker.switchTo().defaultContent();
                                    if (driverChecker.findElements(By.xpath("//div[contains(@class,'Cor-PopupPanelExpSol') and contains(@class, 'open')]")).size() > 0) {
                                        String var = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue with element " + var);
                                        String var2 = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue explained " + var2);
                                        error.setExplication(var2);
                                        error.setCorrection(var);
                                    } else {
                                        String var = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-ListSolTr']")).getAttribute("innerHTML"));
                                        System.out.println("issue with element " + var);
                                        //error happens here
                                        String var2 = lib.getTextHTML(driverChecker.findElement(By.xpath("//div[@class='Cor-PopupPanelExpSol open']")).getAttribute("innerHTML"));
                                        System.out.println("issue explained " + var2);
                                        error.setExplication(var2);
                                        error.setCorrection(var);
                                    }
                                    myElements.get(e).addError(error);
                                    driverChecker.switchTo().frame(iframe);
                                }
                                //  if (listOfErrors.size() == 0)
                            }
                            // updating GUI

                            int numTotal = myElements.size();
                            int numCorrect = 0, numPink = 0, numOrange = 0, numBleu = 0, numYellow = 0;
                            Platform.runLater(() -> areaResult.getChildren().clear());

                            for (int w = 0; w < myElements.size(); w++) {
                                String myString;
                                myString =  myElements.elementAt(w).getText();


                                if (myElements.elementAt(w).getState().equals("correct")) {
                                    text = new Text();
                                    text.setText(myString + "\n");
                                    text.setStyle(" -fx-font-size: 14pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                    text.setFill(Color.web("#2cff00"));
                                    numCorrect++;
                                    Text tempText = text;
                                    Platform.runLater(() -> {
                                        areaResult.getChildren().add(tempText);
                                    });
                                } else if (myElements.elementAt(w).getState().equals("waiting")) {
                                    text = new Text();
                                    text.setText(myString + "\n");
                                    text.setStyle(" -fx-font-size: 14pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                    text.setFill(Color.web("#ffffff"));
                                    Text tempText = text;
                                    Platform.runLater(() -> {
                                        areaResult.getChildren().add(tempText);
                                    });
                                } else if (myElements.elementAt(w).getState().equals("wrong")) {
                                    Vector<Error> myErrors = myElements.elementAt(w).getErrors();
                                    for (Error myError : myErrors) {
                                        textError = new Text();
                                        textError.setStyle("-fx-font-size: 14pt;-fx-font-family: \"Ebrima\";-fx-font-weight: bold;");
                                        textError.setText("\n *** "+myError.getTextFragment() + " =>\n " + "Correction : " + myError.getCorrection() + "\n" + "Explication :" + myError.getExplication() + "\n");
                                        if (myError.getType().equals("pink")) {
                                            textError.setFill(Color.web("#f64dff"));
                                            numPink++;
                                        } else if (myError.getType().equals("orange")) {
                                            textError.setFill(Color.web("#e86868"));
                                            numOrange++;
                                        } else if (myError.getType().equals("yellow")) {
                                            textError.setFill(Color.web("#ddb83e"));
                                            numYellow++;
                                        } else if (myError.getType().equals("bleu")) {
                                            textError.setFill(Color.web("#5e80fc"));
                                            numBleu++;
                                        }
                                        Text tempText = textError;
                                        Platform.runLater(() -> {
                                            areaResult.getChildren().add(tempText);
                                        });
                                    }
                                }
                                //update new Gui
                                String newLabelGreen = String.valueOf(numCorrect);
                                String newLabelWhite = String.valueOf(numTotal);
                                String newLabelPink = String.valueOf(numPink);
                                String newLabelOrange = String.valueOf(numOrange);
                                String newLabelBleu = String.valueOf(numBleu);
                                String newLabelYellow = String.valueOf(numYellow);
                                Platform.runLater(() -> {
                                    labelGreen.setText("Correcte : " + newLabelGreen);
                                    labelWhite.setText("Total : " + newLabelWhite);
                                    labelPink.setText("À Examiner / Suggestions : " + newLabelPink);
                                    labelOrange.setText("Orthographe : " + newLabelOrange);
                                    labelBleu.setText("Topographie / ponctuation : " + newLabelBleu);
                                    labelYellow.setText("Grammaire / Verbes : " + newLabelYellow);
                                });
                            }
                        }
                    }


                }
                //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
            });


            threadChecker.start();
            threadBrowse.start();

        }
    }

}
