package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.NegativeConfig;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Controller;

@Controller
public class NegativeTransformController extends AbstractTransformController<NegativeConfig> {
	public Slider slider;
	public Label sliderLabel;
	private volatile boolean updating;

	public NegativeTransformController() {
		super(NegativeConfig.class);
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	protected synchronized NegativeConfig stateToInformation() {
		return new NegativeConfig(slider.getValue(), previewCheckbox.isSelected());
	}

	protected synchronized void informationToState(NegativeConfig negativeConfig) {
		updating = true;
		try {
			slider.setValue(negativeConfig.getThreshold());
			sliderLabel.setText(Integer.toString((int) negativeConfig.getThreshold()));
			previewCheckbox.setSelected(negativeConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
