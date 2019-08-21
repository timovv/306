package team02.project.visualization;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import team02.project.App;
import team02.project.cli.CLIConfig;

public class MainController {

    private CLIConfig inputConfig;

    MainController(){
        inputConfig = App.config;
    }

    /*
     * Stuff that needs to be done after hooked up FXML
     */
    void initManagers(){
        // do this shit????
        // make list managers >> can call them for updates when needed (but how thread?)
    }



}
