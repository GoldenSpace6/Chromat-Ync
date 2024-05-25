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
                turn(valueList[0].getDouble());
                break;
            case "MOV":
                mov(valueList[0].getDouble(),valueList[1].getDouble());
                break;
            case "POS":
                pos(valueList[0].getDouble(),valueList[1].getDouble());
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
