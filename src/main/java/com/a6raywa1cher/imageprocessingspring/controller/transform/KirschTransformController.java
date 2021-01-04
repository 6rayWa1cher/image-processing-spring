package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.KirschConfig;
import org.springframework.stereotype.Controller;

@Controller
public class KirschTransformController extends AbstractTransformController<KirschConfig> {
	public KirschTransformController() {
		super(KirschConfig.class);
	}

	@Override
	protected KirschConfig stateToInformation() {
		return new KirschConfig(previewCheckbox.isSelected());
	}
}
