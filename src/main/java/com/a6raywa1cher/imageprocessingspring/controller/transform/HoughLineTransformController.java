package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.HoughLineConfig;
import javafx.scene.control.ColorPicker;
import org.springframework.stereotype.Controller;

@Controller
public class HoughLineTransformController extends AbstractTransformController<HoughLineConfig> {
	public ColorPicker picker;
	private volatile boolean updating;

	public HoughLineTransformController() {
		super(HoughLineConfig.class);
	}

	public void initialize() {
		picker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected HoughLineConfig stateToInformation() {
		return new HoughLineConfig(picker.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(HoughLineConfig config) {
		updating = true;
		try {
			picker.setValue(config.getLineColor());
			previewCheckbox.setSelected(config.isPreview());
		} finally {
			updating = false;
		}
	}
}
