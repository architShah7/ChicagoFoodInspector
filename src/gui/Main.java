package gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.JFrame;

import modules.Load;

public class Main extends Application {
	public static void main(String[] args){
		try {
			//Load.update();
			Load.read(System.getProperty("user.dir") + "/data/inspections.cfi");
		} catch (Exception e) {
			System.err.println("An io error occured");
			System.exit(-1);
		} 

		launch(args);
	}
	
	public static Scene scene;

	@Override
	public void start(Stage stage) throws Exception {
		File main = new File(System.getProperty("user.dir") + "\\assets\\main.fxml");
		URL url = main.toURI().toURL();
		Parent root = FXMLLoader.load(url);
	    
        scene = new Scene(root, 1280, 720);
    
        stage.setTitle("Chicago Food Inspector");
        stage.setScene(scene);
        stage.show();
	}
}
