package elcircuit.elcircuit.Controllers;

import elcircuit.elcircuit.Models.Battery;
import elcircuit.elcircuit.Models.Capacitor;
import elcircuit.elcircuit.Models.Resistor;
import elcircuit.elcircuit.Models.Circuit;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

/**
 * Controller for the main ElCircuit simulator view.
 *
 * Responsibilities: - Handle drag-and-drop placement of batteries, resistors,
 * and capacitors - Draw and delete wires between placed components - Maintain a
 * Circuit model and update it from user input - Run a time-based RC simulation
 * using a Timeline - Display voltages, current, and capacitor charge in the
 * side panel
 *
 * @author Rayan
 */
public class MainViewController {

    /**
     * Helper class that bundles a placed component's type, its ImageView node,
     * and the underlying model object (Battery, Resistor, or Capacitor).
     */
    private static class PlacedComponent {
        
        String type;
        ImageView node;
        Object model;
        
        PlacedComponent(String type, ImageView node, Object model) {
            this.type = type;
            this.node = node;
            this.model = model;
        }
    }

    /**
     * Helper class representing a wire drawn between two placed components.
     */
    private static class Wire {
        
        Line line;
        PlacedComponent from;
        PlacedComponent to;
        
        Wire(Line line, PlacedComponent from, PlacedComponent to) {
            this.line = line;
            this.from = from;
            this.to = to;
        }
    }

    /**
     * Flag indicating whether the simulation has started (Start button
     * pressed).
     */
    private boolean simulationStarted = false;

    /**
     * Currently selected component when simulation is running (for side-panel
     * info).
     */
    private PlacedComponent selectedComponent;

    /**
     * Simulation time (seconds) incremented by the Timeline.
     */
    private double time = 0.0;

    /**
     * JavaFX timeline used to advance the simulation.
     */
    private javafx.animation.Timeline timer;

    /**
     * All wires currently drawn in the circuit area.
     */
    private final List<Wire> wires = new ArrayList<>();

    /**
     * Underlying circuit model (batteries, resistors, capacitors).
     */
    private Circuit circuit = new Circuit();

    /**
     * All components that have been dropped into the circuit area.
     */
    private final List<PlacedComponent> placedComponents = new ArrayList<>();

    /**
     * Line currently being drawn as the user drags to create a wire.
     */
    private Line currentWire;
    
    @FXML // fx:id="batteryIcon"
    private ImageView batteryIcon; // Palette icon for battery

    @FXML // fx:id="capacitorIcon"
    private ImageView capacitorIcon; // Palette icon for capacitor

    @FXML // fx:id="circuitStack"
    private StackPane circuitStack; // Root container for grid + layers

    @FXML // fx:id="componentLayer"
    private Pane componentLayer; // Holds placed component ImageViews and wires

    @FXML // fx:id="currentField"
    private TextField currentField; // Displays total circuit current

    @FXML // fx:id="exitButton"
    private Button exitButton; // Exit application

    @FXML // fx:id="gridBackground"
    private ImageView gridBackground; // Static grid image

    @FXML // fx:id="pauseBtn"
    private Button pauseBtn; // Pause simulation

    @FXML // fx:id="resetBtn"
    private Button resetBtn; // Reset simulation and clear circuit

    @FXML // fx:id="resistorIcon"
    private ImageView resistorIcon; // Palette icon for resistor

    @FXML // fx:id="specialField"
    private TextField specialField; // R or C depending on selected component

    @FXML // fx:id="specialField1"
    private TextField specialField1; // Capacitor charge Q when viewing a capacitor

    @FXML // fx:id="startBtn"
    private Button startBtn; // Start simulation

    @FXML // fx:id="timeField"
    private TextField timeField; // Value injected by FXMLLoader
    
    @FXML // fx:id="voltageField"
    private TextField voltageField; // Voltage for battery/resistor/capacitor

    @FXML // fx:id="wireLayer"
    private Pane wireLayer; // (Not used for logic; visual layer placeholder)

