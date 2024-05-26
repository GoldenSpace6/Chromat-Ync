package cursors;


import ihm.CursorController;
import interpreter.InterpreterException;
import interpreter.UserObjectValue;
import lexer.Command;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Abstract class with every cursors types common methods 
 */
public abstract class Cursor {
    protected int id;
    protected GraphicsContext gc;
    protected Canvas canvas;
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
    public void setRotation(double rotation) {
        this.rotation.set(rotation);
    }
    public DoubleProperty rotationProperty() {
        return rotation;
    }

    /**
     * Draws a line between Cursor's position and the point (x,y).
     * Sets color, alpha and width according to cursor's properties
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
    public abstract void turn(double value);

    // mov
    public abstract void mov(double x, double y);
    

    // pos
    public abstract void pos(double x, double y);

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
    public abstract void lookAtCursor(int id);
    
    // lookAtPos
    public abstract void lookAtPos(double xPos, double yPos);

    /**
     * Sets cursor's orientation towards specific coordinates (x,y)
     * @param xPos x coordinate to look at
     * @param yPos y coordinate to look at
     */ 
    public void lookAt(double xPos, double yPos) {
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
    

    /**
     * /**
     * Executes command given by the interpreter
     * @param c command
     * @param valueList list of arguments of the command
     * @throws InterpreterException
     */
    public void execCommand(Command c,UserObjectValue[] valueList) throws InterpreterException {
		String command = c.toString();

        switch (command) {
            case "FWD":
                Double valueFwd = valueList[0].getDouble();
                if (valueList[0].getIsPercentage()) {
                    valueFwd = valueFwd/100 * Math.max(canvas.getWidth(), canvas.getHeight());
                }
                fwd(valueFwd);  
                break;
            case "BWD":
                Double valueBwd = valueList[0].getDouble();
                if (valueList[0].getIsPercentage()) {
                    valueBwd = valueBwd/100 * Math.max(canvas.getWidth(), canvas.getHeight());
                }
                bwd(valueBwd);
                break;
            case "TURN":
                turn(valueList[0].getDouble());
                break;
            case "MOV":
                Double valueMov1 = valueList[0].getDouble();
                Double valueMov2 = valueList[1].getDouble();
                if (valueList[0].getIsPercentage()) {
                    valueMov1 = valueMov1/100 * canvas.getWidth();
                }
                if (valueList[1].getIsPercentage()) {
                    valueMov2 = valueMov2/100 * canvas.getHeight();
                }
                mov(valueMov1,valueMov2);
                break;
            case "POS":
                Double valuePos1 = valueList[0].getDouble();
                Double valuePos2 = valueList[1].getDouble();
                if (valueList[0].getIsPercentage()) {
                    valuePos1 = valuePos1/100 * canvas.getWidth();
                }
                if (valueList[1].getIsPercentage()) {
                    valuePos2 = valuePos2/100 * canvas.getHeight();
                }
                mov(valuePos1,valuePos2);
                pos(valuePos1,valuePos2);
                break;
            case "HIDE":
                hide();
                break;
            case "SHOW":
                show();
                break;
            case "PRESS":
                Double valuePress = valueList[0].getDouble();
                if (valueList[0].getIsPercentage()) {
                    valuePress = valuePress/100;
                }
                if (valuePress<0 || valuePress > 1) {
                    throw new InterpreterException("pressure value " + valuePress + " is out of bound");
                }
                if (valueList.length == 1) {
                    press(valuePress);
                }
                break;
            case "COLOR":
                if (valueList.length == 1) {
                    Color drawColorWEB = Color.web(valueList[0].getString());
                    color(drawColorWEB);
                } else if (valueList.length == 3) {
                    int red = valueList[0].getInt();
                    int green = valueList[1].getInt();
                    int blue = valueList[2].getInt();
                    if (red<0 || red>255 || green<0 || green>255 || blue<0 || blue>255) {
                        throw new InterpreterException("RGB color out of bound");
                    }
                    Color drawColorRGB = Color.rgb(red, green, blue);
                    color(drawColorRGB);
                }
                break;
            case "THICK":
                double thickValue = valueList[0].getDouble();
                if (thickValue<0) {
                    throw new InterpreterException("thickness value " + thickValue + " is out of bound");
                }
                thick(thickValue);
                break;
            case "LOOKAT":
                if (valueList.length == 1) {
                	int id = valueList[0].getInt();
                    if (!cursorController.getCursors().containsKey(id)) {
                        throw new InterpreterException("Cursor " + id + " does not exist");
                    }
                    lookAtCursor(id);     	
                } else if (valueList.length == 2) {
                    Double valueLookAt1 = valueList[0].getDouble();
                    Double valueLookAt2 = valueList[1].getDouble();
                    if (valueList[0].getIsPercentage()) {
                        valueLookAt1 = valueLookAt1/100 * canvas.getWidth();
                    }
                    if (valueList[1].getIsPercentage()) {
                        valueLookAt2 = valueLookAt2/100 * canvas.getHeight();
                    }
                    lookAtPos(valueLookAt1, valueLookAt2);
                }
                break;
            default:
                break;
        }
	}
    
}
