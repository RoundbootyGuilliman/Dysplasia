package app;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.Event;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
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
	
	private void configureImage(File file, HBox background, Pane imgRoot) {
		if (file != null) {
			Image img = new Image(file.toURI().toString());
			ImageView image = new ImageView(img);
			image.setPreserveRatio(true);
			image.fitHeightProperty().bind(background.prefHeightProperty());
			image.fitWidthProperty().bind(background.prefWidthProperty());
			imgRoot.getChildren().addAll(image);
			image.setOnMouseEntered(event -> System.out.println("ENTERED IMG"));
			imgRoot.setOnMouseEntered(event -> System.out.println("ENTERED IMGROOT"));
			//image.toBack();
			c.xray = image;
			double ratio = img.getWidth() / img.getHeight();
			background.widthProperty().addListener((obs, oldValue, newValue) -> {
				if (!oldValue.equals(newValue)) {

					double oldWidth;
					double oldHeight;
					if (oldValue.doubleValue() > background.getHeight() * ratio) {
						oldHeight = background.getHeight();
						double scale = img.getHeight() / background.getHeight();
						oldWidth = img.getWidth() / scale;
					} else {
						oldWidth = oldValue.doubleValue();
						double scale = img.getWidth() / oldValue.doubleValue();
						oldHeight = img.getHeight() / scale;
					}

					double newWidth;
					double newHeight;
					if (newValue.doubleValue() > background.getHeight() * ratio) {
						newHeight = background.getHeight();
						double scale = img.getHeight() / background.getHeight();
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

			background.heightProperty().addListener((obs, oldValue, newValue) -> {
				if (!oldValue.equals(newValue)) {

					double oldWidth;
					double oldHeight;
					if (background.getWidth() > oldValue.doubleValue() * ratio) {
						oldHeight = oldValue.doubleValue();
						double scale = img.getHeight() / oldValue.doubleValue();
						oldWidth = img.getWidth() / scale;
					} else {
						oldWidth = background.getWidth();
						double scale = img.getWidth() / background.getWidth();
						oldHeight = img.getHeight() / scale;
					}

					double newWidth;
					double newHeight;
					if (background.getWidth() > newValue.doubleValue() * ratio) {
						newHeight = newValue.doubleValue();
						double scale = img.getHeight() / newValue.doubleValue();
						newWidth = img.getWidth() / scale;
					} else {
						newWidth = background.getWidth();
						double scale = img.getWidth() / background.getWidth();
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
	
	private BorderPane configureContextRoot(Button clearButton) {
		BorderPane contextRoot = new BorderPane();
		contextRoot.prefWidthProperty().bind(c.imgRoot.widthProperty());
		contextRoot.prefHeightProperty().bind(c.imgRoot.heightProperty());
		contextRoot.maxWidthProperty().bind(c.imgRoot.widthProperty());
		contextRoot.maxHeightProperty().bind(c.imgRoot.heightProperty());
		
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
							c.pointsDrawn.set(true);
						}
						break;
					}
				}
			}
			update();
		});
		
		return contextRoot;
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		
		Button openButton = new Button("Open a Picture...");
		
		Button clearButton = new Button("Clear");
		
		Button saveButton = new Button("Save");
		
		Button statButton = new Button("Stats");
		
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
		Label label = new Label("Label with text");
		c.textC = label;
		
		Label patientName = new Label("Имя пациента");
		TextField nameField = new TextField();
		patientName.setFont(Font.font(null, FontWeight.BOLD, 20));
		patientName.setTextFill(Color.WHITE);
		
		Label patientLastName = new Label("Фамилия пациента");
		TextField lastNameField = new TextField();
		patientLastName.setFont(Font.font(null, FontWeight.BOLD, 20));
		patientLastName.setTextFill(Color.WHITE);
		
		List<Node> nodes = Arrays.asList(openButton, clearButton, saveButton, statButton, label, patientName, nameField, patientLastName, lastNameField);
		GridPane inputGridPane = new GridPane();
		
		int index = 0;
		for (Node node : nodes) {
			GridPane.setConstraints(node, 0, index);
			index++;
		}
		inputGridPane.getChildren().addAll(nodes);
		
		TilePane inputTilePane = new TilePane();
		inputTilePane.setOrientation(Orientation.HORIZONTAL);
		inputTilePane.setPrefColumns(6);
		inputTilePane.setMaxHeight(40);
		inputTilePane.setMinHeight(40);
		
		buttons.forEach(button -> {
			button.setShape(new Rectangle(50, 40, Color.DARKBLUE));
			button.setPrefWidth(100);
			button.setPrefHeight(40);
			//button.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");
			button.setFont(Font.font(null, FontWeight.BOLD, 20));
			button.setTextFill(Color.WHITE);
			
//			button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null)));
//			button.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
//					button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null))));
//			button.addEventFilter(MouseEvent.MOUSE_EXITED, event ->
//					button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null))));
			
			
			button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null)));
			button.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
					button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null))));
			button.addEventFilter(MouseEvent.MOUSE_EXITED, event ->
					button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null))));
			
		});
		
		Stream.of(openButton, clearButton, saveButton, statButton).forEach(button -> {
			button.setShape(new Rectangle(50, 40, Color.DARKBLUE));
			button.setPrefWidth(200);
			button.setPrefHeight(40);
			//button.setStyle("-fx-background-color: #457ecd; -fx-text-fill: #ffffff;");
			button.setFont(Font.font(null, FontWeight.BOLD, 20));
			button.setTextFill(Color.WHITE);
			
//			button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null)));
//			button.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
//					button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null))));
//			button.addEventFilter(MouseEvent.MOUSE_EXITED, event ->
//					button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null))));
			
			button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null)));
			button.addEventFilter(MouseEvent.MOUSE_ENTERED, event ->
					button.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null))));
			button.addEventFilter(MouseEvent.MOUSE_EXITED, event ->
					button.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null))));
		});
		
		inputTilePane.getChildren().addAll(buttons);
		
		
		
		Screen screen = Screen.getPrimary();
		c.primaryStage = primaryStage;
		BorderPane root = new BorderPane();
		c.rootRoot = root;
		
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Runner nodes");
		primaryStage.setMinHeight(768);
		primaryStage.setMinWidth(1024);
		primaryStage.setMaxHeight(screen.getVisualBounds().getMaxY());
		primaryStage.setMaxWidth(screen.getVisualBounds().getMaxX());
		primaryStage.setScene(scene);
		
		
		VBox uiPane = new VBox();
		uiPane.setMaxWidth(200);
		uiPane.setMinWidth(200);
		
		
		c.uiPane = uiPane;
		
		
		c.buttonsPane = inputTilePane;
		
