package io.pal.spell;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
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
    private ScrollPane myScrollPane;
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
    private Button buttonStop;
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
    WebDriver driverBrowser, driverChecker;
    Thread threadBrowse, threadChecker;



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebDriverManager.chromedriver().browserVersion("99.0.4844.51").setup();
        buttonStop.setVisible(false);
        buttonStop.setDisable(true);
        buttonResult.setDisable(true);
        // setting the icons for language buttons
        ImageView imageViewFR = new ImageView(getClass().getResource("french_icon.png").toExternalForm());
        ImageView imageViewEN = new ImageView(getClass().getResource("english_icon.png").toExternalForm());
        ImageView imageViewAR = new ImageView(getClass().getResource("arabic_icon.png").toExternalForm());
        buttonFR.setGraphic(imageViewFR);
        buttonEN.setGraphic(imageViewEN);
        buttonAR.setGraphic(imageViewAR);
        labelGreen.setText("Correcte : 0");
        labelWhite.setText("Total : 0");
        labelPink.setText("À Examiner / Suggestions : 0");
        labelOrange.setText("Orthographe : 0");
        labelBleu.setText("Topographie / ponctuation : 0");
        labelYellow.setText("Grammaire / Verbes : 0");
    }

    @FXML
    private void Event(ActionEvent event) throws IOException {
        if (event.getSource() == buttonFR) {
            labelLanguage.setText("Langue sélectionnée :  FR");
            currentLanguage = "FR";
        } else if (event.getSource() == buttonEN) {
            labelLanguage.setText("Langue sélectionnée :  EN");
            currentLanguage = "EN";
        } else if (event.getSource() == buttonAR) {
            labelLanguage.setText("Langue sélectionnée :  AR");
            currentLanguage = "AR";
        } else if (event.getSource() == buttonStop) {
            boolean exeption = false;
            try {
                threadBrowse.stop();
                threadChecker.stop();
                driverChecker.close();
                driverBrowser.close();
            } catch (Exception ex) {
                System.out.println(ex);
                exeption = true;
            }
            if (!exeption) {
                buttonStop.setVisible(false);
                buttonStop.setDisable(true);
                buttonRun.setDisable(false);
                buttonRun.setVisible(true);
                buttonAR.setDisable(false);
                buttonEN.setDisable(false);
                buttonFR.setDisable(false);
            }
        } else if (event.getSource() == buttonResult) {
            FileWriter fWriter = new FileWriter("resultat.txt", true);
            int white = myElements.size(), green = 0, pink = 0, bleu = 0, orange = 0, yellow = 0;
            for (int m = 0; m < myElements.size(); m++) {
                for (int e = 0; e < myElements.get(m).getErrors().size(); e++) {
                    if (myElements.get(m).getErrors().get(e).getType().equals("pink")) pink++;
                    else if (myElements.get(m).getErrors().get(e).getType().equals("yellow")) yellow++;
                    else if (myElements.get(m).getErrors().get(e).getType().equals("orange")) orange++;
                    else if (myElements.get(m).getErrors().get(e).getType().equals("bleu")) bleu++;
                    else green++;
                }
            }
            //summary
            fWriter.write("----------------Resultats : \n");
            fWriter.write("Date d'execution : " + new SimpleDateFormat("dd-MM-yyyy").format(new Date()) + "\n");
            fWriter.write("Langue d'execution : " + currentLanguage + "\n");
            fWriter.write("Nombre Total des elements verifieé : " + white + "\n");
            fWriter.write("Nombre Total des elements corrrectes : " + green + "\n");
            fWriter.write("À Examiner / Suggestions : " + pink + "\n");
            fWriter.write("Orthographe : " + orange + "\n");
            fWriter.write("Topographie / ponctuation : " + bleu + "\n");
            fWriter.write("Grammaire / Verbes : " + yellow + "\n \n");

            if (pink + yellow + bleu + orange > 0) {
                //List of Errors
                fWriter.write("----------------Liste des erreurs trouvé : \n");
                for (int m = 0; m < myElements.size(); m++) {
                    String errorText = "";
                    for (int e = 0; e < myElements.get(m).getErrors().size(); e++) {
                        errorText = errorText + "\t'" + myElements.get(m).getErrors().get(e).textFragment +
                                "'\n\t\t Type : " + myElements.get(m).getErrors().get(e).getType() +
                                "'\n\t\t Correction : " + myElements.get(m).getErrors().get(e).getCorrection() +
                                "'\n\t\t Explication :" + myElements.get(m).getErrors().get(e).getExplication() + "\n";
                    }
                    if (myElements.get(m).getErrors().size() > 0) {
                        errorText = m + " : " + myElements.get(m).getText() + "\n";
                        fWriter.write(errorText);
                    }
                }
            } else {
                fWriter.write("Aucune erreurs touvé");
            }
            fWriter.close();

        } else if (event.getSource() == buttonRun) {
            buttonStop.setVisible(true);
            buttonStop.setDisable(false);
            buttonRun.setDisable(true);
            buttonRun.setVisible(false);
            buttonAR.setDisable(true);
            buttonEN.setDisable(true);
            buttonFR.setDisable(true);
            threadBrowse = new Thread(() -> {
                //  start browser for user
                String newPage, contentPage;
                driverBrowser = lib.initilizeBrowser("www.google.com");
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
            threadChecker = new Thread(() -> {
                driverChecker = lib.startBrowserChecker(currentLanguage);
                WebDriverWait wait = new WebDriverWait(driverChecker, Duration.ofSeconds(10));
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
                                Thread.sleep(500);
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
                            if (currentLanguage.equals("FR")){
                                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='button'][contains(.,'Vérifier')]")));
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Vérifier')]")).click();
                            }

                            else if (currentLanguage.equals("EN")) {
                                wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='button'][contains(.,'Check')]")));
                                driverChecker.findElement(By.xpath("//div[@class='button'][contains(.,'Check')]")).click();
                            }


                            //switch to iframe
                            driverChecker.switchTo().frame(iframe);

                            //print after check
                            System.out.println(driverChecker.findElement(By.tagName("p")).getAttribute("innerHTML"));
                            //get number of errors
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            int numberOfErrors = driverChecker.findElements(By.xpath("//span[@class='s-rg'] | //span[@class='s-bl'] | //span[@class='s-ve'] | //span[@class='s-or']")).size();

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
                                    error.setTextFragment(driverChecker.findElement(By.id(listOfErrorsElement.get(er).getAttribute("id"))).getText());
                                    driverChecker.findElement(By.id(listOfErrorsElement.get(er).getAttribute("id"))).click();

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
                                myString = myElements.elementAt(w).getText();
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
                                        textError.setText("\n *** " + myError.getTextFragment() + " =>\n " + "Correction : " + myError.getCorrection() + "\n" + "Explication :" + myError.getExplication() + "\n");
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
