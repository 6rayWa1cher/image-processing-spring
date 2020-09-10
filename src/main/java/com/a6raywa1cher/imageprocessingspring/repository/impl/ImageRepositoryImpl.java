package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.GrayScaleInformation;
import com.a6raywa1cher.imageprocessingspring.model.ImageBundle;
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
	private ImageBundle imageBundle;

	private String url;

	private final ReentrantLock lock = new ReentrantLock();

	private GrayScaleInformation grayScaleInformation;
	private int imageBundleVersion;

	private ApplicationContext ctx;

	public ImageRepositoryImpl() {
		imageBundle = new ImageBundle();
		grayScaleInformation = new GrayScaleInformation();
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
