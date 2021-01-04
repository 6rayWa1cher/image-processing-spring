package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.LowHighFrequencyConfig;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.scene.control.ChoiceBox;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller
public class LowHighFrequencyTransformController extends AbstractTransformController<LowHighFrequencyConfig> {
	public ChoiceBox<String> kernelChooser;
	private volatile boolean updating;

	public LowHighFrequencyTransformController() {
		super(LowHighFrequencyConfig.class);
	}

	public void initialize() {
		kernelChooser.setItems(new ImmutableObservableList<>(
			Arrays.stream(LowHighFrequencyConfig.KernelType.values())
				.map(LowHighFrequencyConfig.KernelType::name)
				.toArray(String[]::new)
		));
		kernelChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized LowHighFrequencyConfig stateToInformation() {
		return new LowHighFrequencyConfig(LowHighFrequencyConfig.KernelType.valueOf(kernelChooser.getValue()), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(LowHighFrequencyConfig lowHighFrequencyConfig) {
		updating = true;
		try {
			kernelChooser.setValue(lowHighFrequencyConfig.getType().name());
			previewCheckbox.setSelected(lowHighFrequencyConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
