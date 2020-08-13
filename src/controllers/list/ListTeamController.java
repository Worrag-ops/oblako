package controllers.list;

import java.io.IOException;
import java.net.URL;

import controllers.edit.EditDisciplineController;
import controllers.edit.EditTeamController;
import entities.Bookmaker;
import entities.Team;
import entities.view.BetView;
import entities.view.BookmakerView;
import entities.view.TeamView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import storage.Bookmakers;
import storage.Disciplines;
import storage.Teams;
import util.JavaFXUtil;

public class ListTeamController {
	
	@FXML private VBox root;
	@FXML private TableView<TeamView> table;
	@FXML private TableColumn<TeamView, String> tableDisc;
	@FXML private TableColumn<TeamView, String> tableName;
	@FXML private TableColumn<TeamView, Double> tableProfit;
	@FXML private TableColumn tableButtons;
	@FXML private Button cancelButton;
	
	private static Stage stage;
	
	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
		fillTable();
	}
	
	private void fillTable() {
		
		tableName.setCellValueFactory(new PropertyValueFactory<TeamView, String>("name"));
		tableDisc.setCellValueFactory(new PropertyValueFactory<TeamView, String>("disciplineName"));
		tableProfit.setCellValueFactory(new PropertyValueFactory<TeamView, Double>("profit"));
		ObservableList<TeamView> views = FXCollections.observableArrayList();
		
		for (Team t : Teams.getInstance().getAll()) {
			TeamView tv = new TeamView(t);
			views.add(tv);
		}
		
		Callback<TableColumn<TeamView, String>, TableCell<TeamView, String>> cellFactoryForView = (param) -> {
			
			TableCell<TeamView, String> cell = new TableCell<TeamView, String>() {
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
					        	URL path = getClass().getClassLoader().getResource("fxml/edit/editTeam.fxml");
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
					        	EditTeamController controller = fxmlLoads.getController();
					        	
					        	TeamView tv = getTableView().getItems().get(getIndex());
					        	Team team = Teams.getInstance().get(tv.getName(), Disciplines.getInstance().get(tv.getDisciplineName()));
					        	controller.setTeamFields(team);
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
					        	TeamView tv = getTableView().getItems().get(getIndex());
					        	Team team = Teams.getInstance().get(tv.getName(), Disciplines.getInstance().get(tv.getDisciplineName()));
					        	JavaFXUtil.deleteWindow(team, root);
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
		
		tableButtons.setCellFactory(cellFactoryForView);
		
		table.setItems(views);
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void setStage(Stage stg) {
		stage = stg;
	}
}
