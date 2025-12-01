package elcircuit;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.geometry.Bounds;

public class MainViewController {

    @FXML
    private ImageView gridBackground;
    @FXML
    private Pane wireLayer;
    @FXML
    private Pane componentLayer;

    @FXML
    private ImageView batteryIcon;
    @FXML
    private ImageView resistorIcon;
    @FXML
    private ImageView capacitorIcon;

    @FXML
    public void initialize() {
        drawBottomWire();
        setupDragSources();
        setupDropTarget();
    }

    private void drawBottomWire() {
        double width = gridBackground.getFitWidth();
        double y = 430;

        Line wire = new Line(20, y, width - 20, y);
        wire.setStroke(Color.WHITE);
        wire.setStrokeWidth(3);

        wireLayer.getChildren().add(wire);
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
//                iv.setFitWidth(80);

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
