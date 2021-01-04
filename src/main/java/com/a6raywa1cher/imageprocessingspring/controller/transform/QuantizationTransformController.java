package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.QuantizationConfig;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Controller;

@Controller
public class QuantizationTransformController extends AbstractTransformController<QuantizationConfig> {
	public Slider slider;
	public Label sliderLabel;
	private volatile boolean updating;

	public QuantizationTransformController() {
		super(QuantizationConfig.class);
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	protected synchronized QuantizationConfig stateToInformation() {
		return new QuantizationConfig((int) slider.getValue(), previewCheckbox.isSelected());
	}

	protected synchronized void informationToState(QuantizationConfig quantizationConfig) {
		updating = true;
		try {
			slider.setValue(quantizationConfig.getSegments());
			sliderLabel.setText(Integer.toString(quantizationConfig.getSegments()));
			previewCheckbox.setSelected(quantizationConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
