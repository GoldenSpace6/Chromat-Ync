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
                turn(turnValue);
                break;
            case "MOV":
                double movValue1 = valueList[0].getDouble();
                double movValue2 = valueList[1].getDouble();
                mov(- movValue1, - movValue2);
                break;
            case "POS":
                double posValue1 = valueList[0].getDouble();
                double posValue2 = valueList[1].getDouble();
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
