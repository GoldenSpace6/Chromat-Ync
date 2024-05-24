package cursors;


import ihm.CursorController;
import interpreter.UserObjectValue;
import lexer.Command;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class Cursor {
    protected int id;
    protected GraphicsContext gc;
    protected CursorController cursorController;
    protected Cursor fatherCursor;

    protected DoubleProperty x = new SimpleDoubleProperty();
    protected DoubleProperty y = new SimpleDoubleProperty();
    protected BooleanProperty isVisible = new SimpleBooleanProperty(true);
    /** Rotation in degrees, 0 being UP and 90 is RIGHT */
    protected DoubleProperty rotation = new SimpleDoubleProperty();
    protected double pressure;
    protected double thickness;
    protected Color drawColor;

    // id accessors
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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
        gc.setStroke(drawColor);
        gc.setGlobalAlpha(pressure);
        gc.setLineWidth(thickness);
        gc.strokeLine(this.x.get(),this.y.get(),x,y);
    }

    // fwd
    public void fwd(double value) {
        Platform.runLater(() -> {
            double newX = x.get();
            double newY = y.get();
            double rotationRadians = rotation.get() * Math.PI / 180.0;

            newX +=  Math.sin(rotationRadians) * value;
            newY -= Math.cos(rotationRadians) * value;
            
            drawLine(newX, newY);
            x.set(newX);
            y.set(newY);
        });
    }

    // bwd
    public void bwd(double value) {
        Platform.runLater(() -> {
            double newX = x.get();
            double newY = y.get();
            double rotationRadians = rotation.get() * Math.PI / 180.0;

            newX -=  Math.sin(rotationRadians) * value;
            newY += Math.cos(rotationRadians) * value;
            
            drawLine(newX, newY);
            x.set(newX);
            y.set(newY);
        });
    }

    // turn
    public void turn(double value) {
        Platform.runLater(() -> {
            rotation.set(rotation.get() + value);
            rotation.set(rotation.get() % 360);
        });
    }

    // mov
    public void mov(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(this.x.get() + x);
            this.y.set(this.y.get() + y);
        });
    }

    // pos
    public void pos(double x, double y) {
        Platform.runLater(() -> {
            this.x.set(x);
            this.y.set(y);
        });
    }

    // hide
    public void hide() {
        Platform.runLater(() -> {
            isVisible.set(false);
        });
    }

    // show
    public void show() {
        Platform.runLater(() -> {
            isVisible.set(true);
        });
    }

    // press
    public void press(double pressure) {
        Platform.runLater(() -> {
            this.pressure = pressure;
        }); 
    }

    // color
    public void color(Color drawColor) {
        Platform.runLater(() -> {
            this.drawColor = drawColor;
        });
    }

    // thick
    public void thick(double thickness) {
        Platform.runLater(() -> {
            this.thickness = thickness;
        });
    }

    // lookAtCursor
    public void lookAtCursor(int id) {
        Platform.runLater(() -> {
            double xPos = cursorController.getCursors().get(id).get(0).getX();
            double yPos = cursorController.getCursors().get(id).get(0).getY();
            lookAtPos(xPos, yPos);
        });
    }

    // lookAtPos
    public void lookAtPos(double xPos, double yPos) {
        Platform.runLater(() -> {
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
        });
    }


    public abstract void execCommand(Command c,UserObjectValue[] valueList);
}
