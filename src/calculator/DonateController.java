package calculator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.ResourceBundle;

public class DonateController implements Initializable, ControlledScreen{
    private Main mainClass;
    private ScreensController myController;

    @FXML Button btnReturn;
    @FXML WebView donateView;

    public void initialize(URL url, ResourceBundle rb) {
        mainClass = Main.getInstance();
        WebEngine testEngine = donateView.getEngine();
        testEngine.setJavaScriptEnabled(true);
        testEngine.load("https://www.paypal.me/thume02");
    }
    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML public void calcScreen(){
        myController.setScreen(Main.screen4ID);
    }


    @FXML public void donatePage(){
        WebEngine testEngine = donateView.getEngine();
        testEngine.setJavaScriptEnabled(true);
        testEngine.load("https://www.paypal.me/thume02");
    }
}
