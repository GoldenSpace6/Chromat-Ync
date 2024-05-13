package Canva;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class Frame extends Application {
	
	public void start(Stage stage) {
		Pane test = new Pane();
		test.setScaleX(100);
		test.setScaleY(100);
		
		test.setStyle("-fx-background-color:red;"
				+ "-fx-border-color:blue;"
				+ "-fx-border-width:30px");
		
		Scene scene = new Scene(test);
		stage.setTitle("Chromat-Ynk");
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args) ;
	}
}
