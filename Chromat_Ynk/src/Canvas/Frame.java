package Canvas;

import javafx.application.Application;
import javafx.stage.Stage;
//import javafx.scene.Scene;
//import javafx.scene.layout.Pane;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.canvas.*;

public class Frame extends Application {
	
	public void start(Stage stage) {
		//Create A Canvas to draw on
		Group root = new Group();
		Scene scene = new Scene(root, 300, 300, Color.BLACK);
		
		final Canvas canvas = new Canvas(250,250);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		
		//Draw on the Canvas
		this.draw(gc);
		 
		//put the Canvas on Screen
		root.getChildren().add(canvas);
		stage.setTitle("Chromat-Ynk");
		stage.setScene(scene);
		stage.show();
	}
	public void draw(GraphicsContext gc ) {
		gc.setFill(Color.BLUE);
		gc.fillRect(75,75,100,100);
	}
	public static void main(String[] args) {
		launch(args) ;
	}
}