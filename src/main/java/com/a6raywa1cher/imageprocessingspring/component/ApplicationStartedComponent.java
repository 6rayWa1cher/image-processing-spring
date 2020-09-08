package com.a6raywa1cher.imageprocessingspring.component;

import com.a6raywa1cher.imageprocessingspring.event.GrayScaleModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.JavaFXApplicationStartedEvent;
import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import com.a6raywa1cher.imageprocessingspring.service.ResourceService;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(2)
public class ApplicationStartedComponent implements ApplicationListener<JavaFXApplicationStartedEvent>, ApplicationContextAware {
	private final ImageRepository imageRepository;
	private final ImageProcessingService imageProcessingService;
	private final ResourceService resourceService;
	private ApplicationContext ctx;

	public ApplicationStartedComponent(ImageRepository imageRepository, ImageProcessingService imageProcessingService,
									   ResourceService resourceService) {
		this.imageRepository = imageRepository;
		this.imageProcessingService = imageProcessingService;
		this.resourceService = resourceService;
	}

	@SneakyThrows
	@Override
	public void onApplicationEvent(JavaFXApplicationStartedEvent event) {
		imageProcessingService.openFile(
			resourceService.loadStaticImage("sampleImage.png"),
			resourceService.getStaticImageURL("sampleImage.png").toString()
		);
		ctx.publishEvent(new GrayScaleModifiedEvent(imageRepository.getGrayScaleInformation()));
		ctx.publishEvent(new ImageModifiedEvent(imageRepository.getImageBundle()));
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}
}
