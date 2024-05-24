package cursors;

import java.util.Arrays;

import ihm.CursorController;
import interpreter.UserObjectValue;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import lexer.Command;

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
        System.out.println("init rotation");
        double rotationFather = fatherCursor.getRotation();
        setRotation((rotationFather+180.0)%360.0); // rotate 180°
        System.out.println("finish init rotation");
    }


    public void execCommand(Command c,UserObjectValue[] valueList) {
		System.out.println(c.toString()+" "+Arrays.toString(valueList));
		String command = c.toString();

        switch (command) {
            case "FWD":
                fwd((double)valueList[0].getValue());  
                break;
            case "BWD":
                bwd((double)valueList[0].getValue());
                break;
            case "TURN":
                double turnValue = (double)valueList[0].getValue();
                turn(turnValue);
                break;
            case "MOV":
                double movValue1 = (double)valueList[0].getValue();
                double movValue2 = (double)valueList[1].getValue();
                mov(- movValue1, - movValue2);
                break;
            case "POS":
                double posValue1 = (double)valueList[0].getValue();
                double posValue2 = (double)valueList[1].getValue();
                pos(xCenter*2 - posValue1, yCenter*2 - posValue2);
                break;
            case "HIDE":
                hide();
                break;
            case "SHOW":
                show();
                break;
            case "PRESS":
                if (valueList.length == 1) {
                    press((Double)valueList[0].getValue());
                }
                break;
            case "COLOR":
                if (valueList.length == 1) {
                    Color drawColorWEB = Color.web(valueList[0].getValue().toString(), pressure);
                    color(drawColorWEB);
                } else if (valueList.length == 3) {
                    int red = 0;
                    int green = 0;
                    int blue = 0;
                    if (valueList[0].getValue() instanceof Double) {
                        red = ((Double) valueList[0].getValue()).intValue();
                    }
                    if (valueList[1].getValue() instanceof Double) {
                        green = ((Double) valueList[1].getValue()).intValue();
                    }
                    if (valueList[2].getValue() instanceof Double) {
                        blue = ((Double) valueList[2].getValue()).intValue();
                    }
                    Color drawColorRGB = Color.rgb(red, green, blue, pressure);
                    color(drawColorRGB);
                }
                break;
            case "THICK":
                thick((double)valueList[0].getValue());
                break;
            case "LOOKAT":
                if (valueList.length == 1) {
                    if (valueList[0].getValue() instanceof Double) {
                        int id = ((Double) valueList[0].getValue()).intValue();
                        lookAtCursor(id);
                    }
                } else if (valueList.length == 2) {
                    lookAtPos((double)valueList[0].getValue(), (double)valueList[1].getValue());
                }
                break;
            default:
                break;
        }
	}
}
