package sample;

import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;

import java.io.File;

public class Controller {

    @FXML
    private BorderPane mainStage;

    @FXML
    private void loadLog(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(mainStage.getScene().getWindow());

    }
}
