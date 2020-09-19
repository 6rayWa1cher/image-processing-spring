package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.Brightness;
import com.a6raywa1cher.imageprocessingspring.model.GrayScale;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.model.Negative;

public interface ImageRepository {
	ImageBundle getImageBundle();

	int getImageBundleVersion();

	void setImageBundle(ImageBundle imageBundle);

	void setImageBundle(ImageBundle imageBundle, int version);

	GrayScale getGrayScale();

	void setGrayScale(GrayScale grayScale);

	Brightness getBrightness();

	void setBrightness(Brightness brightness);

	Negative getNegative();

	void setNegative(Negative negative);

	String getImageURL();

	void setImageURL(String url);
}
