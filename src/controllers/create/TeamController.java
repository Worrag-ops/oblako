package controllers.create;

import java.net.URL;
import java.util.ResourceBundle;

import application.AutoCompleteComboBoxListener;
import entities.Discipline;
import entities.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Disciplines;
import storage.Teams;

public class TeamController implements Initializable {
	@FXML private ScrollPane scroll;
	@FXML private ComboBox<String> teamCombo;
	@FXML private TextField teamName;
	@FXML private Label teamError;
	@FXML private VBox create_t;
	@FXML private VBox main_vbox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		teamCombo.setItems(discps); 
		new AutoCompleteComboBoxListener<>(teamCombo);
    }
	
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void newTeam(ActionEvent event) {
		String name = teamName.getText();
		String teamDisc = teamCombo.getValue();
		
		if (name.equals("") || teamDisc == null) {
			teamError.setTextFill(Paint.valueOf("RED"));
			teamError.setText("Необходимо заполнить пустые поля");
			return;
		}
		Team t = new Team(name, Disciplines.getInstance().get(teamDisc));
		if (!t.save()) {
			teamError.setTextFill(Paint.valueOf("RED"));
			teamError.setText("Ошибка базы данных");
			return;			
		}
		Teams.getInstance().add(t);
		teamError.setTextFill(Paint.valueOf("GREEN"));
		teamError.setText("Команда успешно сохранена");
		teamName.setText("");
	}
}
