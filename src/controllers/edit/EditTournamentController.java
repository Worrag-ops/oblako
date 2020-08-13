package controllers.edit;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import application.AutoCompleteComboBoxListener;
import controllers.list.ListTournamentController;
import entities.Discipline;
import entities.Tournament;
import h2.H2EntityIdExtractor;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Disciplines;
import util.JavaFXUtil;

public class EditTournamentController {

	@FXML TextField fieldName;
	@FXML Button cancelButton;
	@FXML Label errorLabel;
	@FXML VBox parentVBox;
	@FXML VBox root;	
	@FXML ScrollPane mainScroll;
	
	private Tournament uneditedTournament;
	private List<Discipline> uneditedDiscs;
    private int currentChild = 2;
    private int HBoxCount = 0;
	private final int LABEL_NUMBER = 0;
	private final int COMBOBOX_NUMBER = 2;
	private final int PLUSBTN_NUMBER = 1;
	private final int HBOX_HEIGHT = 42;

	@FXML
	public void initialize() {
		JavaFXUtil.setCancelButton(cancelButton);
	}
	
	@FXML
	private void applyChanges() {
		String name = fieldName.getText();
		int tour_id = uneditedTournament.getId();
		List <String> namesList = new ArrayList<String>();
		
		for (int i = 0; i < HBoxCount; i++) { //fill the list with discipline names
			HBox hb = (HBox) parentVBox.getChildren().get(HBoxCount + 1 - i);
			ComboBox <String> cb = (ComboBox <String>) hb.getChildren().get(COMBOBOX_NUMBER);
			String discName = cb.getValue();
			namesList.add(discName);
		}
		
		if (name.equals("") || hasNullFields(namesList)) {
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		if (hasRepeatFields(namesList)) { //Check for repeating fields
			errorLabel.setTextFill(Paint.valueOf("RED"));
			errorLabel.setText("Дисциплины не должны повторяться");
			return;
		}
		
		List <Discipline> newDiscs = new ArrayList<Discipline>();		
		for (String s : namesList) {
			newDiscs.add(Disciplines.getInstance().get(s));
		}
		
		List <Discipline> removedDiscsList = new ArrayList<Discipline>();
		removedDiscsList.addAll(uneditedDiscs);
		removedDiscsList.removeAll(newDiscs);
		
		List <Discipline> addedDiscsList = new ArrayList<Discipline>();
		addedDiscsList.addAll(newDiscs);
		addedDiscsList.removeAll(uneditedDiscs);	
		
		Tournament newTour = new Tournament(tour_id, name, newDiscs, uneditedTournament.getProfit());
		if(newTour.update()) {
			uneditedTournament.setName(name);
			for (Discipline d : removedDiscsList) {
				if (!uneditedTournament.removeDiscipline(d)) {   //Removing disciplines from tournament and updating DB
					errorLabel.setTextFill(Paint.valueOf("RED"));
					errorLabel.setText("Ошибка при изменении списка дисциплин");
					return;
				}
			}
			for (Discipline d : addedDiscsList) {
				if (!uneditedTournament.addDiscipline(d)) {   //Adding new disciplines on tournament and updating DB
					errorLabel.setTextFill(Paint.valueOf("RED"));
					errorLabel.setText("Ошибка при изменении списка дисциплин");
					return;
				}
			}
		}
		refreshPrevWindow();
    	Stage stage = (Stage) fieldName.getScene().getWindow();
		stage.close();
	}
	
	public void setTournamentFields(Tournament t) {
		uneditedTournament = t;	
		fieldName.setText(uneditedTournament.getName());
		uneditedDiscs = uneditedTournament.getDisciplines();
		
		if (uneditedDiscs.size() != 0) {
			for (int i = 0; i < uneditedDiscs.size(); i++) {
				addDisciplineHBox(true);		
			}
		} else {
			addDisciplineHBox(false);				
		}
	}
	
	private void addDisciplineHBox(boolean init) {
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		HBoxCount++;
		String labelText = HBoxCount > 1 ? "Дисциплина " + String.valueOf(HBoxCount) : "Дисциплина";
		Label l = new Label(labelText);
		l.setStyle("-fx-font: 18 system;");
		l.setOpacity(0.62);
		l.setPrefWidth(360);
		l.setTranslateX(25);
		ComboBox<String> newc = new ComboBox<String>(discps);
		newc.setEditable(true);
		newc.setPrefWidth(308);
		newc.setTranslateX(1);
		if (init) newc.setValue(uneditedDiscs.get(HBoxCount-1).getName());
		new AutoCompleteComboBoxListener<>(newc);

		ImageView plusButton = new ImageView(new Image("/icon/circle_plus_32.png"));
		plusButton.setTranslateX(343);
		plusButton.setFitHeight(27);
		plusButton.setFitWidth(27);
		plusButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

	        @Override
	        public void handle(MouseEvent t) {
	        	addDisciplineHBox(false);
	        }
	    });
		
