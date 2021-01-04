package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.SobelConfig;
import org.springframework.stereotype.Controller;

@Controller
public class SobelTransformController extends AbstractTransformController<SobelConfig> {
	public SobelTransformController() {
		super(SobelConfig.class);
	}

	@Override
	protected SobelConfig stateToInformation() {
		return new SobelConfig(previewCheckbox.isSelected());
	}
}
