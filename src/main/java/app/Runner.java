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
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static app.Actions.*;
import static app.Utils.Context;
import static app.Utils.Shapes;

public class Runner extends Application {
	
	final static Context c = new Context();
	final static Shapes s = new Shapes();
	private Background back = new Background(new BackgroundFill(Color.rgb(23, 33, 43), null, null));
	private Background over = new Background(new BackgroundFill(Color.rgb(35, 46, 60), null, null));
	private Background pressed = new Background(new BackgroundFill(Color.rgb(43, 82, 120), null, null));
	private Background textField = new Background(new BackgroundFill(Color.rgb(36, 47, 61), null, null));
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
	
	private void configureImage(File file, HBox background) {
		if (file != null) {
			Image img = new Image(file.toURI().toString());
			ImageView image = new ImageView(img);
			image.setPreserveRatio(true);
			image.fitHeightProperty().bind(background.prefHeightProperty());
			image.fitWidthProperty().bind(background.prefWidthProperty());
			c.root.setPrefHeight(image.getLayoutBounds().getHeight());
			c.root.setPrefWidth(image.getLayoutBounds().getWidth());
			c.xray = image;
			image.layoutBoundsProperty().addListener((obs, oldValue, newValue) -> {
				c.root.setPrefHeight(newValue.getHeight());
				c.root.setPrefWidth(newValue.getWidth());
				c.points.forEach((key, value) -> {
					double widthRatio = oldValue.getWidth() / value.getCenterX();
					double widthAddition = ((newValue.getWidth() - oldValue.getWidth()) / widthRatio);
					double heightRatio = oldValue.getHeight() / value.getCenterY();
					double heightAddition = ((newValue.getHeight() - oldValue.getHeight()) / heightRatio);
					value.setCenterX(value.getCenterX() + widthAddition);
					value.setCenterY(value.getCenterY() + heightAddition);
				});
			});
		}
		update();
	}
	
	private BorderPane configureContextRoot(Button clearButton) {
		
		BorderPane contextRoot = new BorderPane();
		
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
		
		Button openButton = new Button("  Загрузить снимок...");
		Button clearButton = new Button("  Очистить");
		Button saveButton = new Button("  Сохранить");
		Button statButton = new Button("  Статистика");
		
		List<Button> buttons = Stream.of("AI", "ADR", "CEA", "AA", "RI", "CI").map(Button::new).collect(Collectors.toList());
		
		Button ai = buttons.get(0);
		buttons.forEach(button -> button.setOnAction(event -> {
			buttons.forEach(innerButton -> {
				innerButton.setDisable(false);
				innerButton.setStyle("");
			});
			button.setDisable(true);
			button.setStyle("-fx-opacity: 1.0; -fx-background-color: #2b5278;");
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
			clearButton.setDisable(false);
			clearButton.fire();
			update();
		}));
		
		
		TextField nameField = new TextField();
		nameField.setBackground(textField);
		nameField.setPromptText("Имя пациента...");
		nameField.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #6d7883");
		
		
		TextField lastNameField = new TextField();
		lastNameField.setBackground(textField);
		lastNameField.setPromptText("Фамилия пациента...");
		lastNameField.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #6d7883");
		
		
		List<Node> nodes = Arrays.asList(openButton, clearButton, saveButton, statButton, new Label(""), nameField, new Label(""), lastNameField);
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
		
		List<Button> tempButtons = new ArrayList<>(buttons);
		tempButtons.addAll(Arrays.asList(openButton, clearButton, saveButton, statButton));
		
		tempButtons.forEach(button -> {
			button.setShape(new Rectangle(50, 40, Color.DARKBLUE));
			button.setPrefWidth(200);
			button.setPrefHeight(40);
			
			button.setTextFill(Color.WHITE);
			
			button.setBackground(back);
			button.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
				button.setBackground(over);
				button.getScene().setCursor(Cursor.HAND);
			});
			button.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
				button.setBackground(back);
				button.getScene().setCursor(Cursor.DEFAULT);
			});
			button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				button.setBackground(pressed);
			});
		});
		
		Stream.of(openButton, clearButton, saveButton, statButton).forEach(button -> {
			button.setFont(Font.font(16));
			button.setAlignment(Pos.CENTER_LEFT);
			button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> button.setBackground(back));
		});
		
		buttons.forEach(button -> {
			button.setPrefWidth(100);
			button.setFont(Font.font(20));
			button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> button.setBackground(pressed));
		});
		
		inputTilePane.getChildren().addAll(buttons);
		
		
		Screen screen = Screen.getPrimary();
		c.primaryStage = primaryStage;
		BorderPane root = new BorderPane();
		c.rootRoot = root;
		
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Runner nodes");
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.setMaxHeight(screen.getVisualBounds().getMaxY());
		primaryStage.setMaxWidth(screen.getVisualBounds().getMaxX());
		primaryStage.setScene(scene);
		
		
		VBox uiPane = new VBox();
		uiPane.setMaxWidth(200);
		uiPane.setMinWidth(200);
		
		
		c.uiPane = uiPane;
		
		
		c.buttonsPane = inputTilePane;
		
		
		uiPane.setBackground(back);
		inputTilePane.setBackground(back);
		
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
		
		
		BorderPane contextRoot = configureContextRoot(clearButton);
		c.root = contextRoot;
		c.xray = new ImageView();
		c.centerRoot = new Pane(c.xray, contextRoot);
		c.background.getChildren().addAll(c.centerRoot);
		
		
		background.setOnMouseEntered(event -> System.out.println("ENTERED BACKGROUND"));
		contextRoot.setOnMouseEntered(event -> System.out.println("ENTERED contextRoot"));
		
		c.textC = new Text(300, 100, "");
		c.textC.xProperty().bind(c.root.layoutXProperty().add(c.root.prefWidthProperty()).subtract(c.root.prefWidthProperty().divide(4.5)).divide(2));
		c.textC.yProperty().bind(c.root.prefHeightProperty().divide(6));
		c.textC.setFont(Font.font(20));
		c.textC.setStroke(Color.YELLOW);
		
		
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
		
		
		Stream.of(c.textL, c.textR).forEach(text ->
				text.textProperty().addListener(((observable, oldValue, newValue) -> {
					if (!newValue.contains(mode.get()) && !newValue.equals("")) {
						text.setText(mode.getValue() + " = " + newValue);
					}
				})));
		
		uiPane.getChildren().add(inputGridPane);
		contextRoot.getChildren().addAll(c.textL, c.textR, c.textC);
		
		
		openButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			configureImage(file, background);
		});
		
		clearButton.setOnAction(e -> {
			
			c.root = configureContextRoot(clearButton);
			c.root.getChildren().addAll(c.textL, c.textR, c.textC);
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
					c.textL.getText().replaceAll("[^.0123456789]", ""));
			Props.set(nameField.getText() + "SPC" + lastNameField.getText() + ";" + mode.get() + "R",
					c.textR.getText().replaceAll("[^.0123456789]", ""));
			update();
		});
		saveButton.disableProperty().bind(c.pointsDrawn.not());
		
		statButton.setOnAction(e -> {
			
			c.table = TableConfigurer.configureTable();
			c.rootRoot.setCenter(c.table);
		});
		
		File initialFile = new File(System.getProperty("user.home") + "/Desktop/5230e6e4807b8f13fce73b2b7d542f.jpg");
		
		primaryStage.show();
		configureImage(initialFile, background);
		ai.fire();
		update();
	}
	
	private void update() {
		
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
		c.centerRoot = new Pane(c.xray, c.root);
		c.background.getChildren().addAll(c.centerRoot);
		
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