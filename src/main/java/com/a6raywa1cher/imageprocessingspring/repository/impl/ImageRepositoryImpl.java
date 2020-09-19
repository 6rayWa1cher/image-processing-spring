package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.BrightnessModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.NegativeModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.Brightness;
import com.a6raywa1cher.imageprocessingspring.model.GrayScale;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
import com.a6raywa1cher.imageprocessingspring.model.Negative;
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

	private GrayScale grayScale;

	private Brightness brightness;

	private Negative negative;


	public ImageRepositoryImpl() {
		imageBundle = new ImageBundle();
		grayScale = new GrayScale();
		brightness = new Brightness();
		negative = new Negative();
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
	public GrayScale getGrayScale() {
		return grayScale;
	}


	@Override
	public void setGrayScale(GrayScale grayScale) {
		lock.lock();
		try {
			this.grayScale = grayScale;
			log.info(grayScale.toString());
			ctx.publishEvent(new GrayScaleModifiedEvent(grayScale));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Brightness getBrightness() {
		return brightness;
	}

	@Override
	public void setBrightness(Brightness information) {
		lock.lock();
		try {
			this.brightness = information;
			log.info(information.toString());
			ctx.publishEvent(new BrightnessModifiedEvent(information));
		} finally {
			lock.unlock();
		}
	}

	@Override
	public Negative getNegative() {
		return negative;
	}

	@Override
	public void setNegative(Negative negative) {
		lock.lock();
		try {
			this.negative = negative;
			log.info(negative.toString());
			ctx.publishEvent(new NegativeModifiedEvent(negative));
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
