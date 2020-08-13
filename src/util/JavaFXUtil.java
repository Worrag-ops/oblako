package util;

import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import controllers.Controller;
import controllers.list.ListDisciplineController;
import entities.Bet;
import entities.Bookmaker;
import entities.Discipline;
import entities.Team;
import entities.Tournament;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storage.Bets;
import storage.Bookmakers;
import storage.Disciplines;
import storage.Teams;
import storage.Tournaments;

public class JavaFXUtil {

	public static void setDoubleField(TextField tf) {
		tf.textProperty().addListener(new ChangeListener<String>() { //only double in coef field
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.matches("\\d+\\.?(\\d+)?") && !tf.getText().isEmpty()) {
                	tf.setText(oldValue);
                } 
                else 
                {
                	tf.setText(newValue);
                }
            }
        });
	}
	
	public static void openWindow(String path, boolean wait) {
		Pane page = null;
		FXMLLoader fxmlLoads = new FXMLLoader(JavaFXUtil.class.getClassLoader().getResource(path));
		try
		{
		   page = (Pane) fxmlLoads.load();
		}
		   catch (IOException exception)
		{
		   throw new RuntimeException(exception);
		} 
		Scene scene = new Scene(page);
		scene.getStylesheets().add(JavaFXUtil.class.getClassLoader().getResource("css/application.css").toExternalForm());
		Stage stage = new Stage(); 
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		if (wait) {
			stage.initModality(Modality.APPLICATION_MODAL); 
			stage.showAndWait();
		}
		else
			stage.show();		
	}
	
	public static void deleteWindow(Object ent, Pane pane) {
		String entName = ""; 
		String warningMessage = "Зависимостей нет. Удалить объект?";
		final int entId;
		int cntBets = 0;
		int cntTeams = 0;
		int cntTours = 0;
		List<Bet> depBets = new ArrayList<Bet>();
		List<Tournament> depTours = new ArrayList<Tournament>();
		List<Team> depTeams = new ArrayList<Team>();
		//define type of entity then looking for dependencies and forming warning message
		if(ent instanceof Discipline) { //DISCIPLINE
			entName = "дисциплину?";
			entId = 1;
			for (Bet bet : Bets.getInstance().getAll()) {
				if (bet.getDiscipline().equals(ent)) {
					cntBets++;
					depBets.add(bet);
				}
			}
			for (Team team : Teams.getInstance().getAll()) {
				if (team.getDiscipline().equals(ent)) {
					cntTeams++;
					depTeams.add(team);
				}
			}			
			for (Tournament tour : Tournaments.getInstance().getAll()) {
				for (Discipline d : tour.getDisciplines()) {
					if (d.equals(ent)) {
						cntTours++;
						depTours.add(tour);
						break;
					}
				}
			}	
			if (cntBets + cntTeams +  cntTours > 0) {
				warningMessage = "В этой дисциплине сделано " + String.valueOf(cntBets) + " ставок, в ней играет " + String.valueOf(cntTeams) + " команд и она относится к " + String.valueOf(cntTours) + " турнирам. Удалив её, зависящие ставки и команды будут удалены, а у турниров исчезнет эта дисциплина. Подтвердить удаление?"; 
			}
		} else if (ent instanceof Team) { //TEAM
			entName = "команду?";
			entId = 2;
			for (Bet bet : Bets.getInstance().getAll()) {
				if (bet.getTeamFor().equals(ent) || bet.getTeamOpp().equals(ent)) {
					depBets.add(bet);
					cntBets++;
				}
			}	
			if (cntBets > 0) {
				warningMessage = "Ставок с участием этой команды: " + String.valueOf(cntBets) + ". Удалив её, все эти ставки будут также удалены. Подтвердить удаление?"; 
			}
		} else if (ent instanceof Bookmaker) { //BOOKMAKER
			entName = "букмекера?";
			entId = 3;
			for (Bet bet : Bets.getInstance().getAll()) {
				if (bet.getBookmaker().equals(ent)) {
					depBets.add(bet);
					cntBets++;
				}
			}	
			if (cntBets > 0) {
				warningMessage = "Количество ставок, сделанного у этого букмекера: " + String.valueOf(cntBets) + ". Удалив его, все эти ставки будут также удалены. Подтвердить удаление?"; 
			}
		} else if (ent instanceof Tournament) { //TOURNAMENT
			entName = "турнир?";
			entId = 4;
			for (Bet bet : Bets.getInstance().getAll()) {
				if (bet.getTournament().equals(ent)) {
					depBets.add(bet);
					cntBets++;
				}
			}	
			if (cntBets > 0) {
				warningMessage = "Количество ставок на этом турнире: " + String.valueOf(cntBets) + ". Удалив его, все эти ставки будут также удалены. Подтвердить удаление?"; 
			}
		} else if (ent instanceof Bet) { //Bet
			entName = "ставку?";
			entId = 6;
			warningMessage = "Удалить ставку?"; 
		} else {	
			return;
		}
		
		Label title = new Label("Удалить " + entName);
		title.setStyle("-fx-font-size:32;");
		title.setPadding(new Insets(5, 0, 0, 0));
		Label message = new Label(warningMessage);
		message.setStyle("-fx-font-size:14;");		
		message.setPrefHeight(156);
		message.setPrefWidth(425);	
		message.setWrapText(true);
		Separator line = new Separator();
		line.setPrefWidth(200);
		HBox hb = new HBox(400);
		hb.setPadding(new Insets(0, 10, 15, 10));
		hb.setPrefHeight(40);
		hb.setPrefWidth(200);
		Button cancel = new Button("Отмена");
		cancel.setPrefWidth(90);
		cancel.setMinWidth(90);
		cancel.setCancelButton(true);
		setCancelButton(cancel);
		Button delete = new Button("Удалить");
		delete.setPrefWidth(90);
		delete.setMinWidth(90);
		delete.setOnMouseClicked(new EventHandler<MouseEvent>() {
	        @Override
	        public void handle(MouseEvent t) {
	 			String fxmlPath = "";
	        	switch (entId) {
	 	 			case 1: //DISCIPLINE	 	
	 	 				Discipline delDisc = (Discipline) ent;
	 	 				if (delDisc.delete()) { //Deleting all dependences
	 	 					Bets.getInstance().removeAll(depBets, true, false, true);
	 	 					Teams.getInstance().removeAll(depTeams);
	 	 					for (Tournament tour : depTours) {
	 	 						tour.getDisciplines().remove(delDisc);
	 	 					}
	 	 					Disciplines.getInstance().remove(delDisc);
	 	 				}
	 	 				fxmlPath = "fxml/list/listDiscipline.fxml";
	 	 				break;
	 	 			case 2: //Team	 	
	 	 				Team delTeam = (Team) ent;
	 	 				if (delTeam.delete()) { 
	 	 					Bets.getInstance().removeAll(depBets, true, false, true);
	 	 					Teams.getInstance().remove(delTeam);
	 	 				}
	 	 				fxmlPath = "fxml/list/listTeam.fxml";
	 	 				break;
	 	 			case 3: //Bookmaker 	
	 	 				Bookmaker delBookmaker = (Bookmaker) ent;
	 	 				if (delBookmaker.delete()) { 
	 	 					Bets.getInstance().removeAll(depBets, false, true, true);
	 	 					Bookmakers.getInstance().remove(delBookmaker);
	 	 				}
	 	 				fxmlPath = "fxml/list/listBookmaker.fxml";
	 	 				break;
	 	 			case 4: //Tournament 	
	 	 				Tournament delTournament = (Tournament) ent;
	 	 				if (delTournament.delete()) { 
	 	 					Bets.getInstance().removeAll(depBets, true, true, false);
	 	 					Tournaments.getInstance().remove(delTournament);
	 	 				}
	 	 				fxmlPath = "fxml/list/listTournament.fxml";
	 	 				break;
	 	 			case 6: //Bet 	
	 	 				Bet delBet = (Bet) ent;
	 	 				if (delBet.delete()) { 
	 	 					Bets.getInstance().remove(delBet, true, true, true);
	 	 				}
	 	 			    fxmlPath = "fxml/Scene.fxml";			
		 	            Stage stage = (Stage) delete.getScene().getWindow();
		 	        	stage.close();
		 	        	return;
	        	}
	 			Stage prevWindow = (Stage) pane.getScene().getWindow(); //refresh
	 			prevWindow.close();										//list
	 			openWindow(fxmlPath, false);							//window
            	Stage stage = (Stage) delete.getScene().getWindow();
        		stage.close();
	        }
	    });
		
		hb.getChildren().add(cancel);
		hb.getChildren().add(delete);
		
		VBox root = new VBox(15);
		root.setAlignment(Pos.TOP_CENTER);
		root.getChildren().add(title);
		root.getChildren().add(message);
		root.getChildren().add(line);
		root.getChildren().add(hb);
		root.setStyle("-fx-border-color: black; -fx-border-image-insets: 5; -fx-border-width: 1;");
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(JavaFXUtil.class.getClassLoader().getResource("css/application.css").toExternalForm());
		Stage window = new Stage();
		window.initStyle(StageStyle.UNDECORATED);
		window.setScene(scene);
		window.initModality(Modality.APPLICATION_MODAL); 
		window.showAndWait();
	}
	
	public static void setCancelButton(Button btn) {
		btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {		
            	Stage stage = (Stage) btn.getScene().getWindow();
        		stage.close();
            }
        });
	}
}
