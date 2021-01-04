package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.GammaConfig;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.springframework.stereotype.Controller;

@Controller
public class GammaTransformController extends AbstractTransformController<GammaConfig> {
	public Slider slider;
	public Label sliderLabel;
	private volatile boolean updating;

	public GammaTransformController() {
		super(GammaConfig.class);
	}

	public void initialize() {
		slider.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected synchronized GammaConfig stateToInformation() {
		return new GammaConfig(rawGammaToDouble((int) slider.getValue()), previewCheckbox.isSelected());
	}

	private double rawGammaToDouble(int gamma) {
		return gamma >= 0 ? gamma + 1 : 1d / (-gamma + 1);
	}

	private int doubleGammaToRaw(double gamma) {
		return gamma >= 1 ? (int) (gamma - 1) : (int) (1d / (-gamma) + 1d);
	}

	@Override
	protected synchronized void informationToState(GammaConfig gammaConfig) {
		updating = true;
		try {
			int readyGamma = doubleGammaToRaw(gammaConfig.getGamma());
			slider.setValue(readyGamma);
			sliderLabel.setText(readyGamma >= 0 ? Integer.toString(readyGamma + 1) : "1/" + (-readyGamma + 1));
			previewCheckbox.setSelected(gammaConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
