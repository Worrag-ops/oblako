package controllers.create;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import application.AutoCompleteComboBoxListener;
import entities.Discipline;
import entities.Tournament;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import storage.Disciplines;
import storage.Tournaments;

public class TournamentController {
	@FXML private ScrollPane scroll;
	@FXML private ComboBox<String> tournCombo;
	@FXML private TextField tournName;
	@FXML private Label tournError;
	@FXML private VBox create_t;
	@FXML private VBox main_vbox;
	@FXML private ImageView plus;
	
	private final int LABEL_NUMBER = 0;
	private final int COMBOBOX_NUMBER = 2;
	private final int PLUSBTN_NUMBER = 1;
	private final int HBOX_HEIGHT = 42;
    private int currentChild = 3;
    private int countDisciplines = 1;
	
    public void initialize(){ //fill combobox with disciplines
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		tournCombo.setItems(discps); 
		new AutoCompleteComboBoxListener<>(tournCombo);
		plus.setImage(new Image(("/icon/circle_plus_32.png")));
		plus.setOnMouseClicked(new EventHandler<MouseEvent>() {

	        @Override
	        public void handle(MouseEvent t) {
	        	createNewDisciplineHBox();
	        }
	    });
    }
    
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
		
	@FXML
	private void newTournament(ActionEvent event) {
		String name = tournName.getText();
		List <String> sqlDiscNames = new ArrayList<String>();
		
		for (int i = 0; i < countDisciplines; i++) { //fill the list with discipline names
			HBox hb = (HBox) main_vbox.getChildren().get(countDisciplines + 1 - i);
			ComboBox <String> cb = (ComboBox <String>) hb.getChildren().get(COMBOBOX_NUMBER);
			String discName = cb.getValue();
			sqlDiscNames.add(discName);
		}
		
		if (name.equals("") || hasNullFields(sqlDiscNames) == true) { //Check for empty fields
			tournError.setTextFill(Paint.valueOf("RED"));
			tournError.setText("Необходимо заполнить пустые поля");
			sqlDiscNames.clear();
			return;
		}
		
		if (hasRepeatFields(sqlDiscNames)) { //Check for repeating fields
			tournError.setTextFill(Paint.valueOf("RED"));
			tournError.setText("Дисциплины не должны повторяться");
			sqlDiscNames.clear();
			return;
		}
		
		List<Discipline> disciplines = new ArrayList<Discipline>();
		
		for (String s : sqlDiscNames) { //fill the list with disciplines
			disciplines.add(Disciplines.getInstance().get(s));
		}
		
		Tournament t = new Tournament(name, disciplines, BigDecimal.ZERO);
		
		if (!t.save()) {
			tournError.setTextFill(Paint.valueOf("RED"));
			tournError.setText("Ошибка базы данных");
			return;			
		}
		Tournaments.getInstance().add(t);
		tournError.setTextFill(Paint.valueOf("GREEN"));
		tournError.setText("Турнир успешно создан");
		tournName.setText("");
		for(Discipline ddd : t.getDisciplines())
			System.out.println(ddd.getName());
	}
	
	private void createNewDisciplineHBox() {
			ObservableList<String> discps = FXCollections.observableArrayList();
			for (Discipline d : Disciplines.getInstance().getAll()) {
				discps.add(d.getName());
			}
			countDisciplines++;
			setVisiblePrevButton(countDisciplines, false);
			Label l = new Label("Дисциплина " + String.valueOf(countDisciplines));
			l.setStyle("-fx-font: 18 system;");
			l.setOpacity(0.62);
			l.setPrefWidth(360);
			l.setTranslateX(25);
			ComboBox<String> newc = new ComboBox<String>(discps);
			newc.setEditable(true);
			newc.setPrefWidth(308);
			newc.setTranslateX(1);
			new AutoCompleteComboBoxListener<>(newc);
			ImageView plusButton = new ImageView(new Image("/icon/circle_plus_32.png"));
			plusButton.setTranslateX(343);
			plusButton.setFitHeight(27);
			plusButton.setFitWidth(27);
			plusButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

		        @Override
		        public void handle(MouseEvent t) {
		        	createNewDisciplineHBox();
		        }
		    });
			
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
			
			HBox addDisc = new HBox(l, plusButton, newc, removeButton);
			addDisc.setPrefHeight(42);
			addDisc.setPrefWidth(800);
			addDisc.setAlignment(Pos.TOP_CENTER);
			main_vbox.getChildren().add(currentChild,addDisc);
			currentChild++;
			create_t.setPrefHeight(create_t.getPrefHeight() + HBOX_HEIGHT);
			scroll.setPrefHeight(scroll.getPrefHeight() + HBOX_HEIGHT);
			main_vbox.setPrefHeight(main_vbox.getPrefHeight() + HBOX_HEIGHT);
			Stage stage = (Stage) create_t.getScene().getWindow();
			double stageHeight = stage.getHeight(); 
			if (stageHeight < 600) stage.setHeight(stageHeight + HBOX_HEIGHT);
	}
	
	private void removeDisciplineHBox(MouseEvent t) {
		ImageView btn = (ImageView) t.getTarget();
		HBox hb = (HBox) btn.getParent();
		if (hb.getChildren().get(PLUSBTN_NUMBER).isVisible() == true){
			setVisiblePrevButton(countDisciplines, true);
		}
		main_vbox.getChildren().remove(hb);
		countDisciplines--;
		currentChild--;
		renameLabels();
		create_t.setPrefHeight(create_t.getPrefHeight() - HBOX_HEIGHT);
		scroll.setPrefHeight(scroll.getPrefHeight() - HBOX_HEIGHT);
		main_vbox.setPrefHeight(main_vbox.getPrefHeight() - HBOX_HEIGHT);
		Stage stage = (Stage) create_t.getScene().getWindow();
		double stageHeight = stage.getHeight(); 
		if (create_t.getPrefHeight() < 600) stage.setHeight(stageHeight - HBOX_HEIGHT);
	}
    
	private void renameLabels() {
		for (int i = 1; i < countDisciplines; i++) {
			HBox hb = (HBox) main_vbox.getChildren().get(i + 2);
			Label l = (Label) hb.getChildren().get(LABEL_NUMBER);
			l.setText("Дисциплина " + String.valueOf(i + 1));
		} 
	}
	
	private void setVisiblePrevButton(int nmb, boolean visible) {
		HBox hb = (HBox) main_vbox.getChildren().get(nmb);
		hb.getChildren().get(PLUSBTN_NUMBER).setVisible(visible);
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
}
