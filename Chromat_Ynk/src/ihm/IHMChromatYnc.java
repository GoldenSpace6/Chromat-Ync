package ihm;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * IHMChromatYnc is the presentation class of the application with a PAC model
 * @author Ewen Dano
 */
public class IHMChromatYnc extends Application {

    private ChromatYnc chromatYnc;
    private CtrlChromatYnc ctrlChromatYnc;
    private Canvas canvas;
    private CursorController cursorController;
    private Task<Void> processFileTask;

    @Override
    public void start(Stage primaryStage) {
        chromatYnc = new ChromatYnc();
        canvas = new Canvas();
        chromatYnc.setCanvas(canvas);
        VBox.setVgrow(canvas, javafx.scene.layout.Priority.ALWAYS);
        ctrlChromatYnc = new CtrlChromatYnc(chromatYnc);
        cursorController = new CursorController(canvas);
        chromatYnc.setCursorController(cursorController);

        // Create BorderPane
        BorderPane borderPane = new BorderPane();
        
        // Create components of BorderPane
        Pane canvasContainer = new Pane();
        MenuBar menuBar = new MenuBar();
        VBox bottomPane = new VBox();

        // Create components of menuBar
        Menu imageMenu = new Menu("Image");
        Menu scriptMenu = new Menu("Script");

        // Create components of imageMenu
        MenuItem resetImageItem = new MenuItem("Reset Image");
        MenuItem saveImageItem = new MenuItem("Save Image");

        // Create components of scriptMenu
        MenuItem loadScriptItem = new MenuItem("Load Script");
        MenuItem nextFrameItem = new MenuItem("Next Frame [SPACE]");
        nextFrameItem.setDisable(true);
        Menu scriptDisplayModeMenu = new Menu("Script Display Mode");
        CheckMenuItem exceptionSettingItem = new CheckMenuItem("Stop When Exception");
        exceptionSettingItem.setSelected(true);

        // Create components of scriptDisplayModeMenu
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioMenuItem continuousItem = new RadioMenuItem("Continuous");
        continuousItem.setToggleGroup(toggleGroup);
        RadioMenuItem stepByStepItem = new RadioMenuItem("Step by step");
        stepByStepItem.setToggleGroup(toggleGroup);
        Menu setDelayMenu = new Menu("Delay between instructions (in s)");
        TextField numberInput = new TextField("0");
        numberInput.setPromptText("Enter a positive number");
        CustomMenuItem delayInputItem = new CustomMenuItem(numberInput);
        delayInputItem.setHideOnClick(false);

        // Create components of BottomPane
        HBox inputBox = new HBox();
        HBox outputBox = new HBox();

        // Create components of inputBox
        Label inputLabel = new Label(" Intput   ");
        TextField inputTextField = new TextField();
        HBox.setHgrow(inputTextField, Priority.ALWAYS);

        // Create components of outputBox
        Label outputLabel = new Label(" Output ");
        TextField outputTextField = new TextField();
        HBox.setHgrow(outputTextField, Priority.ALWAYS);
        outputTextField.setEditable(false);
        outputTextField.textProperty().bindBidirectional(chromatYnc.outputProperty());


        // add Events to components
        canvasContainer.widthProperty().addListener((obs, oldVal, newVal) -> canvas.setWidth(newVal.doubleValue())); // update canvas size
        canvasContainer.heightProperty().addListener((obs, oldVal, newVal) -> canvas.setHeight(newVal.doubleValue())); // update canvas size

        resetImageItem.setOnAction(e -> { 
            ctrlChromatYnc.resetCanvas();
            canvasContainer.getChildren().clear();
            canvasContainer.getChildren().add(canvas);
        }); // resetImageItem event    
        saveImageItem.setOnAction(e -> ctrlChromatYnc.saveCanvasToFile()); // saveImageItem event
        loadScriptItem.setOnAction(e -> {   // loadScriptItem event
            
            ctrlChromatYnc.chooseFileToLoad(processFileTask);
            inputTextField.getParent().requestFocus();
            if (!chromatYnc.getIsContinuous()) {
                nextFrameItem.setDisable(false);
            }
        }); 
        nextFrameItem.setOnAction(e -> {    // nextFrameItem event
            inputTextField.getParent().requestFocus();
            ctrlChromatYnc.nextFrame();
        } ); 
        continuousItem.setOnAction(e -> {   // continuousItem event
            ctrlChromatYnc.setIsContinuousTrue();
            nextFrameItem.setDisable(true);
        }); 
        stepByStepItem.setOnAction(e -> ctrlChromatYnc.setIsContinuousFalse()); // stepByStepItem event
        exceptionSettingItem.setOnAction(e -> {     // exceptionSettingItem event
            if (exceptionSettingItem.isSelected()) {
                ctrlChromatYnc.toggleStopWhenException(true);
            } else {
                ctrlChromatYnc.toggleStopWhenException(false);
            }
        });
        numberInput.setOnKeyPressed(event -> { // setDelay event
            if (event.getCode() == KeyCode.ENTER) {
                String inputValue = numberInput.getText();
                ctrlChromatYnc.setDelayBetweenFrames(inputValue);
                scriptMenu.hide();
            }
        });
        inputTextField.setOnKeyPressed(event -> { // input event
            if (event.getCode() == KeyCode.ENTER) {
                String inputValue = inputTextField.getText();
                ctrlChromatYnc.handleInput(inputValue);
                inputTextField.setText("");
            }
        });
        
        // Global event
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (processFileTask != null && processFileTask.isRunning()) {
                processFileTask.cancel();
                System.out.println("cancelled Thread processFileTask");
            }
        });
        


        // Add components to scriptDisplayModeMenu
        scriptDisplayModeMenu.getItems().addAll(continuousItem, stepByStepItem, new SeparatorMenuItem(), setDelayMenu);
        setDelayMenu.getItems().add(delayInputItem); // Add components to setDelayMenu

        // Add components to imageMenu
        imageMenu.getItems().addAll(resetImageItem, saveImageItem);

        // Add components to scriptMenu
        scriptMenu.getItems().addAll(loadScriptItem, nextFrameItem, new SeparatorMenuItem(), scriptDisplayModeMenu, exceptionSettingItem);

        // Add components to menuBar
        menuBar.getMenus().addAll(imageMenu, scriptMenu);

        // Add components to canvasContainer
        canvasContainer.getChildren().add(canvas);

        // Add components to inputBox
        inputBox.getChildren().addAll(inputLabel,inputTextField);

        // Add components to outputBox
        outputBox.getChildren().addAll(outputLabel,outputTextField);
        
        // Add components to BottomPane
        bottomPane.getChildren().addAll(inputBox,outputBox);

        // Add components to BorderPane
        borderPane.setCenter(canvasContainer);
        borderPane.setTop(menuBar);
        borderPane.setBottom(bottomPane);

        
        new CursorPresenter(canvasContainer, cursorController);

   
        
        // // Configuration and display of scene
        Scene scene = new Scene(borderPane, 1080, 720);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {  // global scene event
            if (event.getCode() == KeyCode.SPACE) {
                if (!nextFrameItem.isDisable()) {
                    ctrlChromatYnc.nextFrame();
                    event.consume();
                }      
            }
        });
    
        primaryStage.setTitle("ChromatYnc");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }


    public void debug() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillText("Debug", 10, 50);
    }
}
