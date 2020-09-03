package controllers.create;

import java.net.URL;
import java.util.ResourceBundle;

import entities.MyCurrency;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Currencies;
import util.JavaFXUtil;

public class CurrencyController implements Initializable {
	
	@FXML private TextField currName;
	@FXML private TextField numericField;
	@FXML private CheckBox isMainBox;
	@FXML private Label currError;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		JavaFXUtil.setDoubleField(numericField);
    }
    
    @FXML
    private void newCurrency(ActionEvent event) {
		String name = currName.getText();
		
		if (name.equals("") || numericField.getText().equals("")) {
			currError.setTextFill(Paint.valueOf("RED"));
			currError.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		double eq = Double.valueOf(numericField.getText());
		boolean main = isMainBox.isSelected();
		
		if (main && eq != 1) {
			currError.setTextFill(Paint.valueOf("RED"));
			currError.setText("У основной валюты соотношение должно быть равно 1");
			return;
		}
		
		MyCurrency curr = new MyCurrency(name, main, eq);
		
		if (!curr.save()) {
			currError.setTextFill(Paint.valueOf("RED"));
			currError.setText("Ошибка базы данных");
			return;			
		}
		Currencies.getInstance().add(curr);
		
		if (main) {
			Currencies.getInstance().setNewDefault(curr);			
		}
		currError.setTextFill(Paint.valueOf("GREEN"));
		currError.setText("Валюта успешно создана");
		currName.setText("");
		numericField.setText("");
		isMainBox.setSelected(false);
    }
    
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
}
