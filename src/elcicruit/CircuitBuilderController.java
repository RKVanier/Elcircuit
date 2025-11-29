package elcicruit;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Controller for displaying circuit diagram based on entered components
 * REPLACES the old interactive CircuitBuilderController
 */
public class CircuitBuilderController {

    @FXML private Canvas circuitCanvas;
    
    private Circuit circuit;
    
    /**
     * Sets the circuit data to display
     */
    public void setCircuit(Circuit circuit) {
        this.circuit = circuit;
        drawCircuit();
    }
    
    @FXML
    public void initialize() {
        // Canvas will be sized by FXML
    }
    
    /**
     * Draws the complete circuit diagram
     */
    private void drawCircuit() {
        if (circuit == null) return;
        
        GraphicsContext gc = circuitCanvas.getGraphicsContext2D();
        
        // Clear canvas
        gc.clearRect(0, 0, circuitCanvas.getWidth(), circuitCanvas.getHeight());
        
        // Set background
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, circuitCanvas.getWidth(), circuitCanvas.getHeight());
        
        // Calculate positions
        double centerX = circuitCanvas.getWidth() / 2;
        double centerY = circuitCanvas.getHeight() / 2;
        double circuitWidth = 400;
        double circuitHeight = 200;
        
        // Starting positions for series circuit
        double leftX = centerX - circuitWidth / 2;
        double rightX = centerX + circuitWidth / 2;
        double topY = centerY - circuitHeight / 2;
        double bottomY = centerY + circuitHeight / 2;
        
        // Draw wire frame (rectangle for series circuit)
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        // Calculate component spacing
        int totalComponents = circuit.getBatterys().size() + 
                            circuit.getResistors().size() + 
                            circuit.getCapacitors().size();
        
        if (totalComponents == 0) {
            drawNoComponentsMessage(gc, centerX, centerY);
            return;
        }
        
        // Draw top wire
        gc.strokeLine(leftX, topY, rightX, topY);
        
        // Draw bottom wire with components
        double currentX = leftX;
        double spacing = circuitWidth / (totalComponents + 1);
        
        // Draw left vertical wire
        gc.strokeLine(leftX, topY, leftX, bottomY);
        
        // Draw batteries
        for (Battery battery : circuit.getBatterys()) {
            currentX += spacing;
            drawBattery(gc, currentX, bottomY, battery.getEmf());
        }
        
        // Draw resistors
        for (Resistor resistor : circuit.getResistors()) {
            currentX += spacing;
            drawResistor(gc, currentX, bottomY, resistor.getResistance());
        }
        
        // Draw capacitors
        for (Capacitor capacitor : circuit.getCapacitors()) {
            currentX += spacing;
            drawCapacitor(gc, currentX, bottomY, capacitor.getCapacitance());
        }
        
        // Draw right vertical wire
        gc.strokeLine(rightX, topY, rightX, bottomY);
        
        // Draw connection wires at bottom
        gc.strokeLine(leftX, bottomY, leftX + spacing / 2, bottomY);
        
        currentX = leftX + spacing / 2;
        for (int i = 0; i < totalComponents; i++) {
            double nextX = currentX + spacing;
            if (i < totalComponents - 1) {
                gc.strokeLine(currentX + 40, bottomY, nextX - 40, bottomY);
            } else {
                gc.strokeLine(currentX + 40, bottomY, rightX, bottomY);
            }
            currentX = nextX;
        }
        
