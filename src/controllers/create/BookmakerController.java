package controllers.create;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import application.AutoCompleteComboBoxListener;
import entities.Bookmaker;
import entities.MyCurrency;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Bookmakers;
import storage.Currencies;
import util.JavaFXUtil;

public class BookmakerController implements Initializable {
	
	@FXML private TextField bookName;
	@FXML private TextField numericField;
	@FXML private ComboBox<String> bookCombo;
	@FXML private DatePicker datePick;
	@FXML private Label bookError;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datePick.setValue(LocalDate.now());   
		JavaFXUtil.setDoubleField(numericField);
		ObservableList<String> currs = FXCollections.observableArrayList();
		for (MyCurrency c : Currencies.getInstance().getAll()) {
			currs.add(c.getName());
		}
		bookCombo.setItems(currs); 
		new AutoCompleteComboBoxListener<>(bookCombo);
    }

	@FXML
	public void newBookmaker() {
		String name = bookName.getText();
		
		if (name.equals("") || numericField.getText().equals("") || bookCombo.getValue() == null) {
			bookError.setTextFill(Paint.valueOf("RED"));
			bookError.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		LocalDate date = datePick.getValue();
		BigDecimal walletMoney = new BigDecimal(Double.valueOf(numericField.getText())).setScale(3,BigDecimal.ROUND_HALF_UP);
		String currencyName = bookCombo.getValue();
		MyCurrency currency = Currencies.getInstance().get(currencyName);
		Bookmaker book = new Bookmaker(name);
		book.setWallet(BigDecimal.ZERO, currency);
		book.getWallet().deposit(date, walletMoney);
		System.out.println(String.valueOf(book.getWallet().getMoney().doubleValue()));
		
		if (!book.save()) {
			bookError.setTextFill(Paint.valueOf("RED"));
			bookError.setText("Ошибка базы данных");
			return;	
		}
		Bookmakers.getInstance().add(book);
		
		bookError.setTextFill(Paint.valueOf("GREEN"));
		bookError.setText("Букмекер успешно создан");
		bookName.setText("");
		numericField.setText("");
	}
	
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
}
