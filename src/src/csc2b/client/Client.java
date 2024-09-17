package src.csc2b.client;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
		//create the ClientPane, set up the Scene and Stage
        Scene scene =  new Scene(new BUKAClientPane(), 1200, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
	}
}
