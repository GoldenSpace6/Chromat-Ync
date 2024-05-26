package cursors;

import ihm.CursorController;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

public class CursorMirrorAxial extends Cursor {
    // Axial coordinates
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public CursorMirrorAxial(Canvas canvas, CursorController cursorController, Cursor fatherCursor, double x1, double y1 , double x2, double y2) {
        this.canvas = canvas;
        Platform.runLater(() -> {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.fatherCursor = fatherCursor;
            mirrorPos(fatherCursor.getX(), fatherCursor.getY());    // initiate position
            initRotation();                                         // initiate rotation 
            gc = canvas.getGraphicsContext2D();
            gc.setLineCap(StrokeLineCap.SQUARE);
            gc.setLineWidth(1);
            pressure = 1;
            thickness = 1;
            drawColor = Color.BLACK;
            this.cursorController = cursorController;
            
        });
    }

    public void mirrorPos(double xFather, double yFather) {      
        double[] pos = getMirrorPos(xFather, yFather);       
        x.set(pos[0]);
        y.set(pos[1]);
    }

    public double[] getMirrorPos(double x, double y) {
        double[] pos = new double[2];
        double a = y2 - y1;
        double b = x1 - x2;
        double c = x2*y1 - x1*y2;

        pos[0] = x - (2*a*(a*x + b*y + c)/ (a*a +b*b) );
        pos[1] = y - (2*b*(a*x + b*y + c)/ (a*a +b*b) );

        return pos;
    }

    public void initRotation() {
        double newRotation;
        double rotationFather = fatherCursor.getRotation();
        double rotationAxe;

        if (x1 > x2) {
            if (y1 >= y2) {
                rotationAxe = 90 + Math.toDegrees(Math.atan((y1-y2)/(x1-x2)));
            } else {
                rotationAxe = Math.toDegrees(Math.atan((x1-x2)/(y2-y1)));
            }
        } else {
            if (y1 > y2) {
                rotationAxe = 180 + Math.toDegrees(Math.atan((x2-x1)/(y1-y2)));
            } else {
                rotationAxe = 270 + Math.toDegrees(Math.atan((y2-y1)/(x2-x1)));
            }
        }

        newRotation = (-rotationFather + 2*rotationAxe)%360;
        rotation.set(newRotation);
    }

    // mirror axial turn
    public void turn(double value) {
        Platform.runLater(() -> {
            rotation.set(rotation.get() - value);
            rotation.set(rotation.get() % 360);
        });
    }

    // mirror axial mov
    public void mov(double x, double y) {
        Platform.runLater(() -> {
            mirrorPos(fatherCursor.getX(),fatherCursor.getY());
        });
    }

    // mirror axial pos
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
        //double xPos = cursorController.getCursors().get(id).get(0).getX();
        //double yPos = cursorController.getCursors().get(id).get(0).getY();
        //double[] pos = getMirrorPos(xPos, yPos);
        //lookAt(pos[0], pos[1]);
    }

    // mirror axial lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        Platform.runLater(() -> {
            initRotation();
        });
        //double[] pos = getMirrorPos(xPos, yPos);
        //lookAt(pos[0], pos[1]);
    }

}