//		uiPane.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null)));
//		inputTilePane.setBackground(new Background(new BackgroundFill(Color.rgb(36, 37, 42), null, null)));
		
		uiPane.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null)));
		inputTilePane.setBackground(new Background(new BackgroundFill(Color.rgb(1, 50, 67), null, null)));
		
		HBox background = new HBox();
		c.background = background;
		background.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		
		background.prefWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.prefHeightProperty().bind(scene.heightProperty().subtract(inputTilePane.getMaxHeight()));
		background.maxWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.maxHeightProperty().bind(scene.heightProperty().subtract(inputTilePane.getMaxHeight()));
		
		background.setAlignment(Pos.CENTER);
		background.setFillHeight(false);
		
		root.setRight(uiPane);
		root.setCenter(background);
		root.setTop(inputTilePane);
		
		Pane imgRoot = new Pane();
		c.imgRoot = imgRoot;
		
		BorderPane contextRoot = configureContextRoot(clearButton);
		c.root = contextRoot;
		
		background.getChildren().addAll(new Pane(imgRoot, contextRoot));
		
		
		background.setOnMouseEntered(event -> System.out.println("ENTERED BACKGROUND"));
		contextRoot.setOnMouseEntered(event -> System.out.println("ENTERED contextRoot"));
		
		c.textL = new Text(90, 100, "");
		c.textL.xProperty().bind(c.root.layoutXProperty().add(c.root.prefWidthProperty().divide(10)));
		c.textL.yProperty().bind(c.root.prefHeightProperty().divide(6));
		c.textL.setFont(Font.font(20));
		c.textL.setStroke(Color.YELLOW);
		
		
		c.textR = new Text(600, 100, "");
		c.textR.xProperty().bind(c.root.layoutXProperty().add(c.root.prefWidthProperty()).subtract(c.root.prefWidthProperty().divide(4.5)));
		c.textR.yProperty().bind(c.root.prefHeightProperty().divide(6));
		c.textR.setFont(Font.font(20));
		c.textR.setStroke(Color.YELLOW);
		
		c.textC.setFont(Font.font(null, FontWeight.BOLD, 15));
		c.textC.setTextFill(Color.WHITE);
		
		Stream.of(c.textL, c.textR).forEach(text ->
				text.textProperty().addListener(((observable, oldValue, newValue) -> {
					if (!newValue.contains(mode.get()) && !newValue.equals("")) {
						text.setText(mode.getValue() + " = " + newValue);
					}
				})));
		
		uiPane.getChildren().add(inputGridPane);
		contextRoot.getChildren().addAll(c.textL, c.textR);
		
		
		
		openButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			configureImage(file, background, imgRoot);
		});
		
		clearButton.setOnAction(e -> {
			
			c.root = configureContextRoot(clearButton);
			c.root.getChildren().addAll(c.textL, c.textR);
			c.points.clear();
			clearButton.setDisable(true);
			c.pointsDrawn.set(false);
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
		
		saveButton.setOnAction(e -> {
			
			Props.set(nameField.getText() + "SPC" + lastNameField.getText() + ";" + mode.get() + "L",
					c.textL.getText().replaceAll("[^.0123456789]",""));
			Props.set(nameField.getText() + "SPC" + lastNameField.getText() + ";" + mode.get() + "R",
					c.textR.getText().replaceAll("[^.0123456789]",""));
			update();
		});
		saveButton.disableProperty().bind(c.pointsDrawn.not());
		
		statButton.setOnAction(e -> {
			
			c.table = TableConfigurer.configureTable();
			c.rootRoot.setCenter(c.table);
		});
		
		File initialFile = new File(System.getProperty("user.home") + "/Desktop/5230e6e4807b8f13fce73b2b7d542f.jpg");
		
		primaryStage.show();
		configureImage(initialFile, background, imgRoot);
		ai.fire();
		update();
	}
	
	private void update() {
		
		System.out.println(c.pointsRequired);
		System.out.println(c.currentPoint);
		if (c.xray == null) {
			c.textC.setText("Upload an X-Ray");
		} else if (!c.pointsDrawn.get()) {
			c.textC.setText("Отметьте " + c.currentPoint.replace("rp", "правую точку ").replace("lp", "левую точку "));
		} else {
			c.textC.setText("");
		}
		c.rootRoot.getChildren().clear();
		c.rootRoot.setCenter(c.background);
		c.rootRoot.setRight(c.uiPane);
		c.rootRoot.setTop(c.buttonsPane);
		c.background.getChildren().clear();
		c.background.getChildren().addAll(new Pane(c.imgRoot, c.root));


		System.out.println(c.root.getChildren().size());
		//c.root.getChildren().forEach(Node::toFront);
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