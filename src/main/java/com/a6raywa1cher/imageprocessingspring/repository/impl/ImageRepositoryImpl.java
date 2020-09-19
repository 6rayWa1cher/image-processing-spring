package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.BrightnessModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.NegativeModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.BrightnessInformation;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.model.NegativeInformation;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.util.concurrent.locks.ReentrantLock;

@Repository
@Slf4j
public class ImageRepositoryImpl implements ImageRepository, ApplicationContextAware {
	private final ReentrantLock lock = new ReentrantLock();
	private String url;
	private ApplicationContext ctx;

	private ImageBundle imageBundle;
	private int imageBundleVersion;

	private GrayScaleInformation grayScaleInformation;

	private BrightnessInformation brightnessInformation;

	private NegativeInformation negativeInformation;


	public ImageRepositoryImpl() {
		imageBundle = new ImageBundle();
		grayScaleInformation = new GrayScaleInformation();
		brightnessInformation = new BrightnessInformation();
		negativeInformation = new NegativeInformation();
	}

	private void incrementImageBundleVersion() {
		imageBundleVersion = Math.max(0, imageBundleVersion + 1);
	}

	@Override
	public ImageBundle getImageBundle() {
		return imageBundle;
	}

	@Override
	public int getImageBundleVersion() {
		return imageBundleVersion;
	}

	@Override
	public void setImageBundle(ImageBundle imageBundle) {
		lock.lock();
		try {
			this.imageBundle = imageBundle;
			incrementImageBundleVersion();
			ctx.publishEvent(new ImageModifiedEvent(imageBundle));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void setImageBundle(ImageBundle imageBundle, int version) {
		if (imageBundleVersion <= version) {
			setImageBundle(imageBundle);
		}
	}

	@Override
	public GrayScaleInformation getGrayScaleInformation() {
		return grayScaleInformation;
	}


	@Override
	public void setGrayScaleInformation(GrayScaleInformation information) {
		lock.lock();
		try {
			this.grayScaleInformation = information;
			log.info(information.toString());
			ctx.publishEvent(new GrayScaleModifiedEvent(information));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public BrightnessInformation getBrightnessInformation() {
		return brightnessInformation;
	}

	@Override
	public void setBrightnessInformation(BrightnessInformation information) {
		lock.lock();
		try {
			this.brightnessInformation = information;
			log.info(information.toString());
			ctx.publishEvent(new BrightnessModifiedEvent(information));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public NegativeInformation getNegativeInformation() {
		return negativeInformation;
	}

	@Override
	public void setNegativeInformation(NegativeInformation negativeInformation) {
		lock.lock();
		try {
			this.negativeInformation = negativeInformation;
			log.info(negativeInformation.toString());
			ctx.publishEvent(new NegativeModifiedEvent(negativeInformation));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public String getImageURL() {
		return url;
	}

	@Override
	public void setImageURL(String url) {
		lock.lock();
		try {
			this.url = url;
		} finally {
			lock.unlock();
		}
	}


	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ctx = applicationContext;
	}
}
