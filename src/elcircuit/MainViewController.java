package elcircuit;

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

public class MainViewController {

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
    private boolean simulationStarted = false;
    private PlacedComponent selectedComponent;
    private double time = 0.0;
    private javafx.animation.Timeline timer;
    private final List<Wire> wires = new ArrayList<>();
    private Circuit circuit = new Circuit();
    private final List<PlacedComponent> placedComponents = new ArrayList<>();
    private Line currentWire;

    @FXML // fx:id="batteryIcon"
    private ImageView batteryIcon; // Value injected by FXMLLoader

    @FXML // fx:id="capacitorIcon"
    private ImageView capacitorIcon; // Value injected by FXMLLoader

    @FXML // fx:id="circuitStack"
    private StackPane circuitStack; // Value injected by FXMLLoader

    @FXML // fx:id="componentLayer"
    private Pane componentLayer; // Value injected by FXMLLoader

    @FXML // fx:id="currentField"
    private TextField currentField; // Value injected by FXMLLoader

    @FXML // fx:id="exitButton"
    private Button exitButton; // Value injected by FXMLLoader

    @FXML // fx:id="gridBackground"
    private ImageView gridBackground; // Value injected by FXMLLoader

    @FXML // fx:id="pauseBtn"
    private Button pauseBtn; // Value injected by FXMLLoader

    @FXML // fx:id="resistorIcon"
    private ImageView resistorIcon; // Value injected by FXMLLoader

    @FXML // fx:id="specialField"
    private TextField specialField; // Value injected by FXMLLoader

    @FXML // fx:id="startBtn"
    private Button startBtn; // Value injected by FXMLLoader

    @FXML // fx:id="voltageField"
    private TextField voltageField; // Value injected by FXMLLoader

    @FXML // fx:id="wireLayer"
    private Pane wireLayer; // Value injected by FXMLLoader

    @FXML
    void exitButtonPressed(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void pauseBtnPressed(ActionEvent event) {
        timer.pause();
    }

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

        voltageField.setEditable(true);
        specialField.setEditable(true);
        currentField.setEditable(false);
    }

    @FXML
    void startBtnPressed(ActionEvent event) {
        simulationStarted = true;
        voltageField.clear();
        specialField.clear();
        voltageField.setEditable(false);
        specialField.setEditable(false);
        recomputeCircuit();
        timer.play();
    }

