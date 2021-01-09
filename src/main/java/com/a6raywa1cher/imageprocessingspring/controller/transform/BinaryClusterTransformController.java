package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.BinaryClusterConfig;
import org.springframework.stereotype.Controller;

@Controller
public class BinaryClusterTransformController extends AbstractTransformController<BinaryClusterConfig> {
	public BinaryClusterTransformController() {
		super(BinaryClusterConfig.class);
	}

	@Override
	protected BinaryClusterConfig stateToInformation() {
		return new BinaryClusterConfig(previewCheckbox.isSelected());
	}
}
