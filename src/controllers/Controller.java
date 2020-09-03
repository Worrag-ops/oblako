package controllers;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import application.AutoCompleteComboBoxListener;
import controllers.edit.EditBetController;
import entities.Bet;
import entities.Bookmaker;
import entities.Discipline;
import entities.Team;
import entities.Tournament;
import entities.view.BetView;
import entities.view.BetView;
import h2.ConnectH2;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import storage.Bets;
import storage.Bookmakers;
import storage.Disciplines;
import storage.Teams;
import storage.Tournaments;
import util.JavaFXUtil;

public class Controller {
	@FXML private ComboBox<String> filterDisc;
	@FXML private ComboBox<String> filterBook;
	@FXML private ComboBox<String> filterTour;
	@FXML private ComboBox<String> filterTeam1;
	@FXML private ComboBox<String> filterTeam2;
	@FXML private ChoiceBox<String> filterResult;
	@FXML private TextField filterEvent;
	@FXML private DatePicker filterDateStart;
	@FXML private DatePicker filterDateEnd;
	@FXML private VBox root;
	@FXML private Button filterButton; 
	
	@FXML private TableView<BetView> mainTable;
	@FXML private TableColumn<BetView, Integer> tableId;
	@FXML private TableColumn<BetView, LocalDate> tableDate;
	@FXML private TableColumn<BetView, String> tableDisc;
	@FXML private TableColumn<BetView, String> tableTeam1;
	@FXML private TableColumn<BetView, String> tableTeam2;
	@FXML private TableColumn<BetView, String> tableEvent;
	@FXML private TableColumn<BetView, Float> tableCoef;
	@FXML private TableColumn<BetView, Double> tableBet;
	@FXML private TableColumn<BetView, String> tableResult;
	@FXML private TableColumn<BetView, Double> tableProfit;
	@FXML private TableColumn<BetView, String> tableIsTotal;
	@FXML private TableColumn<BetView, String> tableCashOut;
	@FXML private TableColumn<BetView, Double> tableTotal;
	@FXML private TableColumn<BetView, String> tableTour;
	@FXML private TableColumn<BetView, String> tableBook;
	@FXML private TableColumn tableControl;
	
	//private static Controller controller;
	
	@FXML
	public void initialize(){ 	
		//Controller.Controller.setController(this);
		filterDateStart.setValue(LocalDate.now().minusDays(7));
		filterDateEnd.setValue(LocalDate.now());
		ObservableList<String> books = FXCollections.observableArrayList();
		for (Bookmaker b : Bookmakers.getInstance().getAll()) { //—ƒ≈À¿“‹ ”Õ» ¿À‹ÕŒ—“‹ «Õ¿◊≈Õ»…!
			books.add(b.getName());
		}		
		ObservableList<String> teams = FXCollections.observableArrayList();
		for (Team t : Teams.getInstance().getAll()) {
			teams.add(t.getName());
		}
		ObservableList<String> tours = FXCollections.observableArrayList();
		for (Tournament t : Tournaments.getInstance().getAll()) {
			tours.add(t.getName());
		}
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		filterDisc.setItems(discps); 
		filterBook.setItems(books); 
		filterTeam1.setItems(teams); 
		filterTeam2.setItems(teams);
		filterTour.setItems(tours); 
		filterDisc.valueProperty().addListener(new ChangeListener<String>() { //
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            	if (newValue.isEmpty()) {
            		filterTeam1.setItems(teams); 
            		filterTeam2.setItems(teams);
            		filterTour.setItems(tours); 
            	} else {       	
	            	ObservableList<String> teamsF = FXCollections.observableArrayList();    
	        		for (Team t : Teams.getInstance().getAll()) {
	        			if (t.getDiscipline().getName().equals(newValue)) 
	        				teamsF.add(t.getName());
	        		}
	        		filterTeam1.setItems(teamsF); 
	        		filterTeam2.setItems(teamsF); 
	            	ObservableList<String> toursF = FXCollections.observableArrayList(); 
	        		for (Tournament t : Tournaments.getInstance().getAll()) {
	        			Discipline d = Disciplines.getInstance().get(newValue);
	        			if (t.hasDiscipline(d)) 
	        				toursF.add(t.getName());
	        		}
	        		filterTour.setItems(toursF); 
            	}
        		new AutoCompleteComboBoxListener<>(filterTeam1);
        		new AutoCompleteComboBoxListener<>(filterTeam2);
        		new AutoCompleteComboBoxListener<>(filterTour);
            }
        });
		
		ObservableList<String> results = FXCollections.observableArrayList("À˛·ÓÈ", "œÓ·Â‰‡", "œÓ‡ÊÂÌËÂ", "ÕË˜¸ˇ", " ˝¯-‡ÛÚ");
		filterResult.setItems(results);
		filterResult.setValue("À˛·ÓÈ");
		
