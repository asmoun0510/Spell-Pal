/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private Label labelState;
    @FXML
    private Label labelCorrect;
    @FXML
    private Label labelError;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow areaResult;

    Library lib = new Library();
    ObservableList<Text> green = null, orange = null, red = null, black = null;
    Thread satrtsingle;
    WebDriver driverReverso, driverBrowser;
    Vector<Element> webElements = new Vector<Element>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        green = FXCollections.observableArrayList();
        orange = FXCollections.observableArrayList();
        red = FXCollections.observableArrayList();
        black = FXCollections.observableArrayList();

        labelCorrect.setText("Nombre Correcte : 0");
        labelError.setText("Nombre Erreur : 0");
        labelParsed.setText("Nombre Total : 0");

        System.setProperty("webdriver.chrome.driver", "C:\\Users\\asmou\\Documents\\NetBeansProjects\\Beyn_Spell\\src\\dependencies\\chromedriver.exe");

        /*
        WebElement l= driver.findElement(By.tagName("body"));
      String p = l.getText();
      System.out.println("Page Source is : " + p);S
         */
    }

    @FXML
    private void Event(ActionEvent event) {
        if (event.getSource() == buttonRun) {
            Thread threadBrowse = new Thread() {
                public void run() {
                    //  start browser for user
                    String newPage, contentPage;
                    driverBrowser = lib.initilizeBrowser(driverBrowser, "www.google.com");
                    String initialPage = lib.getSourcePage(driverBrowser);
                    Text text;
                    while (true) {
                        try {
                            Thread.sleep(500);
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
                                        text.setFill(Color.rgb(100, 100, 100));
                                    } else if (webElements.elementAt(w).getState().equals("correct")) {
                                        text.setFill(Color.rgb(60, 200, 80));
                                    } else if (webElements.elementAt(w).getState().equals("wrong")) {
                                        text.setFill(Color.rgb(250, 85, 85));
                                    }
                                    Text tempText = text;
                                    //update new Gui

                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            areaResult.getChildren().add(tempText);
                                        }
                                    });

                                }
                                initialPage = newPage;
                                //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                            } else {

                            }
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            };

            Thread threadReverso = new Thread() {
                //  start browser and visit Reverso  
                public void run() {
                    driverReverso = lib.initilizeReverso(driverReverso);
                    WebDriverWait wait = new WebDriverWait(driverReverso, 50);
                    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("processing")));

                    Text text;
                    while (true) {
                        try {
                            Thread.sleep(500);
                            for (int j = 0; j < webElements.size(); j++) {
                                if (webElements.get(j).state.equals("waiting")) {
                                    Thread.sleep(500);
                                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnClear"))).click();
                                    Thread.sleep(500);
                                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("startText"))).sendKeys(webElements.get(j).text);
                                    Thread.sleep(500);
                                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSpell"))).click();
                                    Thread.sleep(500);
                                    //  wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("btnSpell"))).click();
                                    List<WebElement> mistakes = driverReverso.findElements(By.xpath("*//span[contains(@class, 'correction')]"));
                                    if (!mistakes.isEmpty()) {
                                        System.out.println(mistakes.size() + "/");
                                        String suggestion = "";
                                        for (int m = 0; m < mistakes.size(); m++) {
                                            System.out.println(mistakes.get(m).getAttribute("tooltip"));
                                            System.out.println("init sug" + suggestion);
                                            suggestion = suggestion + lib.getSugesstion(mistakes.get(m).getAttribute("tooltip"));
                                            System.out.println("last sug" + suggestion);
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
                                        text.setFill(Color.rgb(100, 100, 100));
                                    } else if (webElements.elementAt(w).getState().equals("correct")) {
                                        text.setFill(Color.rgb(60, 200, 80));
                                    } else if (webElements.elementAt(w).getState().equals("wrong")) {
                                        text.setFill(Color.rgb(250, 85, 85));
                                    }
                                    Text tempText = text;

                                    //update new Gui
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            areaResult.getChildren().add(tempText);
                                        }
                                    });

                                }

                            }

                            //   System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                }
            };

            threadReverso.start();
            threadBrowse.start();

        }
    }

}
