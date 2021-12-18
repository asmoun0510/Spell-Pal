/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
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
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * FXML Controller class
 *
 * @author asmou
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private Button buttonRun;
    @FXML
    private TextField inputUrl;
    @FXML
    private Label labelParsed;
    @FXML
    private Label labelState;
    @FXML
    private Label labelCorrect;
    @FXML
    private Label labelSpell;
    @FXML
    private Label labelGrammar;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextFlow areaResult;

    Library lib = new Library();
    ObservableList<Text> green = null, bleu = null, red = null, black = null;
    Thread satrtsingle;

    WebDriver driverScribens, driverBrowser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        green = FXCollections.observableArrayList();
        bleu = FXCollections.observableArrayList();
        red = FXCollections.observableArrayList();
        black = FXCollections.observableArrayList();

        labelCorrect.setText("Number of correct  elements : 0");
        labelSpell.setText("Number of spell erros : 0");
        labelGrammar.setText("Number of Grammar errors : 0");
        labelParsed.setText("Number of elements parsed : 0");

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
                    driverBrowser = lib.initilizeBrowser(driverBrowser, inputUrl.getText());
                    String initialPage = lib.getSourcePage(driverBrowser) ;
                    while (true) {
                        try {
                            Thread.sleep(250);
                            String newPage = lib.getSourcePage(driverBrowser) ;
                            if (!initialPage.equals (newPage)) {
                                System.out.println("changed "+ lib.getContentPage(driverBrowser) );
                               
                            }
                            else System.out.println("no changes");
                        } catch (InterruptedException ex) {
                            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    }
                }
            };

            Thread threadScribens = new Thread() {
                //  start browser and visit Scribens  
                public void run() {
                    driverScribens = lib.initilizeScribens(driverScribens);
                }
            };

            threadScribens.start();
            threadBrowse.start();

        }
    }

}
