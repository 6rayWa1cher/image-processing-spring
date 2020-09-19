package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.BrightnessInformation;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.model.NegativeInformation;

public interface ImageRepository {
	ImageBundle getImageBundle();

	int getImageBundleVersion();

	void setImageBundle(ImageBundle imageBundle);

	void setImageBundle(ImageBundle imageBundle, int version);

	GrayScaleInformation getGrayScaleInformation();

	void setGrayScaleInformation(GrayScaleInformation information);

	BrightnessInformation getBrightnessInformation();

	void setBrightnessInformation(BrightnessInformation brightnessInformation);

	NegativeInformation getNegativeInformation();

	void setNegativeInformation(NegativeInformation negativeInformation);

	String getImageURL();

	void setImageURL(String url);
}
