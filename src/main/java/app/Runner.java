package app;

import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
	private StringProperty mode = new SimpleStringProperty(this, "mode", "АИ");
	private SimpleBooleanProperty changed = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty hasText = new SimpleBooleanProperty(false);
	private SimpleBooleanProperty statOn = new SimpleBooleanProperty(false);
	
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
		
		List<Button> buttons = Stream.of("АИ", "ОАШВ", "ЦУ", "АУ", "ИМР", "ИК", "ШДУ").map(Button::new).collect(Collectors.toList());
		
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
				case "АИ":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp3", "rp2", "rp3"));
					break;
				
				case "ОАШВ":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp3", "lp4", "rp2", "rp3", "rp4"));
					break;
				
				case "ЦУ":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "rp1", "rp2"));
					break;
				
				case "АУ":
					c.pointsRequired.addAll(Arrays.asList("lp2", "lp4", "rp2", "rp4"));
					break;
				
				case "ИМР":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "rp1", "rp2"));
					break;
				
				case "ИК":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp2", "lp4", "rp1", "rp2", "rp4"));
					break;
				
				case "ШДУ":
					c.pointsRequired.addAll(Arrays.asList("lp1", "lp5", "lp6", "rp1", "rp5", "rp6"));
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
		nameField.setMaxWidth(180);
		nameField.setMinWidth(180);
		
		TextField lastNameField = new TextField();
		lastNameField.setBackground(textField);
		lastNameField.setPromptText("Фамилия пациента...");
		lastNameField.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #6d7883");
		
		
		TextField ageField = new TextField();
		ageField.setBackground(textField);
		ageField.setPromptText("Возраст...");
		ageField.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #6d7883");
		ageField.setMaxWidth(85);
		ageField.setMinWidth(85);
		
		Arrays.asList(nameField, lastNameField)
				.forEach(textField ->
						textField.textProperty().addListener((observable, oldValue, newValue) -> {
							if (!newValue.matches("\\p{L}")) {
								textField.setText(newValue.replaceAll("[^\\p{L}]", ""));
							}
						}));
		ageField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d^.")) {
				ageField.setText(newValue.replaceAll("[^\\d^.]", ""));
			}
		});
		ChoiceBox<String> gender = new ChoiceBox<>(FXCollections.observableArrayList("Муж.", "Жен."));
		gender.setValue("Муж.");
		gender.setBackground(textField);
		gender.setMaxWidth(85);
		gender.setMinWidth(85);
		
		GridPane box = new GridPane();
		GridPane.setConstraints(ageField, 0, 0);
		GridPane.setConstraints(gender, 1, 0);
		box.setMaxWidth(180);
		box.setHgap(10);
		box.getChildren().addAll(ageField, gender);
		
		List<Node> fields = Arrays.asList(nameField, lastNameField, box);
		TilePane fieldsPane = new TilePane();
		fieldsPane.setOrientation(Orientation.VERTICAL);
		fieldsPane.setPrefRows(3);
		fieldsPane.setAlignment(Pos.CENTER);
		fieldsPane.setMaxWidth(200);
		fieldsPane.setMinWidth(200);
		fieldsPane.setVgap(10);
		fieldsPane.getChildren().addAll(fields);
		
		Pane boilerplate = new Pane();
		boilerplate.setMinHeight(40);
		boilerplate.setMaxHeight(40);
		
		List<Node> nodes = Arrays.asList(boilerplate, openButton, clearButton, saveButton, statButton, new Label(""), new Label());
		GridPane inputGridPane = new GridPane();
		
		int index = 0;
		for (Node node : nodes) {
			GridPane.setConstraints(node, 0, index);
			index++;
		}
		inputGridPane.getChildren().addAll(nodes);
		
		TilePane inputTilePane = new TilePane();
		inputTilePane.setOrientation(Orientation.HORIZONTAL);
		inputTilePane.setPrefColumns(7);
		inputTilePane.setMaxHeight(40);
		inputTilePane.setMinHeight(40);
		
		statButton.setShape(new Rectangle(50, 40, Color.DARKBLUE));
		statButton.setPrefWidth(200);
		statButton.setPrefHeight(40);
		
		statButton.setTextFill(Color.WHITE);
		
		statButton.setBackground(back);
		statButton.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
			if (!statOn.get()) {
				statButton.setBackground(over);
				statButton.getScene().setCursor(Cursor.HAND);
			}
		});
		statButton.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
			if (!statOn.get()) {
				statButton.setBackground(back);
				statButton.getScene().setCursor(Cursor.DEFAULT);
			}
			
		});
		statButton.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			if (!statOn.get()) {
				statButton.setBackground(pressed);
			}
		});
		
		statButton.setFont(Font.font(16));
		statButton.setAlignment(Pos.CENTER_LEFT);
		
		List<Button> tempButtons = new ArrayList<>(buttons);
		tempButtons.addAll(Arrays.asList(openButton, clearButton, saveButton));
		
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
		
		Stream.of(openButton, clearButton, saveButton).forEach(button -> {
			button.setFont(Font.font(16));
			button.setAlignment(Pos.CENTER_LEFT);
			button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> button.setBackground(back));
		});
		
		buttons.forEach(button -> {
			button.setTooltip(new Tooltip(button.getText()
					.replace("АИ", "Ацетабулярный индекс")
					.replace("ОАШВ", "Отношение ацетабулярной ширины к высоте")
					.replace("ЦУ", "Центральный угол")
					.replace("АУ", "Ацетабулярный угол")
					.replace("ИМР", "Индекс миграции Реймера")
					.replace("ИК", "Индекс конгруэнтности")
					.replace("ШДУ", "Шеечно-диафизарный угол")));
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
		scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
		
		primaryStage.setTitle("Runner nodes");
		primaryStage.setMinHeight(687);
		primaryStage.setMinWidth(916);
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
		
		BorderPane hintPane = new BorderPane();
		hintPane.setMaxHeight(30);
		hintPane.setMinHeight(30);
		hintPane.setBackground(back);
		hintPane.setStyle("-fx-border-width: 0 2 0 2; -fx-border-color: black;");
		hintPane.setPadding(new Insets(0, 40, 0, 40));
		c.hintPane = hintPane;
		
		HBox background = new HBox();
		c.background = background;
		background.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		
		background.prefWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.prefHeightProperty().bind(scene.heightProperty().subtract(inputTilePane.getMaxHeight()).subtract(hintPane.getMaxHeight()));
		background.maxWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.maxHeightProperty().bind(scene.heightProperty().subtract(inputTilePane.getMaxHeight()).subtract(hintPane.getMaxHeight()));
		
		background.setAlignment(Pos.CENTER);
		background.setFillHeight(false);
		
		
		
		BorderPane anotherRoot = new BorderPane();
		c.anotherRoot = anotherRoot;
		
		
		root.setLeft(uiPane);
		root.setCenter(anotherRoot);
		anotherRoot.setCenter(background);
		anotherRoot.setTop(inputTilePane);
		anotherRoot.setBottom(hintPane);
		
		
		BorderPane contextRoot = configureContextRoot(clearButton);
		c.root = contextRoot;
		c.xray = new ImageView();
		c.centerRoot = new Pane(c.xray, contextRoot);
		c.background.getChildren().addAll(c.centerRoot);
		
		c.textC = new Text(300, 100, "");
		c.textC.setFont(Font.font(20));
		c.textC.setFill(Color.WHITE);
		
		
		c.textL = new Text(90, 100, "");
		c.textL.setFont(Font.font(20));
		c.textL.setFill(Color.YELLOW);
		
		
		c.textR = new Text(600, 100, "");
		c.textR.setFont(Font.font(20));
		c.textR.setFill(Color.YELLOW);
		
		
		Stream.of(c.textL, c.textR).forEach(text ->
				text.textProperty().addListener(((observable, oldValue, newValue) -> {
					if (!changed.get()) changed.set(true);
					if (!newValue.contains(mode.get()) && !newValue.equals("")) {
						text.setText(mode.getValue() + " = " + newValue);
					}
				})));
		
		uiPane.getChildren().addAll(inputGridPane, fieldsPane);
		
		openButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			configureImage(file, background);
		});
		
		clearButton.setOnAction(e -> {
			
			c.root = configureContextRoot(clearButton);
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
		
		hasText.bind(nameField.textProperty().isNotEmpty()
				.and(lastNameField.textProperty().isNotEmpty())
				.and(ageField.textProperty().isNotEmpty()));
		
		saveButton.setOnAction(e -> {
			changed.set(false);
			String key = nameField.getText() + "SPC" + lastNameField.getText() + ";" + ageField.getText() + ";"
					+ gender.getValue().replace("Муж.", "M").replace("Жен.", "F")
					+ ";" + mode.get()
					.replace("АИ", "AI")
					.replace("ОАШВ", "ADR")
					.replace("ЦУ", "CEA")
					.replace("АУ", "AA")
					.replace("ИМР", "RI")
					.replace("ИК", "CI")
					.replace("ШДУ", "SDU");
			
			Props.set(key + "L", c.textL.getText().replaceAll("[^.0123456789]", ""));
			Props.set(key + "R", c.textR.getText().replaceAll("[^.0123456789]", ""));
			update();
		});
		saveButton.disableProperty().bind((c.pointsDrawn.not()).or(changed.not()).or(hasText.not()));
		
		
		statButton.setOnAction(e -> {
			if (!statOn.get()) {
				c.table = TableConfigurer.configureTable();
				c.rootRoot.setCenter(c.table);
				statOn.set(true);
			} else {
				statOn.set(false);
				update();
			}
			
		});
		
		File initialFile = new File(System.getProperty("user.home") + "/Desktop/5230e6e4807b8f13fce73b2b7d542f.jpg");
		
		primaryStage.show();
		configureImage(initialFile, background);
		ai.fire();
		update();
		System.out.println(primaryStage.getWidth());
	}
	
	private void update() {
		
		if (c.xray == null) {
			c.textC.setText("Upload an X-Ray");
		} else if (!c.pointsDrawn.get()) {
			c.textC.setText("Отметьте " + c.currentPoint
					.replace("lp1", "середину головки левого сустава")
					.replace("rp1", "середину головки правого сустава")
					.replace("lp2", "край левой вертлужной впадины")
					.replace("rp2", "край правой вертлужной впадины")
					.replace("lp3", "левую точку Хильгенрейнера")
					.replace("rp3", "правую точку Хильгенрейнера")
					.replace("lp4", "нижний край левой слезы Келера")
					.replace("rp4", "нижний край правой слезы Келера")
					.replace("lp5", "пересечение левой головки и бедренной кости")
					.replace("rp5", "пересечение правой головки и бедренной кости")
					.replace("lp6", "нижний край левой бедренной кости")
					.replace("rp6", "нижний край правой бедренной кости"));
			
		} else {
			c.textC.setText("");
		}
		
		c.hintPane.setCenter(c.textC);
		c.hintPane.setLeft(c.textL);
		c.hintPane.setRight(c.textR);
		c.rootRoot.getChildren().clear();
		c.rootRoot.setCenter(c.anotherRoot);
		c.rootRoot.setLeft(c.uiPane);
		c.anotherRoot.setCenter(c.background);
		c.anotherRoot.setTop(c.buttonsPane);
		c.background.getChildren().clear();
		c.centerRoot = new Pane(c.xray, c.root);
		c.background.getChildren().addAll(c.centerRoot);
		c.root.setPrefHeight(c.xray.getLayoutBounds().getHeight());
		c.root.setPrefWidth(c.xray.getLayoutBounds().getWidth());
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
			
			case "ОАШВ":
				calculateAdr();
				
				break;
			
			case "АИ":
				calculateAi();
				
				break;
			
			case "ЦУ":
				calculateCea();
				
				break;
			
			case "АУ":
				calculateAa();
				
				break;
			
			case "ИМР":
				calculateRi();
				
				break;
			
			case "ИК":
				calculateCi();
				
				break;
				
			case "ШДУ":
				calculateSdu();
				
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
		
		Circle innerPoint = new Circle(point.getCenterX(), point.getCenterY(), 4);
		
		point.setFill(Color.TRANSPARENT);
		innerPoint.setFill(Color.WHITE);
		innerPoint.setStroke(Color.ORANGERED);
		innerPoint.setStrokeWidth(2);
		innerPoint.centerXProperty().bind(point.centerXProperty());
		innerPoint.centerYProperty().bind(point.centerYProperty());
		
		c.root.getChildren().add(innerPoint);
		c.innerPoints.add(innerPoint);
		
		
		point.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> point.getScene().setCursor(Cursor.HAND));
		
		point.addEventFilter(MouseEvent.MOUSE_EXITED, event -> point.getScene().setCursor(Cursor.DEFAULT));
		
		//point.addEventFilter(MouseEvent.ANY, Event::consume);
	}
}