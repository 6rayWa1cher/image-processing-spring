package com.a6raywa1cher.imageprocessingspring.controller.transform;

import com.a6raywa1cher.imageprocessingspring.model.ScalingConfig;
import com.a6raywa1cher.imageprocessingspring.model.SearchConfig;
import com.a6raywa1cher.imageprocessingspring.service.ResourceService;
import com.sun.javafx.collections.ImmutableObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Controller
public class SearchTransformController extends AbstractTransformController<SearchConfig> {
	public ChoiceBox<String> templateChooser;
	private volatile boolean updating;

	private final ResourceService resourceService;


	public SearchTransformController(ResourceService resourceService) {
		super(SearchConfig.class);
		this.resourceService = resourceService;
	}

	public void initialize() {
		templateChooser.setItems(new ImmutableObservableList<>(
			resourceService.getAllFilesInDirectory("templates").stream()
				.sorted()
				.toArray(String[]::new)
		));
		templateChooser.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (!updating) onChange();
		});
	}

	@Override
	@SneakyThrows
	protected SearchConfig stateToInformation() {
		return new SearchConfig(
			StringUtils.hasText(templateChooser.getValue()) ? resourceService.loadStaticImage("templates/" + templateChooser.getValue()) : null,
			"templates/" + templateChooser.getValue(),
			Color.RED,
			previewCheckbox.isSelected()
		);
	}

	@Override
	protected synchronized void informationToState(SearchConfig tConfig) {
		updating = true;
		try {
			String templateUrl = tConfig.getTemplateUrl();
			if (templateUrl != null) templateChooser.setValue(templateUrl.substring("templates/".length()));
			previewCheckbox.setSelected(tConfig.isPreview());
		} finally {
			updating = false;
		}
	}
}