		new AutoCompleteComboBoxListener<>(filterDisc);
		new AutoCompleteComboBoxListener<>(filterBook);
		new AutoCompleteComboBoxListener<>(filterTeam1);
		new AutoCompleteComboBoxListener<>(filterTeam2);
		new AutoCompleteComboBoxListener<>(filterTour);
	}
	
	@FXML
	private void fillTable() {
		tableId.setCellValueFactory(new PropertyValueFactory<BetView, Integer>("id"));
		tableDate.setCellValueFactory(new PropertyValueFactory<BetView, LocalDate>("date"));
		tableDisc.setCellValueFactory(new PropertyValueFactory<BetView, String>("discipline"));
		tableTeam1.setCellValueFactory(new PropertyValueFactory<BetView, String>("teamFor"));
		tableTeam2.setCellValueFactory(new PropertyValueFactory<BetView, String>("teamOpp"));
		tableEvent.setCellValueFactory(new PropertyValueFactory<BetView, String>("betEvent"));		
		tableCoef.setCellValueFactory(new PropertyValueFactory<BetView, Float>("coefficient"));	
		tableBet.setCellValueFactory(new PropertyValueFactory<BetView, Double>("moneyBet"));	
		tableResult.setCellValueFactory(new PropertyValueFactory<BetView, String>("result"));	
		tableProfit.setCellValueFactory(new PropertyValueFactory<BetView, Double>("profit"));			
		tableTotal.setCellValueFactory(new PropertyValueFactory<BetView, Double>("total"));		
		tableIsTotal.setCellValueFactory(new PropertyValueFactory<BetView, String>("isTotal"));		
		tableCashOut.setCellValueFactory(new PropertyValueFactory<BetView, String>("isCashOut"));		
		tableTour.setCellValueFactory(new PropertyValueFactory<BetView, String>("tourn"));	
		tableBook.setCellValueFactory(new PropertyValueFactory<BetView, String>("book"));
		
		Callback<TableColumn<BetView, String>, TableCell<BetView, String>> cellFactoryControl = (param) -> {
			
			TableCell<BetView, String> cell = new TableCell<BetView, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
						setText(null);
					} else {
						HBox buttonsContainer = new HBox(6);
						ImageView editButton = new ImageView(new Image("/icon/edit_32.png"));
						editButton.setFitHeight(30);
						editButton.setFitWidth(30);
						editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
					        @Override
					        public void handle(MouseEvent t) {			     
					        	URL path = getClass().getClassLoader().getResource("fxml/edit/editBet.fxml");
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
					    		Scene scene = new Scene(page);
					    		scene.getStylesheets().add(getClass().getClassLoader().getResource("css/application.css").toExternalForm());
					    		Stage stage = new Stage(); 
					    		stage.setScene(scene);
					    		stage.initStyle(StageStyle.UNDECORATED);
					        	EditBetController controller = fxmlLoads.getController();
					        	
					        	BetView bv = getTableView().getItems().get(getIndex());
					        	Bet bet = Bets.getInstance().get(bv.getId());
					        	controller.setBetFields(bet);										 //
				    			stage.initModality(Modality.APPLICATION_MODAL); 
				    			stage.showAndWait();
					        }
					    });
						
						ImageView deleteButton = new ImageView(new Image("/icon/remove_ent_32.png"));
						deleteButton.setFitHeight(30);
						deleteButton.setFitWidth(30);	
						deleteButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
					        @Override
					        public void handle(MouseEvent t) {
					        	BetView bv = getTableView().getItems().get(getIndex());
					        	Bet bet = Bets.getInstance().get(bv.getId());
					        	JavaFXUtil.deleteWindow(bet, root);
					        }
					    });
						buttonsContainer.getChildren().add(editButton);
						buttonsContainer.getChildren().add(deleteButton);					
						setGraphic(buttonsContainer);
					
					}
				}
			};
			return cell;
		};
		
		tableControl.setCellFactory(cellFactoryControl);
		
		ObservableList<BetView> views = FXCollections.observableArrayList();
		LocalDate dateStart = filterDateStart.getValue();
		LocalDate dateEnd = filterDateEnd.getValue();	
		String discName = filterDisc.getValue();
		String team1 = filterTeam1.getValue();
		String team2 = filterTeam2.getValue();
 		String tournament = filterTour.getValue();
 		String result = filterResult.getValue();
 		String event = filterEvent.getText();
 		String book = filterBook.getValue();
		
		for (Bet b : Bets.getInstance().getAll()) {
			LocalDate date = b.getDate();
			if(!((date.isAfter(dateStart) && date.isBefore(dateEnd)) || date.isEqual(dateStart) || date.isEqual(dateEnd)))
				continue;
			if(discName != null && !(b.getDiscipline().getName().equals(discName))) 
				continue;
			if(team1 != null && !(b.getTeamFor().getName().equals(team1)))
				continue;
			if(team2 != null && !(b.getTeamOpp().getName().equals(team2)))
				continue;	
			if(tournament != null && !(b.getTournament().getName().equals(tournament)))
				continue;
			if(!result.equals("À˛·ÓÈ") && !(b.getResult().equals(result)))
				continue;
			if(!event.isEmpty() && !(b.getBetEvent().equals(event)))
				continue;
			if(book != null && !(b.getBookmaker().getName().equals(book)))
				continue;
			BetView bv = new BetView(b);
			views.add(bv);
		}
		
		mainTable.setItems(views);
	}
	
	@FXML
	private void openCreateTeamWindow(ActionEvent event) {	
		JavaFXUtil.openWindow("fxml/create/createTeam.fxml", false);
	}
	
	@FXML
	private void openCreateCurrencyWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/create/createCurrency.fxml", false);
	}
	
	@FXML
	private void openCreateDisciplineWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/create/createDiscipline.fxml", false);
	}
	
	@FXML
	private void openCreateTournamentWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/create/createTournament.fxml", false);
	}
	
	@FXML
	private void openCreateBookmakerWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/create/createBookmaker.fxml", false);
	}
	
	@FXML
	private void openCreateBetWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/create/createBet.fxml", false);
	}
	
	@FXML
	private void openListDisciplineWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/list/listDiscipline.fxml", false);
	}
	
	@FXML
	private void openListTeamWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/list/listTeam.fxml", false);
	}
	
	@FXML
	private void openListBookmakerWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/list/listBookmaker.fxml", false);
	}
	
	@FXML
	private void openListTournamentWindow(ActionEvent event) {
		JavaFXUtil.openWindow("fxml/list/listTournament.fxml", false);
	}
	
	@FXML
	private void testPrintTableDisciplines() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM disciplines");
			System.out.println(">>>>> DISCIPLINES <<<<<");
			while (result.next()) {
				System.out.println(result.getInt(1) + ": " + result.getString(2) + " " + result.getString(3));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void testPrintTableTournamentDiscipline() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM tournament_discipline");
			System.out.println(">>>>> tournament_discipline <<<<<");
			int i = 0;
			while (result.next()) {
				i++;
				System.out.println(String.valueOf(i) + ": " + result.getInt(1) + " " + result.getInt(2));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void testPrintTableTeams() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM teams");
			System.out.println(">>>>> TEAMS <<<<<");
			while (result.next()) {
				System.out.println(result.getString(1) + ": " + result.getString(2) + " " + result.getString(3) + " " + result.getBigDecimal(4));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void testPrintTableTournaments() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM tournaments");
			System.out.println(">>>>> TOURNAMENTS <<<<<");
			while (result.next()) {
				System.out.println(result.getInt(1) + ": " + result.getString(2) + result.getBigDecimal(3));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	private void testPrintTableCurrency() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM currency");
			int i = 0;
			System.out.println(">>>>> CURRENCY <<<<<");
			while (result.next()) {
				i++;
				System.out.println(String.valueOf(i) + ": " + result.getString(2) + " " + result.getBoolean(3) + " " + result.getDouble(4));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	@FXML
	private void testPrintTableBookmakers() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM bookmakers");
			int i = 0;
			System.out.println(">>>>> BOOKMAKERS <<<<<");
			while (result.next()) {
				i++;
				System.out.println(String.valueOf(i) + ": " + result.getString(2) + " " + result.getBigDecimal(3) + " " + result.getInt(4));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	@FXML
	private void testPrintTableBets() {
		try {
			Statement st = ConnectH2.getConnection().createStatement();
			ResultSet result = st.executeQuery("SELECT * FROM bets");
			int i = 0;
			System.out.println(">>>>> BETS <<<<<");
			while (result.next()) {
				i++;
				System.out.println(String.valueOf(i) + ": " + result.getObject(2) + " " + result.getString(3) + " " + result.getString(4) + " " + result.getString(5) + " " + result.getString(6) + " " + result.getString(7) + " " + result.getFloat(8) + " " + result.getDouble(9) + " " + result.getString(10) + " " + result.getBigDecimal(11)  + " " + result.getInt(12) + " " + result.getBoolean(13));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
		
	@FXML
	private void testRecreateTables() {
		try {
			ConnectH2.dropTables();
			ConnectH2.createTables();			
			} catch (SQLException e) {
				e.printStackTrace();
		}	
	}
	
	public TableView<BetView> getMainTable() {
		return mainTable;
	}

	/*public static Controller getController() {
		return controller;
	}

	public static void setController(Controller controller) {
		Controller.controller = controller;
	}*/
}
