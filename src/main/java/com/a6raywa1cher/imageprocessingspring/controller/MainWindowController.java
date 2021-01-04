package com.a6raywa1cher.imageprocessingspring.controller;

import com.a6raywa1cher.imageprocessingspring.event.ImageModifiedEvent;
import com.a6raywa1cher.imageprocessingspring.service.ImageProcessingService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
public class MainWindowController {
	private final ImageProcessingService service;
	public Canvas histogramCanvas;
	public ScrollPane scrollPane1;
	public AnchorPane anchorPane1;
	private double[] cachedHistogramInfo = new double[256];

	@Autowired
	public MainWindowController(ImageProcessingService service) {
		this.service = service;
	}

	public void initialize() {
		histogramCanvas.widthProperty().addListener(e -> renderHistogram(cachedHistogramInfo));
		histogramCanvas.heightProperty().addListener(e -> renderHistogram(cachedHistogramInfo));
		scrollPane1.viewportBoundsProperty().addListener((observableValue, o, n) -> anchorPane1.setPrefWidth(n.getMaxX()));
	}

	public Stage getStage() {
		return (Stage) histogramCanvas.getScene().getWindow();
	}

	@FXML
	public void onOpenFile() throws MalformedURLException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open image");
		FileChooser.ExtensionFilter anyFile = new FileChooser.ExtensionFilter("Compatible image", "*.png", "*.jpg", "*.jpeg", "*.gif");
		fileChooser.getExtensionFilters().add(anyFile);
		fileChooser.getExtensionFilters().addAll(getFilters());
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Any file", "*"));
		fileChooser.setSelectedExtensionFilter(anyFile);

		File file = fileChooser.showOpenDialog(getStage());
		if (file != null) {
			service.openFile(file.toURI().toURL().toString());
		}
	}

	@FXML
	public void onSaveFile() {
		service.saveFile();
	}

	@FXML
	public void onSaveFileAs() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save image");
		fileChooser.getExtensionFilters().addAll(getFilters());
		fileChooser.setSelectedExtensionFilter(getDefaultFilter());
		File file = fileChooser.showSaveDialog(getStage());
		if (file != null) {
			service.saveToFile(file);
		}
	}

	@EventListener(ImageModifiedEvent.class)
	public void onApplicationEvent(ImageModifiedEvent event) {
		Platform.runLater(() -> {
			cachedHistogramInfo = event.getImageBundle().getHistogramMetric();
			renderHistogram(cachedHistogramInfo);
		});
	}

	private FileChooser.ExtensionFilter getDefaultFilter() {
		return new FileChooser.ExtensionFilter("PNG", "*.png");
	}

	private List<FileChooser.ExtensionFilter> getFilters() {
		return List.of(getDefaultFilter(),
			new FileChooser.ExtensionFilter("JPEG-image", "*.jpg", "*.jpeg"),
			new FileChooser.ExtensionFilter("GIF", "*.gif"));
	}

	private Paint grayToColor(int value) {
		String s = String.format("#%02X%02X%02X", value, value, value);
		return Paint.valueOf(s);
	}

	private void renderHistogram(double[] metrics) {
		double width = histogramCanvas.getWidth();
		double height = histogramCanvas.getHeight() - 3d;
		GraphicsContext gctx = histogramCanvas.getGraphicsContext2D();
		gctx.setFill(Paint.valueOf("#A8A8FF"));
		gctx.fillRect(0, 0, width, height);
		if (metrics != null) {
			double segmentSize = width / (double) metrics.length;
			double maximum = Arrays.stream(metrics).max().orElseThrow();
			for (int i = 0; i < 256; i++) {
				double offset = i * segmentSize;
				gctx.setFill(grayToColor(i));
				double metric = metrics[i];
				double columnHeight = height * metric / maximum;
				gctx.fillRect(offset, height - columnHeight, segmentSize + 2d, columnHeight + 3d);
			}
		}
	}
}
