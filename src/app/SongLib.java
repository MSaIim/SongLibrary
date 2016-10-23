package app;
	
import controller.SongLibController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class SongLib extends Application 
{
	@Override
	public void start(Stage primaryStage) 
	{
		try
		{
			// Load the FXML file
			FXMLLoader loader = new FXMLLoader();   
			loader.setLocation(getClass().getResource("/app/SongLibrary.fxml"));
		
			AnchorPane root = (AnchorPane) loader.load();
			
			SongLibController songListDisplayController = loader.getController();
			songListDisplayController.start(primaryStage);
			
			Scene scene = new Scene(root,800,600);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.setTitle("Song Library");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/songlib.png")));
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) 
	{		
		launch(args);
	}
}
