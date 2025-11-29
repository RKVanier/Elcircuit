package elcicruit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/**
 * Controller for the Welcome Screen
 * Only provides access to Form Calculator
 */
public class WelcomeController {
    @FXML private Button formCalculatorButton;
    @FXML private Button exitButton;
    
    @FXML
    private void handleFormCalculator(ActionEvent event) {
        try {
            // Load the form-based circuit view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("CircuitView.fxml"));
            Parent root = loader.load();
            
            // Create new scene
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Get current stage and set new scene
            Stage stage = (Stage) formCalculatorButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Electric Circuit Calculator - Form View");
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading Circuit View: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExit(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }
}