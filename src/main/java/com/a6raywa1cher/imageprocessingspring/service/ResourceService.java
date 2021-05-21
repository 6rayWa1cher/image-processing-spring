package com.a6raywa1cher.imageprocessingspring.service;

import javafx.scene.Parent;
import javafx.scene.image.Image;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

public interface ResourceService {
	Parent loadFXML(String fxml) throws IOException;

	Image loadStaticImage(String image) throws IOException;

	Set<String> getAllFilesInDirectory(String dir);

	URL getStaticImageURL(String image) throws IOException;
}
