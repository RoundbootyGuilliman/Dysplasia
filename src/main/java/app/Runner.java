package app;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static app.Actions.*;
import static app.Utils.Context;
import static app.Utils.Shapes;

public class Runner extends Application {
	
	final static Context c = new Context();
	final static Shapes s = new Shapes();
	private StringProperty mode = new SimpleStringProperty(this, "mode", "AI");
	
	public static void main(String args[]) {
		launch(args);
	}
	
	private void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Pictures");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png")
		);
	}
	
	private void configureImage(File file, BorderPane contextRoot) {
		if (file != null) {
			Image img = new Image(file.toURI().toString());
			ImageView image = new ImageView(img);
			image.setPreserveRatio(true);
			image.fitHeightProperty().bind(contextRoot.prefHeightProperty());
			image.fitWidthProperty().bind(contextRoot.prefWidthProperty());
			contextRoot.setRight(image);
			image.toBack();
			c.xray = image;
			double ratio = img.getWidth() / img.getHeight();
			contextRoot.widthProperty().addListener((obs, oldValue, newValue) -> {
				if (!oldValue.equals(newValue)) {
					
					double oldWidth;
					double oldHeight;
					if (oldValue.doubleValue() > contextRoot.getHeight() * ratio) {
						oldHeight = contextRoot.getHeight();
						double scale = img.getHeight() / contextRoot.getHeight();
						oldWidth = img.getWidth() / scale;
					} else {
						oldWidth = oldValue.doubleValue();
						double scale = img.getWidth() / oldValue.doubleValue();
						oldHeight = img.getHeight() / scale;
					}
					
					double newWidth;
					double newHeight;
					if (newValue.doubleValue() > contextRoot.getHeight() * ratio) {
						newHeight = contextRoot.getHeight();
						double scale = img.getHeight() / contextRoot.getHeight();
						newWidth = img.getWidth() / scale;
					} else {
						newWidth = newValue.doubleValue();
						double scale = img.getWidth() / newValue.doubleValue();
						newHeight = img.getHeight() / scale;
					}
					
					c.points.forEach((key, value) -> {
						double widthRatio = oldWidth / value.getCenterX();
						double widthAddition = ((newWidth - oldWidth) / widthRatio);
						double heightRatio = oldHeight / value.getCenterY();
						double heightAddition = ((newHeight - oldHeight) / heightRatio);
						value.setCenterX(value.getCenterX() + widthAddition);
						value.setCenterY(value.getCenterY() + heightAddition);
					});
				}
			});
			
			contextRoot.heightProperty().addListener((obs, oldValue, newValue) -> {
				if (!oldValue.equals(newValue)) {
					
					double oldWidth;
					double oldHeight;
					if (contextRoot.getWidth() > oldValue.doubleValue() * ratio) {
						oldHeight = oldValue.doubleValue();
						double scale = img.getHeight() / oldValue.doubleValue();
						oldWidth = img.getWidth() / scale;
					} else {
						oldWidth = contextRoot.getWidth();
						double scale = img.getWidth() / contextRoot.getWidth();
						oldHeight = img.getHeight() / scale;
					}
					
					double newWidth;
					double newHeight;
					if (contextRoot.getWidth() > newValue.doubleValue() * ratio) {
						newHeight = newValue.doubleValue();
						double scale = img.getHeight() / newValue.doubleValue();
						newWidth = img.getWidth() / scale;
					} else {
						newWidth = contextRoot.getWidth();
						double scale = img.getWidth() / contextRoot.getWidth();
						newHeight = img.getHeight() / scale;
					}
					
					c.points.forEach((key, value) -> {
						double widthRatio = oldWidth / value.getCenterX();
						double widthAddition = ((newWidth - oldWidth) / widthRatio);
						double heightRatio = oldHeight / value.getCenterY();
						double heightAddition = ((newHeight - oldHeight) / heightRatio);
						value.setCenterX(value.getCenterX() + widthAddition);
						value.setCenterY(value.getCenterY() + heightAddition);
					});
					Stream.of(s.femoralCricleL, s.femoralCricleR).filter(Objects::nonNull).forEach(circle -> {
						double radiusRatio = oldWidth / circle.getRadius();
						double radiusAddition = ((newWidth - oldWidth) / radiusRatio);
						circle.setRadius(circle.getRadius() + radiusAddition);
					});
				}
			});
		}
		update();
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		BorderPane root = new BorderPane();
		c.rootRoot = root;
		
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Runner nodes");
		primaryStage.setMinHeight(768);
		primaryStage.setMinWidth(1024);
		primaryStage.setScene(scene);
		
		
		VBox uiPane = new VBox();
		uiPane.setMaxWidth(200);
		uiPane.setMinWidth(200);
		
		c.uiPane = uiPane;
		root.setRight(uiPane);
		
		BorderPane contextRoot = new BorderPane();
		contextRoot.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		contextRoot.prefWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		contextRoot.prefHeightProperty().bind(scene.heightProperty());
		contextRoot.maxWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		contextRoot.maxHeightProperty().bind(scene.heightProperty());
		
		c.root = contextRoot;
		root.setCenter(contextRoot);
		
		FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		
		Button openButton = new Button("Open a Picture...");
		
		Button clearButton = new Button("Clear");
		clearButton.setDisable(true);
		
		List<Button> buttons = Stream.of("AI", "ADR", "CEA", "AA", "RI", "CI").map(Button::new).collect(Collectors.toList());
		
		Button ai = buttons.get(0);
		buttons.forEach(button -> button.setOnAction(event -> {
			buttons.forEach(buttonInner -> buttonInner.setDisable(false));
			button.setDisable(true);
			mode.set(button.getText());
			c.pointsRequired.clear();
			switch (button.getText()) {
				case "AI":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp3", "rp2", "rp3"));
					
					break;
				case "ADR":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp3", "lp4", "rp2", "rp3", "rp4"));
					
					break;
				case "CEA":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "rp1", "rp2"));
					
					break;
				case "AA":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp4", "rp2", "rp4"));
					
					break;
				case "RI":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "rp1", "rp2"));
					
					break;
				case "CI":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "lp4", "rp1", "rp2", "rp4"));
					
					break;
			}
			clearButton.fire();
			update();
		}));
		
		
		buttons.addAll(0, Arrays.asList(openButton, clearButton));
		GridPane inputGridPane = new GridPane();
		
		int index = 0;
		for (Button button : buttons) {
			GridPane.setConstraints(button, 0, index);
			index++;
		}
		inputGridPane.setHgap(6);
		inputGridPane.setVgap(6);
		inputGridPane.getChildren().addAll(buttons);
		
		uiPane.getChildren().add(inputGridPane);
		
		c.textL = new Text(90, 100, "");
		c.textL.setFont(Font.font(20));
		c.textL.setStroke(Color.YELLOW);
		
		
		c.textR = new Text(600, 100, "");
		c.textR.setFont(Font.font(20));
		c.textR.setStroke(Color.YELLOW);
		
		c.textC = new Text(300, 100, "");
		c.textC.setFont(Font.font(20));
		c.textC.setStroke(Color.YELLOW);
		
		Stream.of(c.textL, c.textR).forEach(text ->
				text.textProperty().addListener(((observable, oldValue, newValue) -> {
					if (!newValue.contains(mode.get()) && !newValue.equals("")) {
						text.setText(mode.getValue() + " = " + newValue);
					}
				})));
		
