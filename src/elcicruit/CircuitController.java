package elcicruit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller for the Circuit Calculator input form
 */
public class CircuitController {

    // Battery Controls
    @FXML private TextField batteryEmfField;
    @FXML private Button addBatteryButton;
    @FXML private TableView<Battery> batteryTable;
    @FXML private TableColumn<Battery, Double> batteryEmfColumn;
    
    // Resistor Controls
    @FXML private TextField resistorResistanceField;
    @FXML private Button addResistorButton;
    @FXML private TableView<Resistor> resistorTable;
    @FXML private TableColumn<Resistor, Double> resistorResistanceColumn;
    
    // Capacitor Controls
    @FXML private TextField capacitorCapacitanceField;
    @FXML private TextField capacitorChargeField;
    @FXML private Button addCapacitorButton;
    @FXML private TableView<Capacitor> capacitorTable;
    @FXML private TableColumn<Capacitor, Double> capacitorCapacitanceColumn;
    @FXML private TableColumn<Capacitor, Double> capacitorChargeColumn;
    
    // Buttons
    @FXML private Button calculateButton;
    @FXML private Button clearAllButton;
    @FXML private Button viewDiagramButton;
    
    // Results Labels
    @FXML private Label equivalentEmfLabel;
    @FXML private Label equivalentResistanceLabel;
    @FXML private Label equivalentCapacitanceLabel;
    @FXML private Label currentLabel;
    
    // Observable Lists for Tables
    private ObservableList<Battery> batteries = FXCollections.observableArrayList();
    private ObservableList<Resistor> resistors = FXCollections.observableArrayList();
    private ObservableList<Capacitor> capacitors = FXCollections.observableArrayList();
    
    // Circuit object
    private Circuit circuit;
    
    // Reference to diagram window
    private Stage diagramStage = null;
    
    @FXML
    public void initialize() {
        // Initialize circuit
        circuit = new Circuit();
        
        // Set up Battery Table
        batteryEmfColumn.setCellValueFactory(new PropertyValueFactory<>("emf"));
        batteryTable.setItems(batteries);
        
        // Set up Resistor Table
        resistorResistanceColumn.setCellValueFactory(new PropertyValueFactory<>("resistance"));
        resistorTable.setItems(resistors);
        
        // Set up Capacitor Table
        capacitorCapacitanceColumn.setCellValueFactory(new PropertyValueFactory<>("capacitance"));
        capacitorChargeColumn.setCellValueFactory(new PropertyValueFactory<>("charge"));
        capacitorTable.setItems(capacitors);
        
        // Initially disable View Diagram button
        updateViewDiagramButton();
    }
    
    @FXML
    private void handleAddBattery(ActionEvent event) {
        try {
            Double emf = Double.parseDouble(batteryEmfField.getText());
            Battery battery = new Battery(emf);
            batteries.add(battery);
            circuit.getBatterys().add(battery);
            batteryEmfField.clear();
            updateViewDiagramButton();
            updateDiagram();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for EMF");
        }
    }
    
    @FXML
    private void handleAddResistor(ActionEvent event) {
        try {
            Double resistance = Double.parseDouble(resistorResistanceField.getText());
            Resistor resistor = new Resistor(resistance, 0, 0);
            resistors.add(resistor);
            circuit.getResistors().add(resistor);
            resistorResistanceField.clear();
            updateViewDiagramButton();
            updateDiagram();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for Resistance");
        }
    }
    
    @FXML
    private void handleAddCapacitor(ActionEvent event) {
        try {
            Double capacitance = Double.parseDouble(capacitorCapacitanceField.getText());
            Double charge = Double.parseDouble(capacitorChargeField.getText());
            Capacitor capacitor = new Capacitor(capacitance, charge, 0);
            capacitors.add(capacitor);
            circuit.getCapacitors().add(capacitor);
            capacitorCapacitanceField.clear();
            capacitorChargeField.clear();
            updateViewDiagramButton();
            updateDiagram();
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter valid numbers for Capacitance and Charge");
        }
    }
    
