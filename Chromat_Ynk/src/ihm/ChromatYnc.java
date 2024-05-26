package ihm;


import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.canvas.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cursors.Cursor;
import interpreter.Interpreter;

/**
 * ChromatYnc is the abstraction class of the application with a PAC model
 * @author Ewen Dano
 */
public class ChromatYnc {
    /** represents the option Continuous and Step by Step, (true being Continuous)*/
    private BooleanProperty isContinuous = new SimpleBooleanProperty(true);
    /** boolean that decides if the interpreter stops when faced with an exception */
    private BooleanProperty stopWhenException = new SimpleBooleanProperty(true);
    /** represents the delay waited between each instructions in seconds */
    protected DoubleProperty delayBetweenFrames = new SimpleDoubleProperty(0);      

    private StringProperty input = new SimpleStringProperty();
    private StringProperty output = new SimpleStringProperty();

    private ObjectProperty<File> fileToLoad = new SimpleObjectProperty<>();
    protected ObjectProperty<Canvas> canvas = new SimpleObjectProperty<>();

    private CursorController cursorController;
    private ObservableList<Cursor> selectedCursor;

    private Interpreter interpreter;



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
    
    // selectedCursor accessors
    public ObservableList<Cursor> getSelectedCursor() {
        return selectedCursor;
    }
    public void setSelectedCursor(ObservableList<Cursor> selectedCursor) {
        this.selectedCursor = selectedCursor;
    }

    // interpreter accessors
    public Interpreter getInterpreter() {
        return interpreter;
    }
    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    /**
     * Method used to send unitary instruction written in the inputTextField to be processed by the lexer
     * @param input Unitary instruction to process
     */
    public void processInput(String input) {
        outputDisplay("processing input...");
        interpreter = new Interpreter(input, this);
        nextInstruction();
    }

    /**
     * Method used to send file of instructions to be processed by the lexer
     * @param fileToLoad Pathnames to the file to process
     */
    public void processFile(File fileToLoad) {
        interpreter = new Interpreter(fileToLoad, this);
        if (isContinuous.get()) {
            executeContinuesly();
        } else {
            nextInstruction();
        }
    }

    /**
     * Executes script continuesly
     */
    public void executeContinuesly() {
        if (interpreter != null) {
            //executeAll
        	while(interpreter.getCurrentInstruction() != null && this.isContinuousProperty().get() && Interpreter.isRunning()) {

        		nextInstruction();

        		try {
        			Thread.sleep((long)(this.delayBetweenFramesProperty().get()*1000));
       			} catch (InterruptedException e) {
       				outputDisplay("ERROR");
       				Thread.currentThread().interrupt();
       			}
        	}
        }
    }

    /**
     * Method to call next instruction of a loaded file of instructions or single instruction.
     * Receives exception from Interpreter.nextstep, stops interpretor if stopWhenException = true
     * @param 
     */
    public void nextInstruction() {
        if (interpreter != null && interpreter.getCurrentInstruction() != null) {
            try {
                interpreter.nextStep();
                /*if (exception != null) {
                    if (stopWhenException.get()) {
                        errorOutputDisplay( exception.getMessage() + ". (at : " + interpreter.getCurrentInstruction().toString() + ")");
                        if (exception instanceof InterpreterException) {
                            throw (InterpreterException) exception;
                        }
                        if (exception instanceof IllegalArgumentException) {
                            throw (IllegalArgumentException) exception;
                        }
                    } else {
                        errorOutputDisplay( exception.getMessage() + ". (skipped : " + interpreter.getCurrentInstruction().toString() + " : skipped)");
                        interpreter.setNextCurrentInstruction();	
                    }
                }*/
            } catch (Exception exception) {
                if (stopWhenException.get()) {
                    errorOutputDisplay( exception.getMessage() + ". (at : " + interpreter.getCurrentInstruction().toString() + ")");
                    Interpreter.stopProcessFileThread();
                } else {
                    errorOutputDisplay( exception.getMessage() + ". (skipped : " + interpreter.getCurrentInstruction().toString() + " : skipped)");
                }
            }
        }
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

    /**
     * Method to set the output error to a specific format : [time] : ERROR : output error
     * Also print the in the terminal err stream
     * @param text error to output 
     */
    public void errorOutputDisplay(String text) {
        Date objDate = new Date();
        SimpleDateFormat objSDF = new SimpleDateFormat("HH:mm:ss");

        setOutput("[" + objSDF.format(objDate) + "] : ERROR : " + text);
        System.err.println("Output : " + text);
    }
}
