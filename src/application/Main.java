package application;
	
import java.sql.SQLException;

import entities.Bet;
import entities.Team;
import h2.ConnectH2;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import storage.Bets;
import storage.Bookmakers;
import storage.Currencies;
import storage.Disciplines;
import storage.Teams;
import storage.Tournaments;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;


public class Main extends Application {
	public static Stage primaryStage = null;
	
	@Override
	public void start(Stage stage) {
		try {
			primaryStage = stage;
			VBox root = (VBox) FXMLLoader.load(getClass().getClassLoader().getResource("fxml/Scene.fxml"));
			Scene scene = new Scene(root,1600,730);
			scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Better Bettor");
			primaryStage.setOnCloseRequest(e -> Platform.exit());
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ConnectH2.connect();
		try {
			ConnectH2.createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Disciplines.getInstance().restore();
		Teams.getInstance().restore();
		Tournaments.getInstance().restore();
		Currencies.getInstance().restore();
		Bookmakers.getInstance().restore();
		Bets.getInstance().restore();
		launch(args);
	}
}
