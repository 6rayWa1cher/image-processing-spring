package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.MiddleConfig;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.stereotype.Controller;

@Controller
public class MiddleTransformController extends AbstractTransformController<MiddleConfig> {
	public Spinner<Integer> chooser;
	private volatile boolean updating;

	public MiddleTransformController() {
		super(MiddleConfig.class);
	}

	public void initialize() {
		chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, Integer.MAX_VALUE, 3, 2));
		chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized MiddleConfig stateToInformation() {
		return new MiddleConfig(chooser.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(MiddleConfig middleConfig) {
		updating = true;
		try {
			chooser.getValueFactory().setValue(middleConfig.getWindowSize());
			previewCheckbox.setSelected(middleConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
