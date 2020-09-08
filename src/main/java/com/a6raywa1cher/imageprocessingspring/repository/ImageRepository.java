package com.a6raywa1cher.imageprocessingspring.repository;

import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;

public interface ImageRepository {
	ImageBundle getImageBundle();

	void setImageBundle(ImageBundle imageBundle);

	GrayScaleInformation getGrayScaleInformation();

	void setGrayScaleInformation(GrayScaleInformation information);
}
