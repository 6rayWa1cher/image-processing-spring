package com.a6raywa1cher.imageprocessingspring;

import com.a6raywa1cher.imageprocessingspring.event.JavaFXApplicationStartedEvent;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class JavaFXApplication extends Application {
	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		ApplicationContextInitializer<GenericApplicationContext> initializer = (ac) -> {
			ac.registerBean(Application.class, () -> this);
			ac.registerBean(Parameters.class, this::getParameters);
			ac.registerBean(HostServices.class, this::getHostServices);
		};
		this.context = new SpringApplicationBuilder()
			.sources(ImageProcessingSpringApplication.class)
			.initializers(initializer)
			.run(this.getParameters().getRaw().toArray(new String[0]));
	}

	@Override
	public void start(Stage stage) {
		this.context.publishEvent(new JavaFXApplicationStartedEvent(stage));
	}

	@Override
	public void stop() {
		context.close();
		Platform.exit();
		System.exit(0);
	}
}
