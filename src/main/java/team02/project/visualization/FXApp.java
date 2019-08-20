package team02.project.visualization;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class FXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Main.fxml"));
            MainController mainController = new MainController();
            loader.setController(mainController);
            Parent root = loader.load();

            primaryStage.setTitle("Team-02");

            // initializing scene
            Scene mainScene = new Scene(root);

            //setting and showing stage
            primaryStage.setScene(mainScene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);

            // proper exit
            primaryStage.setOnCloseRequest(event -> System.exit(0));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