    @FXML
    private void handleCalculate(ActionEvent event) {
        try {
            // Initialize equivalent components
            circuit.setEquivalentBattery(new Battery(0.0));
            circuit.setEquivalentResistor(new Resistor(0.0, 0.0, 0.0));
            circuit.setEquivalentCapacitor(new Capacitor(0.0, 0.0, 0.0));
            
            // Calculate equivalent values
            if (!batteries.isEmpty()) {
                calculateEquivalentBattery();
                equivalentEmfLabel.setText(String.format("%.2f V", circuit.getEquivalentBattery().getEmf()));
            }
            
            if (!resistors.isEmpty()) {
                circuit.calculateResistor();
                equivalentResistanceLabel.setText(String.format("%.2f Ω", circuit.getEquivalentResistor().getResistance()));
            }
            
            if (!capacitors.isEmpty()) {
                circuit.calculateCapacitor();
                equivalentCapacitanceLabel.setText(String.format("%.6f F", circuit.getEquivalentCapacitor().getCapacitance()));
            }
            
            // Calculate current (V = IR, so I = V/R)
            if (!batteries.isEmpty() && !resistors.isEmpty()) {
                double current = circuit.getEquivalentBattery().getEmf() / circuit.getEquivalentResistor().getResistance();
                circuit.setCurrent(current);
                currentLabel.setText(String.format("%.4f A", current));
            }
            
            updateDiagram();
            
        } catch (Exception e) {
            showAlert("Calculation Error", "Error during calculation: " + e.getMessage());
        }
    }
    
    private void calculateEquivalentBattery() {
        double totalEmf = 0;
        for (Battery bat : circuit.getBatterys()) {
            totalEmf += bat.getEmf();
        }
        circuit.getEquivalentBattery().setEmf(totalEmf);
    }
    
    @FXML
    private void handleClearAll(ActionEvent event) {
        batteries.clear();
        resistors.clear();
        capacitors.clear();
        circuit = new Circuit();
        
        equivalentEmfLabel.setText("--");
        equivalentResistanceLabel.setText("--");
        equivalentCapacitanceLabel.setText("--");
        currentLabel.setText("--");
        
        updateViewDiagramButton();
        updateDiagram();
    }
    
    @FXML
    private void handleRemoveBattery(ActionEvent event) {
        Battery selected = batteryTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            batteries.remove(selected);
            circuit.getBatterys().remove(selected);
            updateViewDiagramButton();
            updateDiagram();
        }
    }
    
    @FXML
    private void handleRemoveResistor(ActionEvent event) {
        Resistor selected = resistorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            resistors.remove(selected);
            circuit.getResistors().remove(selected);
            updateViewDiagramButton();
            updateDiagram();
        }
    }
    
    @FXML
    private void handleRemoveCapacitor(ActionEvent event) {
        Capacitor selected = capacitorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            capacitors.remove(selected);
            circuit.getCapacitors().remove(selected);
            updateViewDiagramButton();
            updateDiagram();
        }
    }
    
    /**
     * Opens the circuit diagram viewer window
     */
    @FXML
    private void handleViewDiagram(ActionEvent event) {
        // Check if there are any components
        if (!hasComponents()) {
            showAlert("No Components", "Please add at least one component before viewing the diagram.");
            return;
        }
        
        try {
            if (diagramStage == null || !diagramStage.isShowing()) {
                // Load CircuitBuilderView.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CircuitBuilderView.fxml"));
                Parent root = loader.load();
                
                // Get the controller and pass circuit data
                CircuitBuilderController diagramController = loader.getController();
                diagramController.setCircuit(circuit);
                
                // Create new stage for diagram
                diagramStage = new Stage();
                diagramStage.setTitle("Circuit Diagram");
                diagramStage.setScene(new Scene(root, 800, 600));
                diagramStage.show();
                
                // Clear reference when window is closed
                diagramStage.setOnHidden(e -> diagramStage = null);
            } else {
                // Window already open, just update it
                diagramStage.toFront();
                updateDiagram();
            }
        } catch (IOException e) {
            showAlert("Error", "Could not open diagram view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the diagram if the window is open
     */
    private void updateDiagram() {
        if (diagramStage != null && diagramStage.isShowing()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CircuitBuilderView.fxml"));
                Parent root = loader.load();
                CircuitBuilderController controller = loader.getController();
                controller.setCircuit(circuit);
                diagramStage.getScene().setRoot(root);
            } catch (IOException e) {
                System.err.println("Could not update diagram: " + e.getMessage());
            }
        }
    }
    
    /**
     * Check if circuit has any components
     */
    private boolean hasComponents() {
        return !batteries.isEmpty() || !resistors.isEmpty() || !capacitors.isEmpty();
    }
    
    /**
     * Updates the View Diagram button state based on components
     */
    private void updateViewDiagramButton() {
        if (viewDiagramButton != null) {
            boolean hasComps = hasComponents();
            viewDiagramButton.setDisable(!hasComps);
            
            if (!hasComps) {
                viewDiagramButton.setText("📊 View Diagram (Add components first)");
            } else {
                viewDiagramButton.setText("📊 View Diagram");
            }
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}