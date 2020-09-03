package controllers.create;

import java.net.URL;
import java.util.ResourceBundle;

import entities.Discipline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Disciplines;

public class DisciplineController implements Initializable {
	@FXML private ScrollPane discipline_create;
	@FXML private ComboBox<String> discCombo;
	@FXML private TextField discName;
	@FXML private Label discError;
	
	Stage create_stage = new Stage();
	
	//Create new discipline
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}
	
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void newDiscipline(ActionEvent event) {
		String name = discName.getText();
		String type = discCombo.getValue();
		
		if (name.equals("") || type == null) {
			discError.setTextFill(Paint.valueOf("RED"));
			discError.setText("Необходимо заполнить пустые поля");
			return;
		}
		Discipline d = new Discipline(name, type);
		if (!d.save()) {
			discError.setTextFill(Paint.valueOf("RED"));
			discError.setText("Ошибка базы данных");
			return;			
		}
		Disciplines.getInstance().add(d);
		discError.setTextFill(Paint.valueOf("GREEN"));
		discError.setText("Дисциплина успешно сохранена");
		discName.setText("");
	}
	
	
}
