package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.ContrastConfig;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

@Controller
public class ContrastTransformController extends AbstractTransformController<ContrastConfig> {
	public Slider leftSlider;
	public Label leftSliderLabel;
	public Slider rightSlider;
	public Label rightSliderLabel;
	public ChoiceBox<String> directionChoiceBox;
	private volatile boolean updating;

	public ContrastTransformController() {
		super(ContrastConfig.class);
	}

	public void initialize() {
		leftSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		rightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized ContrastConfig stateToInformation() {
		return new ContrastConfig(leftSlider.getValue(), rightSlider.getValue(),
			directionChoiceBox.getValue().equals("Decrease"), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(ContrastConfig contrastConfig) {
		updating = true;
		try {
			leftSlider.setValue(contrastConfig.getLeftBorder());
			rightSlider.setValue(contrastConfig.getRightBorder());
			leftSliderLabel.setText(Integer.toString((int) contrastConfig.getLeftBorder()));
			rightSliderLabel.setText(Integer.toString((int) contrastConfig.getRightBorder()));
			previewCheckbox.setSelected(contrastConfig.isPreview());
			directionChoiceBox.setValue(contrastConfig.isDecrease() ? "Decrease" : "Increase");
		} finally {
			updating = false;
		}
	}
}
