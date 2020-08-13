package controllers.list;

import java.io.IOException;
import java.net.URL;

import controllers.edit.EditDisciplineController;
import entities.Discipline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import storage.Disciplines;
import util.JavaFXUtil;

public class ListDisciplineController {
	@FXML private GridPane table;
	@FXML private AnchorPane anchor;
	@FXML private Button cancelButton;	
	@FXML private VBox root;
	
	private static Stage stage;
	
	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
		fillTable();
	}
	
	
	private void fillTable() {
		int index = 1;
		for (Discipline d : Disciplines.getInstance().getAll()) {
			Label name = new Label(d.getName());
			Label type = new Label(d.getDiscType());
			ImageView editButton = new ImageView(new Image("/icon/edit_32.png"));
			editButton.setFitHeight(28);
			editButton.setFitWidth(28);
			editButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent t) {
		        	URL path = getClass().getClassLoader().getResource("fxml/edit/editDiscipline.fxml");
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
		        	EditDisciplineController controller = fxmlLoads.getController();
		        	controller.setDisciplineFields(d);
		    		Stage stg = (Stage) cancelButton.getScene().getWindow(); //setting stage for refresh from editcontroller
		    		setStage(stg);											 //
	    			stage.initModality(Modality.APPLICATION_MODAL); 
	    			stage.showAndWait();
		        }
		    });
			ImageView removeButton = new ImageView(new Image("/icon/remove_ent_32.png"));
			removeButton.setFitHeight(28);
			removeButton.setFitWidth(28);
			removeButton.setTranslateX(32);
			removeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent t) {
		        	 JavaFXUtil.deleteWindow(d, root);
		        }
		    });
			
			table.getRowConstraints().add(new RowConstraints(30));
			table.add(name, 0, index);
			table.add(type, 1, index);
			table.add(editButton, 2, index);
			table.add(removeButton, 2, index);			
			
			anchor.setPrefHeight(anchor.getPrefHeight() + 30);
			index++;
		}		
	}
	
	public VBox getRoot() {
		return root;
	}
	
	public void refreshTable() {
		Node title = table.getChildren().get(0);
		Node type = table.getChildren().get(1);	
		table.getChildren().clear();
		ImageView editButton = new ImageView(new Image("/icon/edit_32.png"));
		editButton.setFitHeight(28);
		editButton.setFitWidth(28);
		table.add(editButton, 1, 1);
		//table.getChildren().add(title);
		//table.getChildren().add(type);	
		//table.setGridLinesVisible(true);
		System.out.println("RQW");
		fillTable();
		root.requestLayout();
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	public static void setStage(Stage stg) {
		stage = stg;
	}
}
