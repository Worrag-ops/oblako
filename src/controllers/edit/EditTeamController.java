package controllers.edit;

import java.math.BigDecimal;

import controllers.list.ListDisciplineController;
import controllers.list.ListTeamController;
import entities.Bet;
import entities.Discipline;
import entities.Team;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Bets;
import storage.Disciplines;
import storage.Teams;
import util.JavaFXUtil;

public class EditTeamController {

	@FXML TextField teamNameField;
	@FXML ComboBox<String> discCombo; 
	@FXML Button cancelButton;
	@FXML Label errorLabel;
	
	private int team_id; 
	private Team uneditedTeam;
	
	
	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		discCombo.setItems(discps); 
	}
	
	@FXML
	private void applyChanges() {
		String name = teamNameField.getText();
		String discName = discCombo.getValue();
		BigDecimal profit = uneditedTeam.getProfit();
		team_id = uneditedTeam.getId();
		
		if (name.equals("") || discName == null) {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		Discipline d = Disciplines.getInstance().get(discName);
		Team newTeam = new Team(team_id, name, d, profit);
		
		if (!uneditedTeam.equals(newTeam)) {
			for (Team t : Teams.getInstance().getAll()) {
				if (t.equals(newTeam)) {
					errorLabel.setTextFill(Paint.valueOf("RED"));
					errorLabel.setText("Команда с такими параметрами уже существует!");
					return;			
				}
			}
		}
		
		if (!uneditedTeam.getDiscipline().equals(newTeam.getDiscipline())) {
			for (Bet b : Bets.getInstance().getAll()) {
				if (b.getTeamFor().equals(uneditedTeam) || b.getTeamOpp().equals(uneditedTeam)) {
					errorLabel.setTextFill(Paint.valueOf("RED"));
					errorLabel.setText("Невозможно изменить дисциплину команды, которая уже участвовала в ставках!");
					return;			
				}
			}
		}
		
		if(newTeam.update()){
			uneditedTeam.setName(name);
			uneditedTeam.setDiscipline(d);			
			refreshPrevWindow();
	    	Stage stage = (Stage) teamNameField.getScene().getWindow();
			stage.close();
		} else {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Ошибка при изменении!");
			return;			
		}
	}
	
	public void setTeamFields(Team t) {
		String teamName = t.getName();
		teamNameField.setText(teamName);	
		discCombo.setValue(t.getDiscipline().getName());
		uneditedTeam = t;

	}
	
	private void refreshPrevWindow() {
		ListTeamController.getStage().close();											
		JavaFXUtil.openWindow("fxml/list/listTeam.fxml", false);	
	}
	
}
