package controllers.list;

import java.io.IOException;
import java.net.URL;

import controllers.edit.EditBookmakerController;
import entities.Bookmaker;
import entities.view.BookmakerView;
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
import util.JavaFXUtil;

public class ListBookmakerController {
	
	@FXML private VBox root;
	@FXML private TableView<BookmakerView> table;
	@FXML private TableColumn<BookmakerView, String> tableName;
	@FXML private TableColumn<BookmakerView, String> tableCurrency;
	@FXML private TableColumn<BookmakerView, Double> tableBalance;
	@FXML private TableColumn<BookmakerView, Double> tableDeposit;
	@FXML private TableColumn<BookmakerView, Double> tableWithdraw;
	@FXML private TableColumn tableControl;
	@FXML private Button cancelButton;
	
	private static Stage stage;
	
	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
		fillTable();
	}
	
	private void fillTable() {
		
		tableName.setCellValueFactory(new PropertyValueFactory<BookmakerView, String>("name"));
		tableCurrency.setCellValueFactory(new PropertyValueFactory<BookmakerView, String>("currency"));
		tableBalance.setCellValueFactory(new PropertyValueFactory<BookmakerView, Double>("balance"));
		tableDeposit.setCellValueFactory(new PropertyValueFactory<BookmakerView, Double>("deposit"));
		tableWithdraw.setCellValueFactory(new PropertyValueFactory<BookmakerView, Double>("withdraw"));
		ObservableList<BookmakerView> views = FXCollections.observableArrayList();
		
		for (Bookmaker b : Bookmakers.getInstance().getAll()) {
			BookmakerView bv = new BookmakerView(b);
			views.add(bv);
		}
		
		Callback<TableColumn<BookmakerView, String>, TableCell<BookmakerView, String>> cellFactoryControl = (param) -> {
			
			TableCell<BookmakerView, String> cell = new TableCell<BookmakerView, String>() {
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
					        	URL path = getClass().getClassLoader().getResource("fxml/edit/editBookmaker.fxml");
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
					        	EditBookmakerController controller = fxmlLoads.getController();
					        	
					        	BookmakerView bv = getTableView().getItems().get(getIndex());
					        	Bookmaker book = Bookmakers.getInstance().get(bv.getName());
					        	controller.setBookmakerFields(book);
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
					        	BookmakerView bv = getTableView().getItems().get(getIndex());
					        	Bookmaker book = Bookmakers.getInstance().get(bv.getName());
					        	JavaFXUtil.deleteWindow(book, root);
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
		
		table.setItems(views);
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void setStage(Stage stg) {
		stage = stg;
	}
}
