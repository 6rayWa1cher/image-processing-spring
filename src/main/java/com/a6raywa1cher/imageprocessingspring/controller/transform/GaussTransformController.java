package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.GaussConfig;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.stereotype.Controller;

@Controller
public class GaussTransformController extends AbstractTransformController<GaussConfig> {
	public Spinner<Integer> chooser;
	private volatile boolean updating;

	public GaussTransformController() {
		super(GaussConfig.class);
	}

	public void initialize() {
		chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 67, 3, 2));
		chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized GaussConfig stateToInformation() {
		return new GaussConfig(chooser.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(GaussConfig gaussConfig) {
		updating = true;
		try {
			chooser.getValueFactory().setValue(gaussConfig.getGaussDegree());
			previewCheckbox.setSelected(gaussConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
