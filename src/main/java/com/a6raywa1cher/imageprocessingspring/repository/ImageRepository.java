package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;

public interface ImageRepository {
	ImageBundle getImageBundle();

	int getImageBundleVersion();

	void setImageBundle(ImageBundle imageBundle);

	void setImageBundle(ImageBundle imageBundle, int version);

	GrayScaleInformation getGrayScaleInformation();

	void setGrayScaleInformation(GrayScaleInformation information);

	String getImageURL();

	void setImageURL(String url);
}
