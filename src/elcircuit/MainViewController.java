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

    }

    @FXML
    void resetBtnPressed(ActionEvent event) {
        componentLayer.getChildren().clear();
        wireLayer.getChildren().clear();
        wires.clear();

        placedComponents.clear();
        circuit = new Circuit();

        voltageField.clear();
        currentField.clear();
        specialField.clear();
    }

    @FXML
    void startBtnPressed(ActionEvent event) {

    }

    @FXML
    void initialize() {
        System.out.println("Controller initialize()");
        setupWireDrawing();
        setupDragSources();
        setupDropTarget();

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

    private PlacedComponent findNearestComponent(Point2D pParent, double maxDistance) {
        PlacedComponent nearest = null;
        double bestDist = maxDistance;

        for (PlacedComponent pc : placedComponents) {
            Bounds bounds = pc.node.getBoundsInParent();
            double cx = bounds.getMinX() + bounds.getWidth() / 2;
            double cy = bounds.getMinY() + bounds.getHeight() / 2;

            double dx = pParent.getX() - cx;
            double dy = pParent.getY() - cy;
            double dist = Math.hypot(dx, dy);

            if (dist < bestDist) {
                bestDist = dist;
                nearest = pc;
            }
        }

        return nearest;
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
            currentWire.setStrokeWidth(3);
            currentWire.setStartX(x);
            currentWire.setStartY(y);
            currentWire.setEndX(x);
            currentWire.setEndY(y);

            componentLayer.getChildren().add(currentWire);
        });

        componentLayer.setOnMouseDragged(event -> {
            if (currentWire != null) {
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
            if (currentWire != null) {
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
                } else {
                    currentWire.setStroke(Color.DARKRED);
                    System.out.println("Wire not connected");
                }

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
                iv.setFitWidth(80);

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

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

}