		HBox addDisc;
		if (HBoxCount > 1) {
			setVisiblePrevButton(HBoxCount, false);
			ImageView removeButton = new ImageView(new Image("/icon/remove_32.png"));
			removeButton.setTranslateX(-345);
			removeButton.setFitHeight(27);
			removeButton.setFitWidth(27);
			removeButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
				
			    @Override
			    public void handle(MouseEvent t) {
			    	removeDisciplineHBox(t);
			      }
			});
			addDisc = new HBox(l, plusButton, newc, removeButton);
		 } else {
			l.setTranslateX(12.5);
			plusButton.setTranslateX(330);
			newc.setTranslateX(-12.5);
			addDisc = new HBox(l, plusButton, newc);			 
		 }
		 addDisc.setPrefHeight(42);
		 addDisc.setPrefWidth(800);
		 addDisc.setAlignment(Pos.TOP_CENTER);
		 parentVBox.getChildren().add(currentChild,addDisc);
		 currentChild++;
		 root.setPrefHeight(root.getPrefHeight() + HBOX_HEIGHT);
		 mainScroll.setPrefHeight(mainScroll.getPrefHeight() + HBOX_HEIGHT);
		 parentVBox.setPrefHeight(parentVBox.getPrefHeight() + HBOX_HEIGHT);
		 Stage stage = (Stage) root.getScene().getWindow();
		 double stageHeight = stage.getHeight(); 
		 if (stageHeight < 600) stage.setHeight(stageHeight + HBOX_HEIGHT);
	}
	
	private void removeDisciplineHBox(MouseEvent t) {
		ImageView btn = (ImageView) t.getTarget();
		HBox hb = (HBox) btn.getParent();
		if (hb.getChildren().get(PLUSBTN_NUMBER).isVisible() == true){
			setVisiblePrevButton(HBoxCount, true);
		}
		parentVBox.getChildren().remove(hb);
		HBoxCount--;
		currentChild--;
		renameLabels();
		root.setPrefHeight(root.getPrefHeight() - HBOX_HEIGHT);
		mainScroll.setPrefHeight(mainScroll.getPrefHeight() - HBOX_HEIGHT);
		parentVBox.setPrefHeight(parentVBox.getPrefHeight() - HBOX_HEIGHT);
		Stage stage = (Stage) root.getScene().getWindow();
		double stageHeight = stage.getHeight(); 
		if (root.getPrefHeight() < 600) stage.setHeight(stageHeight - HBOX_HEIGHT);
	}
	
	private void renameLabels() {
		for (int i = 1; i < HBoxCount; i++) {
			HBox hb = (HBox) parentVBox.getChildren().get(i + 2);
			Label l = (Label) hb.getChildren().get(LABEL_NUMBER);
			l.setText("Дисциплина " + String.valueOf(i + 1));
		} 
	}
	
	private void setVisiblePrevButton(int index, boolean visible) {
		HBox hb = (HBox) parentVBox.getChildren().get(index);
		hb.getChildren().get(1).setVisible(visible);
	}
	
	private boolean hasNullFields(List <String> list) {
		for (String s : list) {
			if (s == null) return true;
		}
		return false;
	}
	
	private boolean hasRepeatFields(List <String> list) {
		Set<String> uniq = new LinkedHashSet<String>(list);
		if (list.size() == uniq.size())
			return false;
		else
			return true;
	}
	
	private void refreshPrevWindow() {
		ListTournamentController.getStage().close();											
		JavaFXUtil.openWindow("fxml/list/listTournament.fxml", false);	
	}
	
}
