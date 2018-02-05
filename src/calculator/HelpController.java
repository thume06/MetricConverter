package calculator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javax.swing.text.html.ImageView;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable, ControlledScreen{
    private Main mainClass;
    private ScreensController myController;

    @FXML Button btnReturn;

    public void initialize(URL url, ResourceBundle rb) {
        mainClass = Main.getInstance();
    }

    public void setScreenParent(ScreensController screenParent){
        myController = screenParent;
    }

    @FXML public void calcScreen(){
        myController.setScreen(Main.screen4ID);
    }

}

