package cursors;

import java.util.Arrays;

import ihm.CursorController;
import interpreter.UserObjectValue;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import lexer.Command;

public class CursorMimic extends Cursor {


    public CursorMimic(Canvas canvas, CursorController cursorController, Cursor fatherCursor) {
        Platform.runLater(() -> {
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
                turn((double)valueList[0].getValue());
                break;
            case "MOV":
                mov((double)valueList[0].getValue(),(double)valueList[1].getValue());
                break;
            case "POS":
                pos((double)valueList[0].getValue(),(double)valueList[1].getValue());
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