    @FXML
    void initialize() {
        setupWireDrawing();
        setupDragSources();
        setupDropTarget();

        timer = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.seconds(0.1),
                        e -> {
                            time += 0.1;

                            Resistor req = circuit.getEquivalentResistor();
                            Battery beq = circuit.getEquivalentBattery();

                            Double Req = (req != null) ? req.getResistance() : null;
                            Double Veq = (beq != null) ? beq.getEmf() : null;

                            if (Req == null || Req == 0.0 || circuit.getCapacitors().isEmpty()) {
                                return;
                            }

                            for (Capacitor c : circuit.getCapacitors()) {
                                c.updateVoltage(time, Req, Veq);
                            }

                            if (selectedComponent != null) {
                                showComponentInfo(selectedComponent);
                            }

                            if (Circuit.current != null) {
                                currentField.setText(String.format("%.3f A", Circuit.current));
                            }
                        }
                )
        );
        timer.setCycleCount(javafx.animation.Animation.INDEFINITE);

        voltageField.setEditable(true);
        specialField.setEditable(true);
        currentField.setEditable(false);

        assert batteryIcon != null : "fx:id=\"batteryIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert capacitorIcon != null : "fx:id=\"capacitorIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert circuitStack != null : "fx:id=\"circuitStack\" was not injected: check your FXML file 'FXML.fxml'.";
        assert componentLayer != null : "fx:id=\"componentLayer\" was not injected: check your FXML file 'FXML.fxml'.";
        assert currentField != null : "fx:id=\"currentField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert gridBackground != null : "fx:id=\"gridBackground\" was not injected: check your FXML file 'FXML.fxml'.";
        assert resistorIcon != null : "fx:id=\"resistorIcon\" was not injected: check your FXML file 'FXML.fxml'.";
        assert specialField != null : "fx:id=\"specialField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert voltageField != null : "fx:id=\"voltageField\" was not injected: check your FXML file 'FXML.fxml'.";
        assert wireLayer != null : "fx:id=\"wireLayer\" was not injected: check your FXML file 'FXML.fxml'.";
    }

    private void recomputeCircuit() {
        circuit.recalculateAll();

        if (Circuit.current != null) {
            currentField.setText(String.format("%.3f A", Circuit.current));
        } else {
            currentField.clear();
        }
    }

    private void applyFieldsToComponent(PlacedComponent pc) {
        if (pc == null || pc.model == null) {
            return;
        }

        String specialText = specialField.getText().trim();
        String voltageText = voltageField.getText().trim();

        try {
            if (pc.model instanceof Battery b) {
                if (!voltageText.isEmpty()) {
                    double emf = Double.parseDouble(voltageText);
                    b.setEmf(emf);
                }
                System.out.println("Battery updated: emf = " + b.getEmf());
            } else if (pc.model instanceof Resistor r) {
                if (!specialText.isEmpty()) {
                    double R = Double.parseDouble(specialText);
                    r.setResistance(R);
                }
                System.out.println("Resistor updated: R = " + r.getResistance());
            } else if (pc.model instanceof Capacitor c) {
                if (!specialText.isEmpty() & !voltageText.isEmpty()) {
                    double C = Double.parseDouble(specialText);
                    double V = Double.parseDouble(voltageText);
                    c.setCapacitance(C);
                    c.setVoltage(V);
                    System.out.println("Capacitor updated: C = " + c.getCapacitance());
                    System.out.println("Capacitor updated: V = " + c.getVoltage());
                } else if (!specialText.isEmpty()) {
                    double C = Double.parseDouble(specialText);
                    c.setCapacitance(C);
                    System.out.println("Capacitor updated: C = " + c.getCapacitance());
                }

            }

            recomputeCircuit();

        } catch (NumberFormatException ex) {
            System.out.println("Invalid number in text fields");
        }
    }

    private void enableComponentSelect(ImageView iv) {
        iv.setOnMouseClicked(e -> {
            if (e.getButton() != MouseButton.PRIMARY) {
                return;
            }

            for (PlacedComponent pc : placedComponents) {
                if (pc.node == iv) {

                    if (!simulationStarted) {
                        applyFieldsToComponent(pc);
                        System.out.println("Applied fields to " + pc.type);

                        voltageField.clear();
                        specialField.clear();
                        currentField.clear();

                    } else {
                        selectedComponent = pc;
                        showComponentInfo(pc);
                        System.out.println("Showing info for " + pc.type);
                    }

                    break;
                }
            }
        });
    }

    private void showComponentInfo(PlacedComponent pc) {
        voltageField.clear();
        specialField.clear();
        currentField.clear();

        if (pc == null || pc.model == null) {
            return;
        }

        if (pc.model instanceof Battery b) {
            if (b.getEmf() != null) {
                voltageField.setText(b.getEmf().toString() + " V");
            }
            if (Circuit.current != null) {
                currentField.setText(Circuit.current.toString() + " A");
            }
        } else if (pc.model instanceof Resistor r) {
            if (r.getResistance() != null) {
                specialField.setText(r.getResistance() + " Ω");
            }
            if (r.getVoltage() != null) {
                voltageField.setText(r.getVoltage().toString() + " V");
            }
            if (Circuit.current != null) {
                currentField.setText(Circuit.current.toString() + " A");
            }
        } else if (pc.model instanceof Capacitor c) {
            if (c.getCapacitance() != null) {
                specialField.setText(c.getCapacitance().toString() + " F");
            }
            if (c.getVoltage() != null) {
                voltageField.setText(c.getVoltage().toString() + " V");
            }
            if (Circuit.current != null) {
                currentField.setText(Circuit.current.toString() + " A");
            }
        }
    }

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

    private void enableComponentDelete(ImageView iv) {
        iv.setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.SECONDARY) {
                return;
            }

            e.consume();

            List<Wire> toRemove = new ArrayList<>();
            for (Wire w : wires) {
                if ((w.from != null && w.from.node == iv)
                        || (w.to != null && w.to.node == iv)) {

                    componentLayer.getChildren().remove(w.line);
                    toRemove.add(w);
                }
            }
            wires.removeAll(toRemove);

            componentLayer.getChildren().remove(iv);

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

    private void enableWireDelete(Line line) {
        line.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                componentLayer.getChildren().remove(line);
                wires.removeIf(w -> w.line == line);
                System.out.println("Wire removed");
            }
        });
    }

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

    private void setupWireDrawing() {
        componentLayer.setOnMousePressed(event -> {

            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }

            if (event.getTarget() instanceof ImageView) {
                return;
            }

            double paneW = componentLayer.getWidth();
            double paneH = componentLayer.getHeight();

            double x = event.getX();
            double y = event.getY();

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
                    currentWire.setStroke(Color.LIMEGREEN);
                    System.out.println("Wire connected: " + c1.type + " -> " + c2.type);

                    Wire w = new Wire(currentWire, c1, c2);
                    wires.add(w);
                } else {
                    currentWire.setStroke(Color.DARKRED);
                    System.out.println("Wire not connected");
                }

                enableWireDelete(currentWire);

                currentWire = null;

            }
        });
    }

    private void setupDragSources() {
        setupDragSource(batteryIcon, "BATTERY");
        setupDragSource(resistorIcon, "RESISTOR");
        setupDragSource(capacitorIcon, "CAPACITOR");
    }

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
