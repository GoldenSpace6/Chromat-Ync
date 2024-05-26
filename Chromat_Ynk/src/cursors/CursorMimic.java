package cursors;

import ihm.CursorController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class CursorMimic extends Cursor {


    public CursorMimic(Canvas canvas, CursorController cursorController, Cursor fatherCursor) {
        this.canvas = canvas;
        Platform.runLater(() -> {
            this.fatherCursor = fatherCursor;
            this.x.set(fatherCursor.getX());
            this.y.set(fatherCursor.getY());
            rotation.set(fatherCursor.getRotation());

            gc = canvas.getGraphicsContext2D();
            gc.setLineCap(StrokeLineCap.SQUARE);
            gc.setLineWidth(1);           
            pressure = 1;
            thickness = 1;
            drawColor = Color.BLACK;
            this.cursorController = cursorController;
        });
    }

    // normal turn
    public void turn(double value) {
        Platform.runLater(() -> {
            rotation.set(rotation.get() + value);
            rotation.set(rotation.get() % 360);
        });
    }

    // normal mov
    public void mov(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(this.x.get() + x);
            this.y.set(this.y.get() + y);
        });
    }

    // normal mov
    public void pos(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(x);
            this.y.set(y);
        });
    }

    // mimic lookAtCursor
    public void lookAtCursor(int id) {
        Platform.runLater(() -> {
            rotation.set(fatherCursor.getRotation()); // mimic father orientation
        });
    }

    // mimic lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        Platform.runLater(() -> {
            rotation.set(fatherCursor.getRotation()); // mimic father orientation
        });
    }
}
