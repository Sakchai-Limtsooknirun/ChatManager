//นาย ศักย์ชัย ลิ้มสุขนิรันดร์ 5810451063    Sakchai Limsuknirund 5810451063
package controllers;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller_Login {

    public static String name ;
    public static String address;
    @FXML
    private TextField Name;
    @FXML
    private TextField IP;
    @FXML
    public void initialize(){
        Name.requestFocus();
    }
    @FXML
    public void StartGAME(ActionEvent e) {
        name = Name.getText();
        address = IP.getText();
        Button b = (Button) e.getSource();
        Stage stage = (Stage) b.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/Chat_UI.fxml"));
        try {
            stage.setScene(new Scene(loader.load(), 437, 435));
            stage.setResizable(false);
            stage.show();
            stage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

        } catch (IOException e1) {
            e1.printStackTrace();
        }


    }

}
