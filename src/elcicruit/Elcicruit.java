/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package elcicruit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author olaja
 */
public class Elcicruit extends Application {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            // Load the Welcome View FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeView.fxml"));
            Parent root = loader.load();
            
            // Create the scene
            Scene scene = new Scene(root);
            
            // Add CSS styling
            scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            
            // Set up the stage
            stage.setTitle("Electric Circuit Calculator - Welcome");
            stage.setScene(scene);
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setResizable(false);
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }
}