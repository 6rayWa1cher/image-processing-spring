package com.a6raywa1cher.imageprocessingspring.controller;

import javafx.scene.control.*;
import org.springframework.stereotype.Controller;

@Controller
public class GrayScaleController {
	public Slider grayScaleRedSlider;
	public Label grayScaleRedSliderLabel;
	public Slider grayScaleGreenSlider;
	public Label grayScaleGreenSliderLabel;
	public Slider grayScaleBlueSlider;
	public Label grayScaleBlueSliderLabel;
	public ColorPicker grayScaleBaseColor;
	public CheckBox grayScalePreviewCheckbox;
	public Button grayScaleApplyButton;
}
