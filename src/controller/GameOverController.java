package controller;

import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class GameOverController implements Initializable {

    @FXML
    private Label scoreLabel;

	public void setScoreLabel(String text) {
		this.scoreLabel.setText(text);
	}

	@FXML
    private Button restartButton;

    @FXML
    private Button quitButton;

    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		restartButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Main.main.restartApplication();
				((Node)(event.getSource())).getScene().getWindow().hide();
			}

		});

		quitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}

		});
	}

}
