package elcircuit.elcircuit.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * Controller for the welcome screen of the ElCircuit application.
 * Provides buttons to exit the application or open the main circuit
 * simulator view.
 *
 * @author Rayan
 */
public class WelcomeViewController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button exitButton;

    @FXML
    private Button formCalculatorButton;

    /**
     * Handles the Exit button. Closes the JavaFX application.
     *
     * @param event button click event
     */
    @FXML
    void handleExit(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Handles the "Form Calculator" button.
     * Opens the main circuit simulator window defined by MainView.fxml.
     *
     * @param event button click event
     * @throws IOException if the FXML cannot be loaded
     */
    @FXML
    void handleFormCalculator(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/elcircuit/elcircuit/Views/MainView.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Called automatically by the FXMLLoader after the FXML has been loaded.
     * Used here only to assert that fields were injected correctly.
     */
    @FXML
    void initialize() {
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'WelcomeView.fxml'.";
        assert formCalculatorButton != null : "fx:id=\"formCalculatorButton\" was not injected: check your FXML file 'WelcomeView.fxml'.";
    }
}
