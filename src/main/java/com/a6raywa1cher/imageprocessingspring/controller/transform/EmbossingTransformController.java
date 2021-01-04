package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.EmbossingConfig;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.scene.control.ChoiceBox;
import org.springframework.stereotype.Controller;

import java.util.Arrays;

@Controller
public class EmbossingTransformController extends AbstractTransformController<EmbossingConfig> {
	public ChoiceBox<String> kernelChooser;
	private volatile boolean updating;

	public EmbossingTransformController() {
		super(EmbossingConfig.class);
	}

	public void initialize() {
		kernelChooser.setItems(new ImmutableObservableList<>(
			Arrays.stream(EmbossingConfig.EmbossingMatrix.values())
				.map(EmbossingConfig.EmbossingMatrix::name)
				.toArray(String[]::new)
		));
		kernelChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized EmbossingConfig stateToInformation() {
		return new EmbossingConfig(EmbossingConfig.EmbossingMatrix.valueOf(kernelChooser.getValue()), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(EmbossingConfig embossingConfig) {
		updating = true;
		try {
			kernelChooser.setValue(embossingConfig.getMatrix().name());
			previewCheckbox.setSelected(embossingConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
