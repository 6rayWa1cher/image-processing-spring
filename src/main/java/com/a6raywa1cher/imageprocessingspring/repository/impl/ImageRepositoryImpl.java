package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

@Repository
public class ImageRepositoryImpl implements ImageRepository, ApplicationContextAware {
	private ImageBundle imageBundle;

	private GrayScaleInformation grayScaleInformation;

	private ApplicationContext ctx;

	public ImageRepositoryImpl() {
		imageBundle = new ImageBundle();
		grayScaleInformation = new GrayScaleInformation();
	}

	@Override
	public ImageBundle getImageBundle() {
		return imageBundle;
	}

	@Override
	public void setImageBundle(ImageBundle imageBundle) {
		this.imageBundle = imageBundle;
		ctx.publishEvent(new ImageModifiedEvent(imageBundle));
	}

	@Override
	public GrayScaleInformation getGrayScaleInformation() {
		return grayScaleInformation;
	}

	@Override
	public void setGrayScaleInformation(GrayScaleInformation information) {
		this.grayScaleInformation = information;
		ctx.publishEvent(new GrayScaleModifiedEvent(information));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}
}
