package controllers.edit;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import application.AutoCompleteComboBoxListener;
import entities.Bet;
import entities.Bookmaker;
import entities.Discipline;
import entities.Team;
import entities.Tournament;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Bets;
import storage.Bookmakers;
import storage.Disciplines;
import storage.Teams;
import storage.Tournaments;
import util.JavaFXUtil;

public class EditBetController implements Initializable {

	@FXML private TextField coefField;
	@FXML private TextField betField;
	@FXML private TextField betEventField;
	@FXML private TextField cashOutField;
	@FXML private Label labelError;
	
	@FXML private DatePicker datePick;
	@FXML private ChoiceBox<String> resultBox;
	@FXML private ComboBox<String> discCombo;
	@FXML private ComboBox<String> teamCombo1;
	@FXML private ComboBox<String> teamCombo2;
	@FXML private ComboBox<String> tourCombo;
	@FXML private ComboBox<String> bookCombo;
	@FXML private CheckBox totalChkBox;
	@FXML private Button cancelButton; 
	@FXML private HBox hbCash;
	@FXML private VBox root;	
	@FXML private VBox parentVBox;	
	@FXML private ScrollPane mainScroll;	

	      private boolean isCashOut;
	      private Bet unneditedBet;
	      
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		datePick.setValue(LocalDate.now());
		