        // Draw calculated values
        drawResults(gc, centerX, topY - 50);
    }
    
    /**
     * Draws a battery symbol
     */
    private void drawBattery(GraphicsContext gc, double x, double y, Double emf) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        // Long line (positive)
        gc.strokeLine(x - 5, y - 20, x - 5, y + 20);
        // Short line (negative)
        gc.strokeLine(x + 5, y - 12, x + 5, y + 12);
        
        // Connection points
        gc.strokeLine(x - 40, y, x - 5, y);
        gc.strokeLine(x + 5, y, x + 40, y);
        
        // Label
        gc.setFill(Color.RED);
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        gc.fillText(String.format("%.1f V", emf), x - 20, y - 30);
        
        // Plus and minus signs
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 14));
        gc.fillText("+", x - 15, y);
        gc.fillText("-", x + 10, y);
    }
    
    /**
     * Draws a resistor symbol (zigzag)
     */
    private void drawResistor(GraphicsContext gc, double x, double y, Double resistance) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        // Connection wires
        gc.strokeLine(x - 40, y, x - 30, y);
        gc.strokeLine(x + 30, y, x + 40, y);
        
        // Zigzag pattern
        double[] xPoints = {
            x - 30, x - 22, x - 15, x - 8, x, x + 8, x + 15, x + 22, x + 30
        };
        double[] yPoints = {
            y, y - 12, y + 12, y - 12, y + 12, y - 12, y + 12, y - 12, y
        };
        gc.strokePolyline(xPoints, yPoints, 9);
        
        // Label
        gc.setFill(Color.BLUE);
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        gc.fillText(String.format("%.1f Ω", resistance), x - 20, y - 25);
    }
    
    /**
     * Draws a capacitor symbol (two parallel lines)
     */
    private void drawCapacitor(GraphicsContext gc, double x, double y, Double capacitance) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        
        // Left plate
        gc.strokeLine(x - 5, y - 18, x - 5, y + 18);
        // Right plate
        gc.strokeLine(x + 5, y - 18, x + 5, y + 18);
        
        // Connection wires
        gc.strokeLine(x - 40, y, x - 5, y);
        gc.strokeLine(x + 5, y, x + 40, y);
        
        // Label
        gc.setFill(Color.GREEN.darker());
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        String label = capacitance >= 0.001 ? 
            String.format("%.4f F", capacitance) : 
            String.format("%.2e F", capacitance);
        gc.fillText(label, x - 25, y - 25);
    }
    
    /**
     * Draws calculated results above the circuit
     */
    private void drawResults(GraphicsContext gc, double centerX, double y) {
        if (circuit.getEquivalentBattery() == null || 
            circuit.getEquivalentResistor() == null) {
            return;
        }
        
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.setFill(Color.BLACK);
        
        StringBuilder results = new StringBuilder();
        
        if (circuit.getEquivalentBattery().getEmf() != null && 
            circuit.getEquivalentBattery().getEmf() != 0) {
            results.append(String.format("Total EMF: %.2f V    ", 
                circuit.getEquivalentBattery().getEmf()));
        }
        
        if (circuit.getEquivalentResistor().getResistance() != null && 
            circuit.getEquivalentResistor().getResistance() != 0) {
            results.append(String.format("Total R: %.2f Ω    ", 
                circuit.getEquivalentResistor().getResistance()));
        }
        
        if (circuit.getEquivalentCapacitor() != null && 
            circuit.getEquivalentCapacitor().getCapacitance() != null && 
            circuit.getEquivalentCapacitor().getCapacitance() != 0) {
            results.append(String.format("Total C: %.6f F    ", 
                circuit.getEquivalentCapacitor().getCapacitance()));
        }
        
        if (circuit.getCurrent() != null && circuit.getCurrent() != 0) {
            results.append(String.format("Current: %.4f A", circuit.getCurrent()));
        }
        
        if (results.length() > 0) {
            gc.fillText(results.toString(), centerX - 200, y);
        }
    }
    
    /**
     * Shows message when no components are added
     */
    private void drawNoComponentsMessage(GraphicsContext gc, double centerX, double centerY) {
        gc.setFill(Color.GRAY);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 16));
        gc.fillText("No components added yet", centerX - 100, centerY - 10);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.fillText("Add components in the Circuit Calculator", centerX - 120, centerY + 15);
        gc.fillText("then click 'View Diagram' to see your circuit", centerX - 130, centerY + 35);
    }
}