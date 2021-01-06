package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ConfigModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.event.SelectChangedEvent;
import com.a6raywa1cher.imageprocessingspring.model.SelectConfig;
import com.a6raywa1cher.imageprocessingspring.repository.model.MouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.repository.model.SelectedAreaMouseInnerEvent;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class SelectRenderController {
	private final ImageProcessingService service;
	private SelectConfig currConfig = new SelectConfig();

	public SelectRenderController(ImageProcessingService service) {
		this.service = service;
	}

	@EventListener
	public void onApplicationEvent(ConfigModifiedEvent<SelectConfig> event) {
		if (!event.getClazz().equals(SelectConfig.class)) return;
		currConfig = event.getConfig();
	}

	@EventListener(SelectChangedEvent.class)
	public void onApplicationEvent(SelectChangedEvent event) {
		SelectConfig selectConfig = new SelectConfig();
		List<MouseInnerEvent> lastEvents = event.getLastEvents();
		if (!lastEvents.isEmpty()) {
			MouseInnerEvent innerEvent = lastEvents.get(0);
			if (innerEvent instanceof SelectedAreaMouseInnerEvent) {
				SelectedAreaMouseInnerEvent selectedEvent = (SelectedAreaMouseInnerEvent) innerEvent;
				selectConfig.setX1((int) selectedEvent.getP1().getX());
				selectConfig.setY1((int) selectedEvent.getP1().getY());
				selectConfig.setX2((int) selectedEvent.getP2().getX());
				selectConfig.setY2((int) selectedEvent.getP2().getY());
				selectConfig.setColor(currConfig.getColor());
				selectConfig.setPreview(true);
			}
		}
		service.setConfig(selectConfig, SelectConfig.class);
	}
}
