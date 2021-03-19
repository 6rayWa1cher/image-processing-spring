package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.HoughConfig;
import javafx.scene.control.ColorPicker;
import org.springframework.stereotype.Controller;

@Controller
public class HoughTransformController extends AbstractTransformController<HoughConfig> {
	public ColorPicker picker;
	private volatile boolean updating;

	public HoughTransformController() {
		super(HoughConfig.class);
	}

	public void initialize() {
		picker.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	protected HoughConfig stateToInformation() {
		return new HoughConfig(picker.getValue(), previewCheckbox.isSelected());
	}

	@Override
	protected synchronized void informationToState(HoughConfig config) {
		updating = true;
		try {
			picker.setValue(config.getLineColor());
			previewCheckbox.setSelected(config.isPreview());
		} finally {
			updating = false;
		}
	}
}