    /**
     * Handles the Exit button: closes the JavaFX application.
     *
     * @param event button click event
     */
    @FXML
    void exitButtonPressed(ActionEvent event) {
        Platform.exit();
    }

    /**
     * Handles the Pause button: pauses the simulation timer.
     *
     * @param event button click event
     */
    @FXML
    void pauseBtnPressed(ActionEvent event) {
        timer.pause();
    }

    /**
     * Handles the Reset button. Responsibilities: - Resets time and stops the
     * timer - Clears all components and wires from the canvas - Resets the
     * Circuit model - Clears text fields and re-enables editing
     *
     * @param event button click event
     */
    @FXML
    void resetBtnPressed(ActionEvent event) {
        time = 0.0;
        timer.stop();
        simulationStarted = false;
        
        componentLayer.getChildren().clear();
        wireLayer.getChildren().clear();
        
        wires.clear();
        placedComponents.clear();
        circuit = new Circuit();
        
        voltageField.clear();
        currentField.clear();
        specialField.clear();
        specialField1.clear();
        timeField.clear();
        
        voltageField.setEditable(true);
        specialField.setEditable(true);
        specialField1.setEditable(true);
        currentField.setEditable(false);
    }

    /**
     * Handles the Start button. Responsibilities: - Marks the simulation as
     * started - Clears side-panel fields and disables editing - Recomputes the
     * circuit equivalent values - Starts the simulation timer
     *
     * @param event button click event
     */
    @FXML
    void startBtnPressed(ActionEvent event) {
        simulationStarted = true;
        voltageField.clear();
        specialField.clear();
        specialField1.clear();
        
        voltageField.setEditable(false);
        specialField.setEditable(false);
        specialField1.setEditable(false);
        
        recomputeCircuit();
        timer.play();
    }

