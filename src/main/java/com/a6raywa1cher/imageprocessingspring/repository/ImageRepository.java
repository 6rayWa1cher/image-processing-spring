package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.Config;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;

import java.util.Map;

public interface ImageRepository {
	ImageBundle getImageBundle();

	int getImageBundleVersion();

	void setImageBundle(ImageBundle imageBundle);

	void setImageBundle(ImageBundle imageBundle, int version);

	<T> T getConfig(Class<T> tClass);

	Map<Class<?>, Config> getAllConfigs();

	<T> void setConfig(T config, Class<T> tClass);

	String getImageURL();

	void setImageURL(String url);
}
