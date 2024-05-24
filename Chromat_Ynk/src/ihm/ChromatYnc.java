package ihm;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import interpreter.Interpreter;
import interpreter.InterpreterException;

/**
 * ChromatYnc is the abstraction class of the application with a PAC model
 * @author Ewen Dano
 */
public class ChromatYnc {

    private BooleanProperty isContinuous = new SimpleBooleanProperty(true);
    private BooleanProperty stopWhenException = new SimpleBooleanProperty(true);
    //* represents the delay waited between each instructions in seconds */
    protected DoubleProperty delayBetweenFrames = new SimpleDoubleProperty(0);      

    private StringProperty input = new SimpleStringProperty();
    private StringProperty output = new SimpleStringProperty();

    private ObjectProperty<File> fileToLoad = new SimpleObjectProperty<>();
    protected ObjectProperty<Canvas> canvas = new SimpleObjectProperty<>();

    private CursorController cursorController;



    // isContinuous accessors
    public Boolean getIsContinuous() {
        return isContinuous.get();
    }
    public void setIsContinuous(boolean isContinuous) {
        this.isContinuous.set(isContinuous);
    }
    public BooleanProperty isContinuousProperty() {
        return isContinuous;
    }

    // stopWhenException accessors
    public Boolean getStopWhenException() {
        return stopWhenException.get();
    }
    public void setStopWhenException(boolean stopWhenException) {
        this.stopWhenException.set(stopWhenException);
    }
    public BooleanProperty stopWhenExceptionProperty() {
        return stopWhenException;
    }

    // delayBetweenFrames accessors
    public Double getDelayBetweenFrames() {       
        return Double.valueOf(delayBetweenFrames.get());
    }
    public void setDelayBetweenFrames(double delayBetweenFrames) {
        this.delayBetweenFrames.set(delayBetweenFrames);
    }
    public DoubleProperty delayBetweenFramesProperty() {
        return delayBetweenFrames;
    }

    // input accessors
    public String getInput() {
        return input.get();
    }
    public void setInput(String input) {
        this.input.set(input);
    }
    public StringProperty inputProperty() {
        return input;
    }

    // output accessors
    public String getOutput() {
        return output.get();
    }
    public void setOutput(String output) {
        this.output.set(output);
    }
    public StringProperty outputProperty() {
        return output;
    }

    // fileToLoad accessors
    public File getFileToLoad() {
        return fileToLoad.get();
    }
    public void setFileToLoad(File fileToLoad) {
        this.fileToLoad.set(fileToLoad);
    }
    public ObjectProperty<File> fileToLoadProperty() {
        return fileToLoad;
    }

    // canvas accessors
    public Canvas getCanvas() {
        return canvas.get();
    }
    public void setCanvas(Canvas canvas) {
        this.canvas.set(canvas);
    }
    public ObjectProperty<Canvas> canvasProperty() {
        return canvas;
    }

    // cursorController accessors
    public CursorController getCursorController() {
        return cursorController;
    }
    public void setCursorController(CursorController cursorController) {
        this.cursorController = cursorController;
    }

    /**
     * Method used to send unitary instruction written in the inputTextField to be processed by the lexer
     * @param input Unitary instruction to process
     */
    public void processInput(String input) {
        cursorController.addCursor(0);
        cursorController.getCursors().get(0).get(0).fwd(50);   
        // SEND INPUT TO 
    }

    /**
     * Method used to send file of instructions to be processed by the lexer
     * @param fileToLoad Pathnames to the file to process
     */
    public void processFile(File fileToLoad) {
    	Interpreter interpreter = new Interpreter(fileToLoad, canvas.get(), output, cursorController);
    	//execute all;
    	try {
    		while(interpreter.getCurrentInstruction() != null) {
    			interpreter.nextStep();
    			try {
    				Thread.sleep((long)(delayBetweenFrames.get()*1000));
    			} catch (InterruptedException e) {
    				outputDisplay("ERROR");
    				Thread.currentThread().interrupt();
    			}
    		}
		} catch (InterpreterException e) {
			System.err.println(e.getMessage());
		}
    }

    /**
     * Method to call next instruction of a loaded file of instructions
     * @param 
     */
    public void nextInstruction() {

    }

    /**
     * Method to set the output text to a specific format : [time] : output text
     * Also print the output in the terminal
     * @param text text to output
     */
    public void outputDisplay(String text) {
        Date objDate = new Date();
        SimpleDateFormat objSDF = new SimpleDateFormat("HH:mm:ss");

        setOutput("[" + objSDF.format(objDate) + "] : " + text);
        System.out.println("Output : " + text);
    }



    public void debug() {
        GraphicsContext gc = canvas.get().getGraphicsContext2D();
        gc.fillText("Debug", 10, 50);
    }
}
