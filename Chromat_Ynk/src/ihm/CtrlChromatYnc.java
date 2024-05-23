package ihm;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.stage.FileChooser;
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
        chromatYnc.setIsContinuous(true);
        output("set display mode to \"Continuous\"");
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
            if (numberInput <0.05) {
                chromatYnc.setDelayBetweenFrames(0.05);
                output(numberInput + " is to small, set Delay between instructions to " + 0.05 + "s instead");
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
        output("processing input...");
    }

    // output controllers
    public void output(String text) {
        chromatYnc.outputDisplay(text);
    }

    // fileToLoad controllers
    public void chooseFileToLoad(Task<Void> processFileTask) {
        output("choosing file...");

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Tous les fichiers", "*.*");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            if (chromatYnc.getIsContinuous() && chromatYnc.getDelayBetweenFrames()==0) {
                chromatYnc.processFile(selectedFile);    
            } else {
                output("processing file...");
                processFileTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        chromatYnc.processFile(selectedFile);
                        return null;
                    }
                };
                new Thread(processFileTask).start();
            }
        }
    }

    // reset canvas
    public void resetCanvas() {
        output("reset image");
        chromatYnc.setCursorController(new CursorController(chromatYnc.getCanvas()));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
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

    // next frame
    public void nextFrame() {
        if (chromatYnc.getIsContinuous() == false) {
            output("displaying next frame");
            chromatYnc.nextInstruction();
        }    
    }
}
