package elcircuit;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Bounds;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class MainViewController {

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

    @FXML // fx:id="gridBackground"
    private ImageView gridBackground; // Value injected by FXMLLoader

    @FXML // fx:id="resistorIcon"
    private ImageView resistorIcon; // Value injected by FXMLLoader

    @FXML // fx:id="specialField"
    private TextField specialField; // Value injected by FXMLLoader

    @FXML // fx:id="voltageField"
    private TextField voltageField; // Value injected by FXMLLoader

    @FXML // fx:id="wireLayer"
    private Pane wireLayer; // Value injected by FXMLLoader

    @FXML
    void initialize() {
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

    private void setupWireDrawing() {
        circuitStack.setOnMousePressed(event -> {
            double paneW = circuitStack.getWidth();
            double paneH = circuitStack.getHeight();

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

            wireLayer.getChildren().add(currentWire);
        });

        circuitStack.setOnMouseDragged(event -> {
            if (currentWire != null) {
                double paneW = circuitStack.getWidth();
                double paneH = circuitStack.getHeight();

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

        circuitStack.setOnMouseReleased(event -> {
            if (currentWire != null) {
                double paneW = circuitStack.getWidth();
                double paneH = circuitStack.getHeight();

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
                    wireLayer.getChildren().remove(currentWire);
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
        componentLayer.setOnDragOver(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasImage()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        componentLayer.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasImage()) {
                ImageView iv = new ImageView(db.getImage());
                iv.setPreserveRatio(true);

                componentLayer.getChildren().add(iv);

                componentLayer.applyCss();
                componentLayer.layout();

                Bounds b = iv.getBoundsInParent();
                double w = b.getWidth();
                double h = b.getHeight();

                double paneW = componentLayer.getWidth();
                double paneH = componentLayer.getHeight();

                double targetX = event.getX() - w / 2;
                double targetY = event.getY() - h / 2;

                if (targetX < 0) {
                    targetX = 0;
                }
                if (targetY < 0) {
                    targetY = 0;
                }
                if (targetX + w > paneW) {
                    targetX = paneW - w;
                }
                if (targetY + h > paneH) {
                    targetY = paneH - h;
                }

                iv.setLayoutX(targetX);
                iv.setLayoutY(targetY);

                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });

    }
}
