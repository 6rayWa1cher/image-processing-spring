package com.a6raywa1cher.imageprocessingspring.service;

import com.a6raywa1cher.imageprocessingspring.model.Brightness;
import com.a6raywa1cher.imageprocessingspring.model.GrayScale;
import com.a6raywa1cher.imageprocessingspring.model.Negative;
import javafx.scene.image.Image;

import java.io.File;

public interface ImageProcessingService {
	void setGrayScale(GrayScale grayScale);

	void applyGrayScale(GrayScale grayScale);

	void setBrightness(Brightness brightness);

	void applyBrightness(Brightness brightness);

	void setNegative(Negative negative);

	void applyNegative(Negative negative);

	void saveFile();

	void saveToFile(File file);

	void openFile(Image image, String url);

	void openFile(String url);
}
