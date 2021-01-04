package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.BinaryConfig;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Controller;

@Controller
public class BinaryTransformController extends AbstractTransformController<BinaryConfig> {
	public Slider slider;
	public Label sliderLabel;
	private volatile boolean updating;

	public BinaryTransformController() {
		super(BinaryConfig.class);
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized BinaryConfig stateToInformation() {
		return new BinaryConfig(slider.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(BinaryConfig binaryConfig) {
		updating = true;
		try {
			slider.setValue(binaryConfig.getThreshold());
			sliderLabel.setText(Integer.toString((int) binaryConfig.getThreshold()));
			previewCheckbox.setSelected(binaryConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
