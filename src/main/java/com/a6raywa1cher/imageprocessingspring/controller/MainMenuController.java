package com.a6raywa1cher.imageprocessingspring.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.stereotype.Controller;

@Controller
public class MainMenuController {
	@FXML
	public Label label;
	@FXML
	public Button button;
	private int counter;

	@FXML
	public void onClick() {
		label.setText(Integer.toString(counter++));
	}
}
