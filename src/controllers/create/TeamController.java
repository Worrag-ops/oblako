package controllers.create;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import application.AutoCompleteComboBoxListener;
import entities.Discipline;
import entities.Team;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import storage.Disciplines;
import storage.Teams;
import util.FileUtil;
import util.StringUtil;

public class TeamController implements Initializable {
	@FXML private ScrollPane scroll;
	@FXML private ComboBox<String> teamCombo;
	@FXML private TextField teamName;
	@FXML private TextField logoPathField;
	@FXML private Label teamError;
	@FXML private ImageView logoImage;
	@FXML private VBox create_t;
	@FXML private VBox main_vbox;
	@FXML private Button logoPickButton;
	
	      private File logo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> discps = FXCollections.observableArrayList();
		for (Discipline d : Disciplines.getInstance().getAll()) {
			discps.add(d.getName());
		}
		teamCombo.setItems(discps); 
		new AutoCompleteComboBoxListener<>(teamCombo);
    }
	
	@FXML
	private void cancelButton(ActionEvent event) {
		Button btn = (Button) event.getSource();
		Stage stage = (Stage) btn.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void selectLogo(ActionEvent event) throws MalformedURLException {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select logo");
		fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));

		logo = fc.showOpenDialog(logoPickButton.getScene().getWindow());
        if (logo != null) {
            System.out.println("Процесс открытия файла");
            logoPathField.setText(logo.getAbsolutePath());
            logoImage.setImage(new Image(logo.toURI().toURL().toString()));
        }
		
	}
	
	@FXML
	private void newTeam(ActionEvent event) {
		String name = teamName.getText();
		String teamDisc = teamCombo.getValue();
		
		if (name.equals("") || teamDisc == null) {
			teamError.setTextFill(Paint.valueOf("RED"));
			teamError.setText("Необходимо заполнить пустые поля");
			return;
		}
		
		Team t;
		if (logo != null) {
			String fileSeparator = System.getProperty("file.separator");
			String discConstructedName = StringUtil.constructDirectoryName(teamDisc);
			File directory = new File(this.getClass().getResource("/").getPath() + "logo" + "/" + discConstructedName);
			if (!directory.exists()) {
				directory.mkdir();
			}
			String logoPath =  "logo" + "/" + discConstructedName + "/" + name + ".png";
			System.out.println(logoPath);
			File newLogo = new File(this.getClass().getResource("/").getPath() + logoPath);
			try {
				newLogo.createNewFile();
				FileUtil.copyFile(logo, newLogo);
			} catch (IOException e) {
				setErrorLabelMessage(Paint.valueOf("RED"), "Ошибка сохранения лого");
				e.printStackTrace();
				return;
			}
			
			String logoURL;
			try {
				logoURL = newLogo.toURI().toURL().toString();
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return;
			}
			t = new Team(name, Disciplines.getInstance().get(teamDisc), logoURL);			
		} else {
			t = new Team(name, Disciplines.getInstance().get(teamDisc), "");			
		}
		
		if (!t.save()) {
			setErrorLabelMessage(Paint.valueOf("RED"), "Ошибка базы данных");
			return;			
		}
		Teams.getInstance().add(t);
		teamError.setTextFill(Paint.valueOf("GREEN"));
		teamError.setText("Команда успешно сохранена");
		teamName.setText("");
	}
	
	private void setErrorLabelMessage(Paint color, String message) {
		teamError.setTextFill(color);
		teamError.setText(message);		
	}
}
