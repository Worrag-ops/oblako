package controllers.edit;

import java.net.URL;
import java.util.ResourceBundle;

import controllers.list.ListBookmakerController;
import entities.Bookmaker;
import entities.MyCurrency;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Bookmakers;
import storage.Currencies;
import util.JavaFXUtil;

public class EditBookmakerController implements Initializable {

	@FXML TextField bookNameField;
	@FXML TextField bookBalanceField;
	@FXML ComboBox<String> currencyCombo;
	@FXML Button cancelButton;
	@FXML Label errorLabel;
	
	private int book_id; 
	private Bookmaker uneditedBookmaker;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		JavaFXUtil.setCancelButton(cancelButton);
		ObservableList<String> currs = FXCollections.observableArrayList();
		for (MyCurrency c : Currencies.getInstance().getAll()) {
			currs.add(c.getName());
		}
		currencyCombo.setItems(currs); 
	}
	
	@FXML
	private void applyChanges() {
		String name = bookNameField.getText();
		book_id = uneditedBookmaker.getId();
		MyCurrency curr = Currencies.getInstance().get(currencyCombo.getValue());
		
		if (name.equals("")) {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		Bookmaker newBook = new Bookmaker(book_id, name);
		newBook.setWallet(uneditedBookmaker.getWallet());
		newBook.getWallet().setCurrency(curr);
		
		if (!uneditedBookmaker.equals(newBook)) {
			for (Bookmaker b : Bookmakers.getInstance().getAll()) {
				if (b.equals(newBook)) {
					errorLabel.setTextFill(Paint.valueOf("RED"));
					errorLabel.setText("Букмекер с такими параметрами уже существует!");
					return;			
				}
			}
		}
		
		if(newBook.update()){
			uneditedBookmaker.setName(name);	
			uneditedBookmaker.getWallet().setCurrency(curr);
			refreshPrevWindow();
	    	Stage stage = (Stage) bookNameField.getScene().getWindow();
			stage.close();
		} else {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Ошибка при изменении!");
			return;			
		}
	}
	
	public void setBookmakerFields(Bookmaker book) {
		String bookName = book.getName();
		bookNameField.setText(bookName);	
		bookBalanceField.setText(String.valueOf(book.getWallet().getMoney()));	
		currencyCombo.setValue(book.getWallet().getCurrency().getName());
		uneditedBookmaker = book;
	}
	
	private void refreshPrevWindow() {
		ListBookmakerController.getStage().close();											
		JavaFXUtil.openWindow("fxml/list/listBookmaker.fxml", false);	
	}
	
}