//		c.textL.textProperty().bind(primaryStage.widthProperty().subtract(200).asString().concat("x").concat(primaryStage.heightProperty().asString()));
//		c.textR.textProperty().bind(scene.widthProperty().subtract(200).asString().concat("x").concat(scene.heightProperty().asString()));
		
		contextRoot.getChildren().addAll(c.textL, c.textR, c.textC);
		
		openButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			configureImage(file, contextRoot);
		});
		
		
		clearButton.setOnAction(e -> {
			
			contextRoot.getChildren().removeIf(node -> node.getClass() == Circle.class || node.getClass() == Line.class);
			c.points.clear();
			clearButton.setDisable(true);
			c.pointsDrawn = false;
			c.currentPoint = c.pointsRequired.get(0);
			c.textL.setText("");
			c.textR.setText("");
			Stream.of(s.getClass().getDeclaredFields()).forEach(field -> {
				try {
					field.set(s, null);
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
			});
			update();
		});
		
		contextRoot.setOnMouseReleased(event -> {
			
			
			if (c.xray != null) {
				for (int i = 0; i < c.pointsRequired.size(); i++) {
					
					String req = c.pointsRequired.get(i);
					
					if (c.points.get(req) == null) {
						clearButton.setDisable(false);
						Circle newPoint = new Circle(event.getX(), event.getY(), 10);
						
						makeDraggable(newPoint);
						
						contextRoot.getChildren().add(newPoint);
						c.points.put(req, newPoint);
						if (i + 1 < c.pointsRequired.size()) {
							c.currentPoint = c.pointsRequired.get(i + 1);
						} else {
							calculate();
							c.pointsDrawn = true;
						}
						break;
					}
				}
			}
			update();
		});
		ai.fire();
		File initialFile = new File(System.getProperty("user.home") + "/Desktop/5230e6e4807b8f13fce73b2b7d542f.jpg");
		configureImage(initialFile, contextRoot);
		primaryStage.show();
		update();
	}
	
	private void update() {
		
		if (c.xray == null) {
			c.textC.setText("Upload an X-Ray");
		} else if (!c.pointsDrawn) {
			c.textC.setText("Mark " + c.currentPoint.replace("rp", "right point ").replace("lp", "left point "));
		} else {
			c.textC.setText("");
		}
		c.rootRoot.getChildren().clear();
		c.rootRoot.setLeft(c.root);
		c.rootRoot.setRight(c.uiPane);
		System.out.println(c.root.getChildren().size());
		c.frontNodes.forEach(Node::toFront);
		c.innerPoints.forEach(Node::toFront);
		
		c.points.forEach((s, circle) -> {
			if (circle != null) {
				circle.toFront();
			}
		});
	}
	
	private void calculate() {
		switch (mode.get()) {
			
			case "ADR":
				calculateAdr();
				
				break;
			
			case "AI":
				calculateAi();
				
				break;
			
			case "CEA":
				calculateCea();
				
				break;
			
			case "AA":
				calculateAa();
				
				break;
			
			case "RI":
				calculateRi();
				
				break;
			
			case "CI":
				calculateCi();
				
				break;
		}
		Stream.of(s.getClass().getDeclaredFields()).forEach(field -> {
			try {
				if (field.get(s) != null) {
					c.root.getChildren().add((Node) field.get(s));
				}
				
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
		});
		update();
	}
	
	private void makeDraggable(final Circle point) {
		
		
		point.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
			c.moved = false;
			c.mouseAnchorX = mouseEvent.getX();
			c.mouseAnchorY = mouseEvent.getY();
			c.initialCenterX = point.getCenterX();
			c.initialCenterY = point.getCenterY();
			
		});
		
		
		point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
			
			c.moved = true;
			
			point.setCenterX(c.initialCenterX
					+ mouseEvent.getX()
					- c.mouseAnchorX);
			point.setCenterY(c.initialCenterY
					+ mouseEvent.getY()
					- c.mouseAnchorY);
		});
		
		point.addEventFilter(MouseEvent.MOUSE_RELEASED, Event::consume);
		
		Circle innerPoint = new Circle(point.getCenterX(), point.getCenterY(), 3);
		
		point.setFill(Color.TRANSPARENT);
		innerPoint.setFill(Color.YELLOW);
		innerPoint.centerXProperty().bind(point.centerXProperty());
		innerPoint.centerYProperty().bind(point.centerYProperty());
		
		c.root.getChildren().add(innerPoint);
		c.innerPoints.add(innerPoint);
		
		
		point.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> point.getScene().setCursor(Cursor.HAND));
		
		point.addEventFilter(MouseEvent.MOUSE_EXITED, event -> point.getScene().setCursor(Cursor.DEFAULT));
		
		//point.addEventFilter(MouseEvent.ANY, Event::consume);
	}
}