		JavaFXUtil.setDoubleField(coefField);
		JavaFXUtil.setDoubleField(betField);
		JavaFXUtil.setDoubleField(cashOutField);
		JavaFXUtil.setCancelButton(cancelButton);
		
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		discCombo.setItems(discps); 
		discCombo.valueProperty().addListener(new ChangeListener<String>() { //
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	ObservableList<String> teams = FXCollections.observableArrayList();    
        		for (Team t : Teams.getInstance().getAll()) {
        			if (t.getDiscipline().getName().equals(newValue)) 
        				teams.add(t.getName());
        		}
        		teamCombo1.setItems(teams); 
        		teamCombo2.setItems(teams); 
        		new AutoCompleteComboBoxListener<>(teamCombo1);
        		new AutoCompleteComboBoxListener<>(teamCombo2);
            	ObservableList<String> tours = FXCollections.observableArrayList(); 
        		for (Tournament t : Tournaments.getInstance().getAll()) {
        			Discipline d = Disciplines.getInstance().get(newValue);
        			if (t.hasDiscipline(d)) 
        				tours.add(t.getName());
        		}
        		tourCombo.setItems(tours); 
        		new AutoCompleteComboBoxListener<>(tourCombo);
            }
        });
		new AutoCompleteComboBoxListener<>(discCombo);
		
		ObservableList<String> resultSet = FXCollections.observableArrayList("Победа", "Поражение", "Ничья", "Кэш-аут");
		resultBox.setItems(resultSet);
		resultBox.setValue("Победа");
		resultBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {			
            	if (newValue.equals("Кэш-аут")) {
            		root.setPrefHeight(714);
            		mainScroll.setPrefHeight(714);
            		parentVBox.setPrefHeight(708);
            		hbCash.setVisible(true);
            		hbCash.setPrefHeight(42);
	           		Stage stage = (Stage) root.getScene().getWindow();
	        		stage.setHeight(stage.getHeight() + 42);
            		isCashOut = true;
            	} else if (isCashOut) {
            		root.setPrefHeight(672);
            		mainScroll.setPrefHeight(672);
            		parentVBox.setPrefHeight(670);
            		hbCash.setVisible(false);
            		hbCash.setPrefHeight(0);
	           		Stage stage = (Stage) root.getScene().getWindow();
	        		stage.setHeight(stage.getHeight() - 42);
            		isCashOut = false;          		
            	}
            }
		});
		
		ObservableList<String> books = FXCollections.observableArrayList();
		for (Bookmaker b : Bookmakers.getInstance().getAll()) {
			books.add(b.getName());
		}
		bookCombo.setItems(books); 
	}
	
	@FXML
	public void applyChanges() {
		int betId = unneditedBet.getId();
		LocalDate date = datePick.getValue();
		String discName = discCombo.getValue();
		String teamName1 = teamCombo1.getValue();
		String teamName2 = teamCombo2.getValue();		
		String tourName = tourCombo.getValue();
		String bookName = bookCombo.getValue();		
		String betEvent = betEventField.getText();	
		String result = resultBox.getValue();
		boolean isTotal = totalChkBox.isSelected();
		
		if ((date == null || discName == null || teamName1 == null || teamName2 == null || tourName == null || bookName == null || betEvent.equals("") || coefField.getText().equals("") || betField.getText().equals("")) || isCashOut && cashOutField.getText().equals("")) {
			labelError.setTextFill(Paint.valueOf("RED"));
			labelError.setText("Необходимо заполнить пустые поля");
			return;			
		}
		
		float coef = Float.valueOf(coefField.getText());
        BigDecimal betValue = BigDecimal.valueOf(Double.valueOf(betField.getText()));		
		
		if (teamName1.equals(teamName2)) {
			labelError.setTextFill(Paint.valueOf("RED"));
			labelError.setText("Две команды не могут быть одинаковыми");
			return;				
		}
		
		Discipline discipline = Disciplines.getInstance().get(discName);
		Team team1 = Teams.getInstance().get(teamName1, discipline);
		Team team2 = Teams.getInstance().get(teamName2, discipline);	
		Tournament tournament = Tournaments.getInstance().get(tourName);
		BigDecimal profit;
		// Calculating profit
		if (result.equals("Победа")) {
			profit = betValue.multiply(BigDecimal.valueOf(coef)).subtract(betValue).setScale(3, RoundingMode.HALF_UP);
		} else if (result.equals("Поражение")) {
			profit = betValue.negate();
		} else if (isCashOut) {
	        BigDecimal cashOut = BigDecimal.valueOf(Double.valueOf(cashOutField.getText()));
			profit = cashOut.subtract(betValue);
		} else
			profit = BigDecimal.ZERO;
		
		Bookmaker bookmaker = Bookmakers.getInstance().get(bookName);	
		if (bookmaker.getWallet().getMoney().compareTo(betValue) < 0) {
			labelError.setTextFill(Paint.valueOf("RED"));
			labelError.setText("На счету недостаточно средств!");
			return;				
		}
		
		Bet newBet = new Bet(betId, date, discipline, team1, team2, tournament, betEvent, coef, betValue, result, isTotal, isCashOut, bookmaker, profit);
		
		if(newBet.update()) {
			Bets.getInstance().remove(unneditedBet, true, true, true);
			Bets.getInstance().add(newBet);		
			team1.setProfit(team1.getProfit().add(profit));
			tournament.setProfit(tournament.getProfit().add(profit));
			bookmaker.getWallet().setMoney(bookmaker.getWallet().getMoney().add(profit));
			team1.update();
			tournament.update();
			bookmaker.update();
			Bets.getInstance().updateTotals(bookmaker);		
			
	    	Stage stage = (Stage) datePick.getScene().getWindow();
			stage.close();
			refreshTable();
		}
	}
	
	public void setBetFields(Bet b) {
		unneditedBet = b;
		isCashOut = b.isCashOut();
		datePick.setValue(b.getDate());
		discCombo.setValue(b.getDiscipline().getName());
		teamCombo1.setValue(b.getTeamFor().getName());
		teamCombo2.setValue(b.getTeamOpp().getName());
		tourCombo.setValue(b.getTournament().getName());
		bookCombo.setValue(b.getBookmaker().getName());
		coefField.setText(String.valueOf(b.getCoefficient()));
		betField.setText(String.valueOf(b.getMoneyBet()));
		betEventField.setText(b.getBetEvent());
		totalChkBox.setSelected(b.isTotal());
		resultBox.setValue(b.getResult());
		if (isCashOut) {
    		root.setPrefHeight(714);
    		mainScroll.setPrefHeight(714);
    		parentVBox.setPrefHeight(708);
    		hbCash.setVisible(true);
    		hbCash.setPrefHeight(42);
       		Stage stage = (Stage) root.getScene().getWindow();
    		stage.setHeight(stage.getHeight() + 42);			
		}
	}
	
	private void refreshTable() {
    	URL path = getClass().getClassLoader().getResource("fxml/Scene.fxml");
		Pane page = null;
		FXMLLoader fxmlLoads = new FXMLLoader(path);
		try
		{
		   page = (Pane) fxmlLoads.load();
		}
		   catch (IOException exception)
		{
		   throw new RuntimeException(exception);
		} 
		//Controller controller = (Controller) fxmlLoads.getController();
		//Controller.getController().fillTable();
	}
	
}
