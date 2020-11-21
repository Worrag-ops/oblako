package controllers.edit;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.list.ListTeamController;
import entities.Bet;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storage.Bets;
import storage.Disciplines;
import storage.Teams;
import util.FileUtil;
import util.JavaFXUtil;
import util.StringUtil;

public class EditTeamController implements Initializable {

	@FXML TextField teamNameField;
	@FXML TextField logoPathField;
	@FXML ComboBox<String> discCombo;
	@FXML ImageView logoImage;
	@FXML Button logoPickButton;
	@FXML Button cancelButton;
	@FXML Label errorLabel;
	
	private int team_id; 
	private Team uneditedTeam;
	private File logoFile = null;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		JavaFXUtil.setCancelButton(cancelButton);
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		discCombo.setItems(discps); 
	}
	
	@FXML
	private void selectLogo(ActionEvent event) throws MalformedURLException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select logo");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));

		logoFile = fc.showOpenDialog(logoPickButton.getScene().getWindow());
        if (logoFile != null) {
            logoPathField.setText(logoFile.getAbsolutePath());
            logoImage.setImage(new Image(logoFile.toURI().toURL().toString()));
        }
		
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
		
		String logoPath = uneditedTeam.getLogoPath();
		
		if (logoFile != null) {
			String discConstructedName = StringUtil.constructDirectoryName(discName);
			File directory = new File(this.getClass().getResource("/").getPath() + "logo" + "/" + discConstructedName);
			if (!directory.exists()) {
				directory.mkdir();
			}
			String newlogoPath =  "logo" + "/" + discConstructedName + "/" + name + ".png";
			System.out.println(logoPath);
			File newLogo = new File(this.getClass().getResource("/").getPath() + newlogoPath);
			try {
				newLogo.createNewFile();
				FileUtil.copyFile(logoFile, newLogo);
			} catch (IOException e) {
				errorLabel.setTextFill(Paint.valueOf("RED"));
				errorLabel.setText("Ошибка сохранения лого");
				e.printStackTrace();
				return;
			}
	
			try {
				logoPath = newLogo.toURI().toURL().toString();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}
		}
		Team newTeam = new Team(team_id, name, d, profit, logoPath);
		
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
					errorLabel.setText("Невозможно изменить дисциплину команды, которая уже участвовала в матчах!");
					return;			
				}
			}
		}
		
		if(newTeam.update()){
			uneditedTeam.setName(name);
			uneditedTeam.setDiscipline(d);	
			uneditedTeam.setLogoPath(logoPath);	
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
		String logoPath = t.getLogoPath();
		if (logoPath != null && !logoPath.isEmpty()) {
			logoPathField.setText(t.getLogoPath());
			logoImage.setImage(new Image(t.getLogoPath()));			
		}
		discCombo.setValue(t.getDiscipline().getName());
		uneditedTeam = t;

	}
	
	private void refreshPrevWindow() {
		ListTeamController.getStage().close();											
		JavaFXUtil.openWindow("fxml/list/listTeam.fxml", false);	
	}
	
}
