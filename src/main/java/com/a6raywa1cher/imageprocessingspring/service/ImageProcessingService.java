package com.a6raywa1cher.imageprocessingspring.service;

import com.a6raywa1cher.imageprocessingspring.model.Config;
import javafx.scene.image.Image;

import java.io.File;

public interface ImageProcessingService {
	<T extends Config> void setConfig(T config, Class<T> tClass);

	<T extends Config> void applyConfig(T config, Class<T> tClass);

	void saveFile();

	void saveToFile(File file);

	void openFile(Image image, String url);

	void openFile(String url);
}
