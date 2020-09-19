package com.a6raywa1cher.imageprocessingspring.service;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessInformation;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import javafx.scene.image.Image;

import java.io.File;

public interface ImageProcessingService {
	void setGrayScaleInformation(GrayScaleInformation grayScaleInformation);

	void applyGrayScaleInformation(GrayScaleInformation grayScaleInformation);

	void setBrightnessInformation(BrightnessInformation brightnessInformation);

	void applyBrightnessInformation(BrightnessInformation brightnessInformation);

	void saveFile();

	void saveToFile(File file);

	void openFile(Image image, String url);

	void openFile(String url);
}
