//นาย ศักย์ชัย ลิ้มสุขนิรันดร์ 5810451063    Sakchai Limsuknirund 5810451063
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/Login_UI.fxml"));
        primaryStage.setTitle("JAY CHAT");
        primaryStage.setScene(new Scene(root, 537, 342));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}
