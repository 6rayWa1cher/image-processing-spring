package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.PseudocolorConfig;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.stereotype.Controller;

@Controller
public class PseudocolorTransformController extends AbstractTransformController<PseudocolorConfig> {
	public Spinner<Integer> q1chooser;
	public ColorPicker picker1;
	public Spinner<Integer> q2chooser;
	public ColorPicker picker2;
	public Spinner<Integer> q3chooser;
	public ColorPicker picker3;
	public ColorPicker picker4;
	private volatile boolean updating;

	public PseudocolorTransformController() {
		super(PseudocolorConfig.class);
	}

	public void initialize() {
		q1chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q2chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q3chooser.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
		q1chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		q2chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
		q3chooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	protected synchronized PseudocolorConfig stateToInformation() {
		return new PseudocolorConfig(picker1.getValue(), picker2.getValue(), picker3.getValue(), picker4.getValue(),
			q1chooser.getValue(), q2chooser.getValue(), q3chooser.getValue(), previewCheckbox.isSelected());
	}

	protected synchronized void informationToState(PseudocolorConfig pseudocolorConfig) {
		updating = true;
		try {
			q1chooser.getValueFactory().setValue(pseudocolorConfig.getQ1());
			q2chooser.getValueFactory().setValue(pseudocolorConfig.getQ2());
			q3chooser.getValueFactory().setValue(pseudocolorConfig.getQ3());
			picker1.setValue(pseudocolorConfig.getColor1());
			picker2.setValue(pseudocolorConfig.getColor2());
			picker3.setValue(pseudocolorConfig.getColor3());
			picker4.setValue(pseudocolorConfig.getColor4());
			previewCheckbox.setSelected(pseudocolorConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
