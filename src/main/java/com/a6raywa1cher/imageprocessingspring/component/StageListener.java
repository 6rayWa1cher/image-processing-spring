package com.a6raywa1cher.imageprocessingspring.component;

import com.a6raywa1cher.imageprocessingspring.event.JavaFXApplicationStartedEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;

@Component
public class StageListener implements ApplicationListener<JavaFXApplicationStartedEvent> {
	private final ApplicationContext ctx;
	private final Resource resource;

	public StageListener(ApplicationContext ctx, @Value("classpath:/mainWindow.fxml") Resource resource) {
		this.ctx = ctx;
		this.resource = resource;
	}

	@Override
	public void onApplicationEvent(JavaFXApplicationStartedEvent event) {
		try {
			Stage stage = event.getStage();
			URL url = this.resource.getURL();
			FXMLLoader loader = new FXMLLoader(url);
			loader.setControllerFactory(ctx::getBean);
			Parent root = loader.load();
			Scene scene = new Scene(root, 620, 480);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
