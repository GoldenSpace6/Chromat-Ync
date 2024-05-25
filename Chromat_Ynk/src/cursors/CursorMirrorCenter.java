package cursors;

import ihm.CursorController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class CursorMirrorCenter extends Cursor {
    // Axial coordinates
    private double xCenter;
    private double yCenter;

    public CursorMirrorCenter(Canvas canvas, CursorController cursorController, Cursor fatherCursor, double xCenter, double yCenter) {
        Platform.runLater(() -> {
            this.xCenter = xCenter;
            this.yCenter = yCenter;
            this.fatherCursor = fatherCursor;
            this.x.set(xCenter*2 - fatherCursor.getX());
            this.y.set(yCenter*2 - fatherCursor.getY());
            initRotation();
            gc = canvas.getGraphicsContext2D();
            gc.setLineCap(StrokeLineCap.SQUARE);
            gc.setLineWidth(1);
            pressure = 1;
            thickness = 1;
            drawColor = Color.BLACK;
            this.cursorController = cursorController;
        });
    }

    public void initRotation() {
        double rotationFather = fatherCursor.getRotation();
        setRotation((rotationFather+180.0)%360.0); // rotate 180°
    }

    public double[] getMirrorPos(double x, double y) {
        double[] pos = new double[2];
        pos[0] = xCenter*2 - x;
        pos[1] = yCenter*2 - y;
        return pos;
    }
    // normal turn
    public void turn(double value) {
        Platform.runLater(() -> {
            rotation.set(rotation.get() + value);
            rotation.set(rotation.get() % 360);
        });
    }

    // mirror center mov
    public void mov(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(this.x.get() - x);
            this.y.set(this.y.get() - y);
        });
    }

    // mirror center pos
    public void pos(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(xCenter*2 - x);
            this.y.set(yCenter*2 - y);
        });
    }

    // mirror axial lookAtCursor
    public void lookAtCursor(int id) {
        double xPos = cursorController.getCursors().get(id).get(0).getX();
        double yPos = cursorController.getCursors().get(id).get(0).getY();
        double[] pos = getMirrorPos(xPos, yPos);
        lookAt(pos[0], pos[1]);
    }

    // mirror axial lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        double[] pos = getMirrorPos(xPos, yPos);
        lookAt(pos[0], pos[1]);
    }

}
