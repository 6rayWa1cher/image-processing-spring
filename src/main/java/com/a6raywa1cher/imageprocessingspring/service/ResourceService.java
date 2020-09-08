package com.a6raywa1cher.imageprocessingspring.service;

import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;

public interface ResourceService {
	Parent loadFXML(String fxml) throws IOException;

	Image loadStaticImage(String image) throws IOException;

	URL getStaticImageURL(String image) throws IOException;
}
