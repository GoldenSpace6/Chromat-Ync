package ihm;

import java.util.Arrays;

import interpreter.UserObjectValue;
import lexer.Command;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.shape.StrokeLineCap;

public class Cursor {
    private GraphicsContext gc;
    private DoubleProperty x = new SimpleDoubleProperty();
    private DoubleProperty y = new SimpleDoubleProperty();
    private BooleanProperty isVisible = new SimpleBooleanProperty(true);
    /** Rotation in degrees, 0 being UP and 90 is RIGHT */
    private DoubleProperty rotation = new SimpleDoubleProperty();
    private double pressure;

    public Cursor(double x, double y, Canvas canvas) {
        this.x.set(x);
        this.y.set(y);
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineWidth(1);
        rotation.set(0);
        pressure = 1;
    }
    // x accessors
    public double getX() {
        return x.get();
    }
    public void setX(double x) {
        this.x.set(x);
    }
    public DoubleProperty xProperty() {
        return x;
    }

    // y accessors
    public double getY() {
        return y.get();
    }
    public void setY(double y) {
        this.y.set(y);
    }
    public DoubleProperty yProperty() {
        return y;
    }

    // isVisible accessors
    public boolean getIsVisible() {
        return isVisible.get();
    }
    public void setIsVisible(boolean isVisible) {
        this.isVisible.set(isVisible);
    }
    public BooleanProperty isVisibleProperty() {
        return isVisible;
    }

    // rotation accessors
    public double getRotation() {
        return rotation.get();
    }
    public void setRotation(double rotation) { // useless
        this.rotation.set(rotation);
    }
    public DoubleProperty rotationProperty() {
        return rotation;
    }

    /**
     * Draws a line between Cursor's position and the point (x,y)
     * @param x X coordinate of the ending point of the line
     * @param y Y coordinate of the ending point of the line
     */
    public void drawLine(double x, double y) {
        gc.strokeLine(this.x.get(),this.y.get(),x,y);
    }

    // fwd
    public void fwd(double value) {
        double newX = x.get();
        double newY = y.get();
        double rotationRadians = rotation.get() * Math.PI / 180.0;

        newX +=  Math.sin(rotationRadians) * value;
        newY -= Math.cos(rotationRadians) * value;
        
        drawLine(newX, newY);
        x.set(newX);
        y.set(newY);
    }

    // bwd
    public void bwd(double value) {
        double newX = x.get();
        double newY = y.get();
        double rotationRadians = rotation.get() * Math.PI / 180.0;

        newX -=  Math.sin(rotationRadians) * value;
        newY += Math.cos(rotationRadians) * value;
        
        drawLine(newX, newY);
        x.set(newX);
        y.set(newY);
    }

    // turn
    public void turn(double value) {
        rotation.set(rotation.get() + value);
        rotation.set(rotation.get() % 360);
    }

    // mov
    public void mov(double x, double y) {
        this.x.set(this.x.get() + x);
        this.y.set(this.y.get() + y);
    }

    // pos
    public void pos(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    // hide
    public void hide() {
        isVisible.set(false);
    }

    // show
    public void show() {
        isVisible.set(true);
    }

    // press
    public void press(double value) {
        this.pressure = value;
    }

    // color
    public void color(Paint color) {
        gc.setStroke(color);
    }

    // thick
    public void thick(double value) {
        gc.setLineWidth(value);
    }

    // lookAtCursor

    // lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        double xCursor = x.get();
        double yCursor = y.get();

        if (xPos > xCursor) {
            if (yPos >= yCursor) {
                rotation.set(90 + Math.toDegrees(Math.atan((yPos-yCursor)/(xPos-xCursor))));
            } else {
                rotation.set(Math.toDegrees(Math.atan((xPos-xCursor)/(yCursor-yPos))));
            }
        } else {
            if (yPos > yCursor) {
                rotation.set(180 + Math.toDegrees(Math.atan((xCursor-xPos)/(yPos-yCursor))));
            } else {
                rotation.set(270 + Math.toDegrees(Math.atan((yCursor-yPos)/(xCursor-xPos))));
            }
        }
        rotation.set(rotation.get() % 360);
    }


    public void execCommand(Command c,UserObjectValue[] valueList) {
		//System.out.println(c.toString()+" "+Arrays.toString(valueList));
		String command = c.toString();

        if (command.equals("FWD")) {
            fwd((double)valueList[0].getValue());
        }
        if (command.equals("BWD")) {
            bwd((double)valueList[0].getValue());
        }
        if (command.equals("TURN")) {
            turn((double)valueList[0].getValue());
        }
        if (command.equals("MOV")) {
            mov((double)valueList[0].getValue(),(double)valueList[1].getValue());
        }
        if (command.equals("POS")) {
            mov((double)valueList[0].getValue(),(double)valueList[1].getValue());
        }
        if (command.equals("HIDE")) {
            hide();
        }
        if (command.equals("SHOW")) {
            show();
        }
        if (command.equals("THICK")) {
            thick((double)valueList[0].getValue());
        }
	}
}
