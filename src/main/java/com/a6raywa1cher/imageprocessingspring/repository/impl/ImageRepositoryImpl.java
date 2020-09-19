package com.a6raywa1cher.imageprocessingspring.repository.impl;

import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.model.*;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Repository
@Slf4j
public class ImageRepositoryImpl implements ImageRepository, ApplicationContextAware {
	private final ReentrantLock lock = new ReentrantLock();
	private final Map<Class<?>, Config> container;

	private String url;
	private ApplicationContext ctx;

	private ImageBundle imageBundle;
	private int imageBundleVersion;


	public ImageRepositoryImpl() {
		imageBundle = new ImageBundle();
		container = new HashMap<>();
		container.put(GrayScale.class, new GrayScale());
		container.put(Negative.class, new Negative());
		container.put(Brightness.class, new Brightness());
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
	public <T> T getConfig(Class<T> tClass) {
		return (T) container.get(tClass);
	}

	@Override
	public Map<Class<?>, Config> getAllConfigs() {
		return new HashMap<>(container);
	}

	@Override
	public <T> void setConfig(T config, Class<T> tClass) {
		lock.lock();
		try {
			Config value = (Config) config;
			container.put(tClass, value);
			log.info(config.toString());
			ctx.publishEvent(value.getEvent());
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
