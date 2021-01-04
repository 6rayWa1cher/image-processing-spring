package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessConfig;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Controller;

@Controller
public class BrightnessTransformController extends AbstractTransformController<BrightnessConfig> {
	public Slider slider;
	public Label sliderLabel;
	private volatile boolean updating;

	public BrightnessTransformController() {
		super(BrightnessConfig.class);
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized BrightnessConfig stateToInformation() {
		return new BrightnessConfig(slider.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(BrightnessConfig brightnessConfig) {
		updating = true;
		try {
			slider.setValue(brightnessConfig.getDelta());
			sliderLabel.setText(Integer.toString((int) brightnessConfig.getDelta()));
			previewCheckbox.setSelected(brightnessConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
