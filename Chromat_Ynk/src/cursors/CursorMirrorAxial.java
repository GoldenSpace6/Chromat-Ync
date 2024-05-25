package cursors;

import java.util.Arrays;

import ihm.CursorController;
import interpreter.InterpreterException;
import interpreter.UserObjectValue;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import lexer.Command;

public class CursorMirrorAxial extends Cursor {
    // Axial coordinates
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public CursorMirrorAxial(Canvas canvas, CursorController cursorController, Cursor fatherCursor, double x1, double y1 , double x2, double y2) {
        Platform.runLater(() -> {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.fatherCursor = fatherCursor;
            initPos();
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

    public void initPos() {
        double xFather = fatherCursor.getX();
        double yFather = fatherCursor.getY();
        mirrorPos(xFather, yFather);
    }

    public void mirrorPos(double xFather, double yFather) {      
        // axe line equation : ax + bx +c = 0
        double a = y2 - y1;
        double b = x1 - x2;
        double c = x2*y1 - x1*y2;
        
        x.set(xFather - (2*a*(a*xFather + b*yFather + c)/ (a*a +b*b) ));
        y.set(yFather - (2*b*(a*xFather + b*yFather + c)/ (a*a +b*b) ));
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
        setRotation(newRotation);
    }


    public void execCommand(Command c,UserObjectValue[] valueList) throws InterpreterException {
		System.out.println(c.toString()+" "+Arrays.toString(valueList));
		String command = c.toString();

        switch (command) {
            case "FWD":
                fwd(valueList[0].getDouble());  
                break;
            case "BWD":
                bwd(valueList[0].getDouble());
                break;
            case "TURN":
                double turnValue = valueList[0].getDouble();
                turn(- turnValue);
                break;
            case "MOV":
                double movValue1 = valueList[0].getDouble();
                double movValue2 = valueList[1].getDouble();
                mov(- movValue1, - movValue2);
                break;
            case "POS":
                mirrorPos(valueList[0].getDouble(),valueList[1].getDouble());
                break;
            case "HIDE":
                hide();
                break;
            case "SHOW":
                show();
                break;
            case "PRESS":
                if (valueList.length == 1) {
                    press(valueList[0].getDouble());
                }
                break;
            case "COLOR":
                if (valueList.length == 1) {
                    Color drawColorWEB = Color.web(valueList[0].getString(), pressure);
                    color(drawColorWEB);
                } else if (valueList.length == 3) {
                    int red = valueList[0].getInt();
                    int green = valueList[1].getInt();
                    int blue = valueList[2].getInt();
                    Color drawColorRGB = Color.rgb(red, green, blue);
                    color(drawColorRGB);
                }
                break;
            case "THICK":
                thick(valueList[0].getDouble());
                break;
            case "LOOKAT":
                if (valueList.length == 1) {
                	int id = valueList[0].getInt();
                    lookAtCursor(id);
                } else if (valueList.length == 2) {
                    lookAtPos(valueList[0].getDouble(), valueList[1].getDouble());
                }
                break;
            default:
                break;
        }
	}
}