    /**
     * Initializes the controller after FXML loading.
     *
     * Sets up: - Wire drawing handlers on the component layer - Drag-and-drop
     * sources (palette icons) and drop target - The simulation Timeline that
     * advances time and updates current and voltages - Initial editable state
     * of text fields - Assertions to verify FXML injection
     */
    @FXML
    void initialize() {
        setupWireDrawing();
        setupDragSources();
        setupDropTarget();

        // Timeline step every 0.1 s
        timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(0.1),
                        e -> {
                            time += 0.1;

                            // Compute equivalent battery voltage
                            double Veq = 0.0;
                            for (Battery b : circuit.getBatterys()) {
                                if (b.getEmf() != null) {
                                    Veq += b.getEmf();
                                }
                            }

                            // Compute equivalent resistance
                            double Req = 0.0;
                            for (Resistor r : circuit.getResistors()) {
                                if (r.getResistance() != null) {
                                    Req += r.getResistance();
                                }
                            }

                            // No resistance -> no valid current computation
                            if (Req == 0) {
                                Circuit.setCurrent(null);
                                currentField.clear();
                                return;
                            }
                            
                            double I;

                            // If there is at least one capacitor, use RC behaviour
                            if (!circuit.getCapacitors().isEmpty()) {
                                for (Capacitor c : circuit.getCapacitors()) {
                                    c.updateVoltage(time, Req, Veq);
                                }

                                // Use the first capacitor's voltage as Vc
                                Capacitor c0 = circuit.getCapacitors().get(0);
                                Double Vc = c0.getVoltage();
                                if (Vc == null) {
                                    Vc = 0.0;
                                }

                                // I = (Veq - Vc) / Req for series RC
                                I = (Veq - Vc) / Req;
                            } else {
                                // Pure resistive circuit
                                I = Veq / Req;
                            }
                            
                            Circuit.setCurrent(I);

                            // Update voltage across each resistor (Ohm's law)
                            for (Resistor r : circuit.getResistors()) {
                                if (r.getResistance() != null) {
                                    r.calculateVoltage(I);
                                }
                            }
                            
                            currentField.setText(String.format("%.4f A", Math.abs(I)));

                            // If a component is selected, refresh its displayed info
                            if (simulationStarted && selectedComponent != null) {
                                showComponentInfo(selectedComponent);
                            }
                            
                            //Updates the text field with time
                            timeField.setText(String.valueOf(time));
                            
                        }
                )
        );
        timer.setCycleCount(javafx.animation.Animation.INDEFINITE);
        
        voltageField.setEditable(true);
        specialField.setEditable(true);
        specialField1.setEditable(true);
        currentField.setEditable(false);

        // FXML injection checks
        assert batteryIcon != null : "fx:id=\"batteryIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert capacitorIcon != null : "fx:id=\"capacitorIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert circuitStack != null : "fx:id=\"circuitStack\" was not injected: check your FXML file 'FXML.fxml'.";
        assert componentLayer != null : "fx:id=\"componentLayer\" was not injected: check your FXML file 'FXML.fxml'.";
        assert currentField != null : "fx:id=\"currentField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert exitButton != null : "fx:id=\"exitButton\" was not injected: check your FXML file 'FXML.fxml'.";
        assert gridBackground != null : "fx:id=\"gridBackground\" was not injected: check your FXML file 'FXML.fxml'.";
        assert pauseBtn != null : "fx:id=\"pauseBtn\" was not injected: check your FXML file 'FXML.fxml'.";
        assert resetBtn != null : "fx:id=\"resetBtn\" was not injected: check your FXML file 'FXML.fxml'.";
        assert resistorIcon != null : "fx:id=\"resistorIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert specialField != null : "fx:id=\"specialField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert specialField1 != null : "fx:id=\"specialField1\" was not injected: check your FXML file 'FXML.fxml'.";
        assert startBtn != null : "fx:id=\"startBtn\" was not injected: check your FXML file 'FXML.fxml'.";
        assert timeField != null : "fx:id=\"timeField\" was not injected: check your FXML file 'MainView.fxml'.";
        assert voltageField != null : "fx:id=\"voltageField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert wireLayer != null : "fx:id=\"wireLayer\" was not injected: check your FXML file 'FXML.fxml'.";
    }

    /**
     * Recomputes equivalent values in the circuit model and updates the current
     * text field.
     */
    private void recomputeCircuit() {
        circuit.recalculateAll();
        
        if (Circuit.getCurrent() != null) {
            currentField.setText(String.format("%.3f A", Circuit.getCurrent()));
        } else {
            currentField.clear();
        }
    }

    /**
     * Reads values from the side-panel text fields and applies them to the
     * given placed component's model (battery, resistor, or capacitor).
     *
     * This is used before the simulation has started when the user clicks a
     * component to confirm the entered values.
     *
     * @param pc placed component to update
     */
    private void applyFieldsToComponent(PlacedComponent pc) {
        if (pc == null || pc.model == null) {
            return;
        }
        
        String specialText = specialField.getText().trim();
        String voltageText = voltageField.getText().trim();
        
        try {
            if (pc.model instanceof Battery b) {
                if (!voltageText.isEmpty()) {
                    double emf = Math.abs(Double.parseDouble(voltageText));
                    b.setEmf(emf);
                }
                System.out.println("Battery updated: emf = " + b.getEmf());
            } else if (pc.model instanceof Resistor r) {
                if (!specialText.isEmpty()) {
                    double R = Math.abs(Double.parseDouble(specialText));
                    r.setResistance(R);
                }
                System.out.println("Resistor updated: R = " + r.getResistance());
            } else if (pc.model instanceof Capacitor c) {
                if (!specialText.isEmpty() & !voltageText.isEmpty()) {
                    double C = Math.abs(Double.parseDouble(specialText));
                    double V = Math.abs(Double.parseDouble(voltageText));
                    c.setCapacitance(C);
                    c.setVoltage(V);
                    System.out.println("Capacitor updated: C = " + c.getCapacitance());
                    System.out.println("Capacitor updated: V = " + c.getVoltage());
                } else if (!specialText.isEmpty()) {
                    double C = Math.abs(Double.parseDouble(specialText));
                    c.setCapacitance(C);
                    System.out.println("Capacitor updated: C = " + c.getCapacitance());
                }
            }

            // After changing component values, recompute the circuit
            recomputeCircuit();
            
        } catch (NumberFormatException ex) {
            System.out.println("Invalid number in text fields");
        }
    }

    /**
     * Enables selection behaviour for a placed component ImageView.
     *
     * Before simulation: clicking applies field values to that component. After
     * simulation: clicking shows that component's info on the side panel.
     *
     * @param iv ImageView representing a placed component
     */
    private void enableComponentSelect(ImageView iv) {
        iv.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) {
                return;
            }
            
            for (PlacedComponent pc : placedComponents) {
                if (pc.node == iv) {
                    
                    if (!simulationStarted) {
                        // Design time: apply entered values to model
                        applyFieldsToComponent(pc);
                        System.out.println("Applied fields to " + pc.type);
                        
                        voltageField.clear();
                        specialField.clear();
                        currentField.clear();
                        
                    } else {
                        // Simulation time: show live info for selected component
                        selectedComponent = pc;
                        showComponentInfo(pc);
                        System.out.println("Showing info for " + pc.type);
                    }
                    
                    break;
                }
            }
        });
    }

    /**
     * Displays the details of the given placed component in the side-panel
     * fields.
     *
     * Shows: - Battery: emf - Resistor: resistance and voltage across it -
     * Capacitor: capacitance, voltage, and charge Q - Circuit current I
     *
     * @param pc component whose info should be shown
     */
    private void showComponentInfo(PlacedComponent pc) {
        voltageField.clear();
        specialField.clear();
        specialField1.clear();
        currentField.clear();
        
        if (pc == null || pc.model == null) {
            return;
        }
        
        if (pc.model instanceof Battery b) {
            if (b.getEmf() != null) {
                voltageField.setText(b.getEmf().toString() + " V");
            }
        } else if (pc.model instanceof Resistor r) {
            if (r.getResistance() != null) {
                specialField.setText(r.getResistance().toString() + " \u03A9");
            }
            if (r.getVoltage() != null) {
                voltageField.setText(r.getVoltage().toString() + " V");
            }
        } else if (pc.model instanceof Capacitor c) {
            if (c.getCapacitance() != null) {
                specialField.setText(c.getCapacitance().toString() + " F");
            }
            if (c.getVoltage() != null) {
                voltageField.setText(c.getVoltage().toString() + " V");
            }
            if (c.getCharge() != null) {
                specialField1.setText(c.getCharge().toString() + " C");
            }
        }
        
        if (Circuit.getCurrent() != null) {
            currentField.setText(String.format("%.4f A", Circuit.getCurrent()));
        }
    }

    /**
     * Factory method that creates a model object for the given palette type
     * string.
     *
     * @param type "BATTERY", "RESISTOR", or "CAPACITOR"
     * @return new model instance, or {@code null} if type is unknown
     */
    private Object createModelForType(String type) {
        if (type == null) {
            System.out.println("createModelForType: type is null");
            return null;
        }
        
        return switch (type) {
            case "BATTERY" ->
                new Battery(5.0);
            case "RESISTOR" ->
                new Resistor(100.0, 0.0, 0.0);
            case "CAPACITOR" ->
                new Capacitor(1e-6, 0.0, 0.0);
            default ->
                null;
        };
    }

    /**
     * Enables right-click deletion for a placed component.
     *
     * Removes: - The component ImageView - All wires connected to it - The
     * underlying model from the Circuit
     *
     * @param iv ImageView representing the component
     */
    private void enableComponentDelete(ImageView iv) {
        iv.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.SECONDARY) {
                return;
            }
            
            e.consume();

            // Remove any wires connected to this component
            List<Wire> toRemove = new ArrayList<>();
            for (Wire w : wires) {
                if ((w.from != null && w.from.node == iv)
                        || (w.to != null && w.to.node == iv)) {
                    
                    componentLayer.getChildren().remove(w.line);
                    toRemove.add(w);
                }
            }
            wires.removeAll(toRemove);

            // Remove the component ImageView from the layer
            componentLayer.getChildren().remove(iv);

            // Remove model from the circuit
            Object model = iv.getUserData();
            placedComponents.removeIf(pc -> pc.node == iv);
            
            if (model instanceof Battery b) {
                circuit.getBatterys().remove(b);
            } else if (model instanceof Resistor r) {
                circuit.getResistors().remove(r);
            } else if (model instanceof Capacitor c) {
                circuit.getCapacitors().remove(c);
            }
            
            System.out.println("Component removed");
        });
    }

    /**
     * Enables right-click deletion for a wire line.
     *
     * @param line wire line to enable deletion on
     */
    private void enableWireDelete(Line line) {
        line.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                componentLayer.getChildren().remove(line);
                wires.removeIf(w -> w.line == line);
                System.out.println("Wire removed");
            }
        });
    }

    /**
     * Adds a newly created model object to the correct list in the circuit
     * based on the type string.
     *
     * @param type component type ("BATTERY", "RESISTOR", "CAPACITOR")
     * @param model model instance to add
     */
    private void addModelToCircuit(String type, Object model) {
        if (model == null || type == null) {
            return;
        }
        
        switch (type) {
            case "BATTERY" ->
                circuit.getBatterys().add((Battery) model);
            case "RESISTOR" ->
                circuit.getResistors().add((Resistor) model);
            case "CAPACITOR" ->
                circuit.getCapacitors().add((Capacitor) model);
        }
    }

    /**
     * Finds a placed component whose ImageView bounds contain the given point.
     * Used when finishing a wire to see which components the wire connects.
     *
     * @param p point in the component layer's coordinate system
     * @return matching PlacedComponent, or {@code null} if none hit
     */
    private PlacedComponent findComponentAt(Point2D p) {
        for (PlacedComponent pc : placedComponents) {
            Bounds bounds = pc.node.getBoundsInParent();
            if (bounds.contains(p)) {
                System.out.println("Hit " + pc.type);
                return pc;
            }
        }
        return null;
    }

    /**
     * Configures mouse handlers on the component layer to allow users to draw
     * wires by clicking and dragging on empty space.
     *
     * Wires start on mouse press, update on drag, and finalize on release,
     * possibly connecting two components.
     */
    private void setupWireDrawing() {
        componentLayer.setOnMousePressed(event -> {
            
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            // Do not start a wire when clicking directly on a component
            if (event.getTarget() instanceof ImageView) {
                return;
            }
            
            double paneW = componentLayer.getWidth();
            double paneH = componentLayer.getHeight();
            
            double x = event.getX();
            double y = event.getY();

            // Clamp to pane bounds
            if (x < 0) {
                x = 0;
            }
            if (y < 0) {
                y = 0;
            }
            if (x > paneW) {
                x = paneW;
            }
            if (y > paneH) {
                y = paneH;
            }
            
            currentWire = new Line();
            currentWire.setStroke(Color.WHITE);
            currentWire.setStrokeWidth(5);
            currentWire.setStartX(x);
            currentWire.setStartY(y);
            currentWire.setEndX(x);
            currentWire.setEndY(y);
            
            componentLayer.getChildren().add(currentWire);
        });
        
        componentLayer.setOnMouseDragged(event -> {
            if (currentWire != null && event.getButton() == MouseButton.PRIMARY) {
                double paneW = componentLayer.getWidth();
                double paneH = componentLayer.getHeight();
                
                double x = event.getX();
                double y = event.getY();

                // Clamp to pane bounds
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
                if (x > paneW) {
                    x = paneW;
                }
                if (y > paneH) {
                    y = paneH;
                }
                
                currentWire.setEndX(x);
                currentWire.setEndY(y);
            }
        });
        
        componentLayer.setOnMouseReleased(event -> {
            if (currentWire != null && event.getButton() == MouseButton.PRIMARY) {
                double paneW = componentLayer.getWidth();
                double paneH = componentLayer.getHeight();
                
                double x = event.getX();
                double y = event.getY();

                // Clamp to pane bounds
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
                if (x > paneW) {
                    x = paneW;
                }
                if (y > paneH) {
                    y = paneH;
                }
                
                currentWire.setEndX(x);
                currentWire.setEndY(y);
                
                double dx = currentWire.getEndX() - currentWire.getStartX();
                double dy = currentWire.getEndY() - currentWire.getStartY();
                // Ignore very short lines (accidental clicks)
                if (Math.hypot(dx, dy) < 5) {
                    componentLayer.getChildren().remove(currentWire);
                    currentWire = null;
                    return;
                }
                
                Point2D start = new Point2D(currentWire.getStartX(), currentWire.getStartY());
                Point2D end = new Point2D(currentWire.getEndX(), currentWire.getEndY());
                
                PlacedComponent c1 = findComponentAt(start);
                PlacedComponent c2 = findComponentAt(end);
                
                if (c1 != null && c2 != null && c1 != c2) {
                    // Valid connection
                    currentWire.setStroke(Color.LIMEGREEN);
                    System.out.println("Wire connected: " + c1.type + " -> " + c2.type);
                    
                    Wire w = new Wire(currentWire, c1, c2);
                    wires.add(w);
                } else {
                    // Wire not connected to two components
                    currentWire.setStroke(Color.DARKRED);
                    System.out.println("Wire not connected");
                }
                
                enableWireDelete(currentWire);
                
                currentWire = null;
                
            }
        });
    }

    /**
     * Sets up drag detection on each palette icon (battery, resistor,
     * capacitor).
     */
    private void setupDragSources() {
        setupDragSource(batteryIcon, "BATTERY");
        setupDragSource(resistorIcon, "RESISTOR");
        setupDragSource(capacitorIcon, "CAPACITOR");
    }

    /**
     * Configures a single palette ImageView as a drag source.
     *
     * When dragging starts, the image and a type string are placed on the
     * dragboard.
     *
     * @param source palette ImageView
     * @param type associated model type ("BATTERY", "RESISTOR", "CAPACITOR")
     */
    private void setupDragSource(ImageView source, String type) {
        source.setOnDragDetected(event -> {
            Dragboard db = source.startDragAndDrop(TransferMode.COPY);
            
            ClipboardContent content = new ClipboardContent();
            content.putImage(source.getImage());
            content.putString(type);
            db.setContent(content);
            
            event.consume();
        });
    }

    /**
     * Configures the circuit stack as a drag target so that palette components
     * can be dropped into the circuit area.
     *
     * On drop, an ImageView is created at the mouse position, a model instance
     * is created and stored, and the component is added to the circuit.
     */
    private void setupDropTarget() {
        circuitStack.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });
        
        circuitStack.setOnDragDropped(event -> {
            
            Dragboard db = event.getDragboard();
            boolean success = false;
            
            if (db.hasImage()) {
                ImageView iv = new ImageView(db.getImage());
                iv.setPreserveRatio(true);

                // Convert scene coordinates to the component layer's coordinates
                Point2D p = componentLayer.sceneToLocal(event.getSceneX(), event.getSceneY());
                double paneW = componentLayer.getWidth();
                double paneH = componentLayer.getHeight();
                
                double w;
                double h;
                
                componentLayer.getChildren().add(iv);
                componentLayer.applyCss();
                componentLayer.layout();
                Bounds b = iv.getBoundsInParent();
                w = b.getWidth();
                h = b.getHeight();
                
                double x = p.getX() - w / 2;
                double y = p.getY() - h / 2;

                // Clamp position so the image stays fully inside the pane
                if (x < 0) {
                    x = 0;
                }
                if (y < 0) {
                    y = 0;
                }
                if (x + w > paneW) {
                    x = paneW - w;
                }
                if (y + h > paneH) {
                    y = paneH - h;
                }
                
                iv.setLayoutX(x);
                iv.setLayoutY(y);
                
                String type = db.getString();
                Object model = createModelForType(type);
                addModelToCircuit(type, model);
                
                iv.setUserData(model);
                placedComponents.add(new PlacedComponent(type, iv, model));
                
                enableComponentDelete(iv);
                enableComponentSelect(iv);
                
                success = true;
            }
            
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
