package ihm;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import cursors.Cursor;
import javafx.stage.FileChooser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * CtrlChromatYnc is the control class of the application with a PAC model
 * @author Ewen Dano
 */
public class CtrlChromatYnc {
    private ChromatYnc chromatYnc;
    private Canvas canvas;

    public CtrlChromatYnc(ChromatYnc chromatYnc) {
        this.chromatYnc = chromatYnc;
        canvas = chromatYnc.getCanvas();
    }

    // isContinuous controllers
    public void setIsContinuousTrue() {
        if (!chromatYnc.getIsContinuous()) {
            chromatYnc.setIsContinuous(true);
            output("set display mode to \"Continuous\"");
            Task<Void> processFileTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    chromatYnc.executeContinuesly();
                    return null;
                }
            };
            Thread fileProcessThread = new Thread(processFileTask);
            fileProcessThread.setDaemon(true);
            fileProcessThread.start();
        }
    }
    public void setIsContinuousFalse() {
        chromatYnc.setIsContinuous(false);
        output("set display mode to \"Step by Step\"");
    }

    // stopWhenException controllers
    public void toggleStopWhenException(boolean bool) {
        chromatYnc.setStopWhenException(bool);
        output("set \"Stop when excepetion\" to : " + chromatYnc.getStopWhenException());
    }

    // delayBetweenFrames controllers
    public void setDelayBetweenFrames(String delayInputValue) {
        try {
            double numberInput = Double.valueOf(delayInputValue);
            if (numberInput <0) {
                throw new NumberFormatException();
            }
            if (numberInput > 0 && numberInput < 0.001) {
                chromatYnc.setDelayBetweenFrames(0.001);
                output(numberInput + " is to small, set Delay between instructions to " + 0.001 + "s instead");
            } else {
                chromatYnc.setDelayBetweenFrames(numberInput);
                output("set Delay between instructions to " + numberInput + "s");
            }
        } catch (NumberFormatException e) {
            output("Error : incorrect value for delay between instructions (\"" +  delayInputValue  + "\" must be a positive number)");
        }
    }

    // input controllers
    public void handleInput(String input) {
        chromatYnc.processInput(input);    
    }

    // output controllers
    public void output(String text) {
        chromatYnc.outputDisplay(text);
    }

    // fileToLoad controllers
    public void chooseFileToLoad() {
        output("choosing file...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tous les fichiers", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        //chromatYnc.processFile(selectedFile);

        if (selectedFile != null) {
            output("processing file...");
            Task<Void> processFileTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    chromatYnc.processFile(selectedFile);
                    return null;
                }
            };
            Thread fileProcessThread = new Thread(processFileTask);
            fileProcessThread.setDaemon(true);
            fileProcessThread.start();
        }
    }

    // reset canvas
    public void resetCanvas() {
        output("reset image");
        //Interpreter.stopProcessFileThread(); 
        Platform.runLater(() -> {      
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        });
    }

    // save canvas to file
    public void saveCanvasToFile() {
        output("saving image...");

        WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
        canvas.snapshot(null, writableImage);
        PixelReader pixelReader = writableImage.getPixelReader();
        
        boolean isCanvaEmpty = true;
        for (int y = 0; y < writableImage.getHeight(); y++) {
            for (int x = 0; x < writableImage.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                if (!color.equals(Color.WHITE)) {
                    isCanvaEmpty = false;  // Find non-empty pixel
                }
            }
        }

        if (isCanvaEmpty) {
            output("cannot save image : image empty");
            return; // Exit method if canva empty
        } 

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Files PNG (*.png)", "*.png"));
        File file = fileChooser.showSaveDialog(null);


        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
                output("Image saved as " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // reset interpreter
    public void resetInterpreter() {
        output("reset interpreter");
        chromatYnc.setInterpreter(null);
        Platform.runLater(() -> {
            ObservableMap<Integer, ObservableList<Cursor>> cursors = chromatYnc.getCursorController().getCursors();
            cursors.clear();      
        });
    }

    // next frame
    public void nextFrame() {
        if (chromatYnc.getIsContinuous() == false) {
            chromatYnc.nextInstruction();
        }    
    }
}
