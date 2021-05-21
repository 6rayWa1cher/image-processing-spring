package com.a6raywa1cher.imageprocessingspring.service.impl;

import com.a6raywa1cher.imageprocessingspring.service.ResourceService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

@Service
public class ResourceServiceImpl implements ResourceService {
	private final ResourceLoader resourceLoader;

	public ResourceServiceImpl(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private URL getResourceURL(String rawString) throws IOException {
		return resourceLoader.getResource("classpath:" + rawString).getURL();
	}

	@Override
	public Parent loadFXML(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getResourceURL(fxml + ".fxml"));
		return fxmlLoader.load();
	}

	@Override
	public Image loadStaticImage(String image) throws IOException {
		return new Image(getResourceURL(image).toString());
	}

	@Override
	public Set<String> getAllFilesInDirectory(String dir) {
		return Set.of("circle.png", "check.png", "rect.png"); // TODO: make real logic
	}

	public URL getStaticImageURL(String image) throws IOException {
		return getResourceURL(image);
	}
}
