package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.SolarizationConfig;
import org.springframework.stereotype.Controller;

@Controller
public class SolarizationTransformController extends AbstractTransformController<SolarizationConfig> {
	public SolarizationTransformController() {
		super(SolarizationConfig.class);
	}

	@Override
	protected SolarizationConfig stateToInformation() {
		return new SolarizationConfig(previewCheckbox.isSelected());
	}
}
