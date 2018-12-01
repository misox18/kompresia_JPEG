package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {

		Parent root = null;

		try {
			root = FXMLLoader.load(getClass().getResource("/application/Gui.fxml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}
}
