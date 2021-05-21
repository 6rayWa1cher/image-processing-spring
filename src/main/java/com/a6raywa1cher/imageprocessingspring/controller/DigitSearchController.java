package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.repository.ImageRepository;
import com.a6raywa1cher.imageprocessingspring.service.ResourceService;
import com.a6raywa1cher.imageprocessingspring.service.VisionService;
import com.a6raywa1cher.imageprocessingspring.service.dto.DigitSearchResult;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Slf4j
public class DigitSearchController {
	private final ImageRepository imageRepository;
	private final ResourceService service;
	private final VisionService visionService;
	public Button button;

	@Autowired
	public DigitSearchController(ImageRepository imageRepository, ResourceService service,
								 VisionService visionService) {
		this.imageRepository = imageRepository;
		this.service = service;
		this.visionService = visionService;
	}

	@FXML
	public void apply() {
		Map<Integer, Image> digits = IntStream.range(0, 10)
			.boxed()
			.collect(Collectors.toMap(Function.identity(), i -> {
				try {
					return service.loadStaticImage("digittemplates/number" + i + ".png");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}));
		DigitSearchResult digit = visionService.findDigit(imageRepository.getImageBundle().getCurrentImage(), digits);
		log.info(digit.toString());
	}
}
