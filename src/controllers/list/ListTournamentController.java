package controllers.list;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import controllers.edit.EditTournamentController;
import entities.Tournament;
import entities.view.TournamentView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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
import storage.Tournaments;
import util.JavaFXUtil;

public class ListTournamentController implements Initializable {
	
	@FXML private VBox root;
	@FXML private TableView<TournamentView> table;
	@FXML private TableColumn<TournamentView, String> tableDisciplines;
	@FXML private TableColumn<TournamentView, String> tableName;
	@FXML private TableColumn<TournamentView, Double> tableProfit;
	@FXML private TableColumn tableControl;
	@FXML private Button cancelButton;
	
	private static Stage stage;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		JavaFXUtil.setCancelButton(cancelButton);
		fillTable();
	}
	
	private void fillTable() {
		
		tableName.setCellValueFactory(new PropertyValueFactory<TournamentView, String>("name"));
		tableDisciplines.setCellValueFactory(new PropertyValueFactory<TournamentView, String>("disciplines"));
		tableProfit.setCellValueFactory(new PropertyValueFactory<TournamentView, Double>("profit"));
		ObservableList<TournamentView> views = FXCollections.observableArrayList();
		
		for (Tournament t : Tournaments.getInstance().getAll()) {
			TournamentView tv = new TournamentView(t);
			views.add(tv);
		}
		
		Callback<TableColumn<TournamentView, String>, TableCell<TournamentView, String>> cellFactoryForView = (param) -> {
			
			TableCell<TournamentView, String> cell = new TableCell<TournamentView, String>() {
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
					        	URL path = getClass().getClassLoader().getResource("fxml/edit/editTournament.fxml");
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
					    		scene.getStylesheets().add(JavaFXUtil.class.getClassLoader().getResource("css/application.css").toExternalForm());
					    		Stage stage = new Stage(); 
					    		stage.setScene(scene);
					    		stage.initStyle(StageStyle.UNDECORATED);
					        	EditTournamentController controller = fxmlLoads.getController(); 
					        	
					        	TournamentView tv = getTableView().getItems().get(getIndex());
					        	Tournament tour = Tournaments.getInstance().get(tv.getName());
					        	controller.setTournamentFields(tour);
					    		Stage stg = (Stage) cancelButton.getScene().getWindow(); //setting stage for refresh from editcontroller
					    		setStage(stg);											 //
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
					        	TournamentView tv = getTableView().getItems().get(getIndex());
					        	Tournament tour = Tournaments.getInstance().get(tv.getName());
					        	JavaFXUtil.deleteWindow(tour, root);
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
		tableControl.setCellFactory(cellFactoryForView);		
		table.setItems(views);
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void setStage(Stage stg) {
		stage = stg;
	}
}
