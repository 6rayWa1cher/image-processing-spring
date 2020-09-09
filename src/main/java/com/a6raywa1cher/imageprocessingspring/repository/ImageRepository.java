package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;

public interface ImageRepository {
	ImageBundle getImageBundle();

	int getImageBundleVersion();

	void setImageBundle(ImageBundle imageBundle);

	void setImageBundle(ImageBundle imageBundle, int version);

	GrayScaleInformation getGrayScaleInformation();

	int getGrayScaleInformationVersion();

	void setGrayScaleInformation(GrayScaleInformation information);

	void setGrayScaleInformation(GrayScaleInformation information, int version);
}
