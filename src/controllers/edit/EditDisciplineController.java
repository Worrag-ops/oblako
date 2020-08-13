package controllers.edit;

import java.io.IOException;
import java.net.URL;

import controllers.list.ListDisciplineController;
import entities.Discipline;
import h2.ConnectH2;
import h2.H2EntityIdExtractor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Disciplines;
import util.JavaFXUtil;

public class EditDisciplineController {

	@FXML TextField discName;
	@FXML ComboBox<String> discCombo; 
	@FXML Button cancelButton;
	@FXML Label errorLabel;
	
	private int disc_id; 
	private Discipline uneditedDiscipline;
	
	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
	}
	
	@FXML
	private void applyChanges() {
		String name = discName.getText();
		String type = discCombo.getValue();
		
		if (name.equals("") || type == null) {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("���������� ��������� ������ ����");
			return;
		}
		
		Discipline newDisc = new Discipline(disc_id, name, type);
		if(newDisc.update()) {
			uneditedDiscipline.setName(name);
			uneditedDiscipline.setDiscType(type);
		}
		refreshPrevWindow();
    	Stage stage = (Stage) discName.getScene().getWindow();
		stage.close();
	}
	
	public void setDisciplineFields(Discipline d) {
		String name = d.getName();
		discName.setText(name);
		discCombo.setValue(d.getDiscType());
		disc_id = d.getId();
		uneditedDiscipline = d;	
	}
	
	private void refreshPrevWindow() {
		ListDisciplineController.getStage().close();											
		JavaFXUtil.openWindow("fxml/list/listDiscipline.fxml", false);	
	}
	
}
