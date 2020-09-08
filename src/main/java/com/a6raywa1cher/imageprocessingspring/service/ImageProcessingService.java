package com.a6raywa1cher.imageprocessingspring.service;

import javafx.scene.image.Image;

import java.io.File;

public interface ImageProcessingService {
	Image grayScale(Image image, double redWeight, double greenWeight, double blueWeight);

	void saveToFile(Image img, File file);
}
