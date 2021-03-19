package com.a6raywa1cher.imageprocessingspring.service;

import com.a6raywa1cher.imageprocessingspring.service.dto.Circle;
import com.a6raywa1cher.imageprocessingspring.service.dto.Line;
import javafx.scene.image.Image;

import java.util.Set;

public interface VisionService {
	Set<Line> findAllLines(Image image);

	Set<Circle> findAllCircles(Image image, int radius);
}
