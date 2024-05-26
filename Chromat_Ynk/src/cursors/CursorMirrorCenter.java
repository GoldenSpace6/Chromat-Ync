package cursors;

import ihm.CursorController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

/**
 * Cursor class used by instruction MIRROR (centered), its methods are specific to MIRROR (centered) cursor
 */
public class CursorMirrorCenter extends Cursor {
    // Center mirror coordinates
    private double xCenter;
    private double yCenter;

    /**
     * create a CursorMirror (center) object, initiate its position, rotation, color, pressure, alpha and thickness
     * @param canvas canvas where the cursor draws to
     * @param cursorController class that controls cursors
     * @param fatherCursor father cursor to mirror
     * @param xCenter x coordinate of point of mirror center
     * @param yCenter y coordinate of point of mirror center
     */
    public CursorMirrorCenter(Canvas canvas, CursorController cursorController, Cursor fatherCursor, double xCenter, double yCenter) {
        this.canvas = canvas;
        Platform.runLater(() -> {
            this.xCenter = xCenter;
            this.yCenter = yCenter;
            this.fatherCursor = fatherCursor;
            mirrorPos(fatherCursor.getX(),fatherCursor.getY());
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

    /** sets cursor rotation to mirror its father's rotation*/
    private void initRotation() {
        double rotationFather = fatherCursor.getRotation();
        rotation.set((rotationFather+180.0)%360.0); // rotate 180°
    }
  
    /** sets cursor position to mirror its father's position
     * @param xFather x coordinate of father cursor
     * @param yFather y coordinate of father cursor
    */
    private void mirrorPos(double xFather, double yFather) {
        double[] pos = getMirrorPos(xFather, yFather);
        x.set(pos[0]);
        y.set(pos[1]);
    }

    /** 
     * return the mirrored position of specific coordinate as a list
     * @param x x coordinate of position
     * @param y y coordinate of position
     * @return list of mirrored coordinate {x,y}
     */
    private double[] getMirrorPos(double x, double y) {
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
            mirrorPos(fatherCursor.getX(),fatherCursor.getY());
        });
    }

    // mirror center pos
    public void pos(double x, double y) {
        Platform.runLater(() -> {
            mirrorPos(fatherCursor.getX(),fatherCursor.getY());
        });
    }

    // mirror axial lookAtCursor
    public void lookAtCursor(int id) {
        Platform.runLater(() -> {
            initRotation();
        });
    }

    // mirror axial lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        Platform.runLater(() -> {
            initRotation();
        });
    }

}
