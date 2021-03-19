package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.HoughCircleConfig;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.stereotype.Controller;

@Controller
public class HoughCircleTransformController extends AbstractTransformController<HoughCircleConfig> {
	public ColorPicker picker;
	public Spinner<Integer> spinner;
	private volatile boolean updating;

	public HoughCircleTransformController() {
		super(HoughCircleConfig.class);
	}

	public void initialize() {
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, 0));
		picker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		spinner.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected HoughCircleConfig stateToInformation() {
		return new HoughCircleConfig(picker.getValue(), spinner.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(HoughCircleConfig config) {
		updating = true;
		try {
			picker.setValue(config.getLineColor());
			spinner.getValueFactory().setValue(config.getRadius());
			previewCheckbox.setSelected(config.isPreview());
		} finally {
			updating = false;
		}
	}
}
