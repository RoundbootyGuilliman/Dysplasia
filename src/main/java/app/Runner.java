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
				c.ctx.setPrefHeight(newValue.getHeight());
				c.ctx.setPrefWidth(newValue.getWidth());
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
	
	private VBox configureUiPane() {
		
		
		FileChooser fileChooser = new FileChooser();
		configureFileChooser(fileChooser);
		
		Button openButton = new Button("  Загрузить снимок...");
		Button clearButton = new Button("  Очистить");
		c.clearButton = clearButton;
		Button saveButton = new Button("  Сохранить");
		Button statButton = new Button("  Статистика");
		
		Stream.of(openButton, clearButton, saveButton, statButton).forEach(button -> {
			button.setShape(new Rectangle(50, 40, Color.DARKBLUE));
			button.setPrefWidth(200);
			button.setPrefHeight(40);
			
			button.setTextFill(Color.WHITE);
			
			button.setBackground(back);
			button.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
				if (!button.getText().equals("  Статистика") || !statOn.get()) {
					button.setBackground(over);
					button.getScene().setCursor(Cursor.HAND);
				}
			});
			button.addEventFilter(MouseEvent.MOUSE_EXITED, event -> {
				if (!button.getText().equals("  Статистика") || !statOn.get()) {
					button.setBackground(back);
					button.getScene().setCursor(Cursor.DEFAULT);
				}
				
			});
			button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
				if (!button.getText().equals("  Статистика") || !statOn.get()) {
					button.setBackground(pressed);
				}
			});
			
			if (!button.getText().equals("  Статистика")) {
				button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> button.setBackground(back));
			}
			button.setFont(Font.font(16));
			button.setAlignment(Pos.CENTER_LEFT);
		});
		
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
		
		Arrays.asList(nameField, lastNameField)
				.forEach(textField ->
						textField.textProperty().addListener((observable, oldValue, newValue) -> {
							if (!newValue.matches("\\p{L}")) {
								textField.setText(newValue.replaceAll("[^\\p{L}]", ""));
							}
						}));
		
		TextField ageField = new TextField();
		ageField.setBackground(textField);
		ageField.setPromptText("Возраст...");
		ageField.setStyle("-fx-text-fill: white; -fx-prompt-text-fill: #6d7883");
		ageField.setMaxWidth(85);
		ageField.setMinWidth(85);
		
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
		
		TilePane fieldsPane = new TilePane();
		fieldsPane.setOrientation(Orientation.VERTICAL);
		fieldsPane.setPrefRows(3);
		fieldsPane.setAlignment(Pos.CENTER);
		fieldsPane.setMaxWidth(200);
		fieldsPane.setMinWidth(200);
		fieldsPane.setVgap(10);
		fieldsPane.getChildren().addAll(nameField, lastNameField, box);
		
		hasText.bind(nameField.textProperty().isNotEmpty()
				.and(lastNameField.textProperty().isNotEmpty())
				.and(ageField.textProperty().isNotEmpty()));
		
		
		openButton.setOnAction(e -> {
			File file = fileChooser.showOpenDialog(c.primaryStage);
			clear();
			configureImage(file, c.background);
		});
		
		clearButton.setOnAction(e -> clear());
		
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
				c.root.setCenter(c.table);
				statOn.set(true);
			} else {
				statOn.set(false);
				update();
			}
			
		});
		
		
		VBox uiPane = new VBox();
		uiPane.setMaxWidth(200);
		uiPane.setMinWidth(200);
		uiPane.setBackground(back);
		uiPane.getChildren().addAll(inputGridPane, fieldsPane);
		
		return uiPane;
	}
	
	private TilePane configureButtonsPane() {
		List<Button> buttons = Stream.of("АИ", "ОАШВ", "ЦУ", "АУ", "ИМР", "ИК", "ШДУ").map(Button::new).collect(Collectors.toList());
		
		Button ai = buttons.get(0);
		
		buttons.forEach(button -> {
			button.setShape(new Rectangle(50, 40, Color.DARKBLUE));
			button.setPrefWidth(100);
			button.setPrefHeight(40);
			
			button.setFont(Font.font(20));
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
			
			button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> button.setBackground(pressed));
			
			button.setTooltip(new Tooltip(button.getText()
					.replace("АИ", "Ацетабулярный индекс")
					.replace("ОАШВ", "Отношение ацетабулярной ширины к высоте")
					.replace("ЦУ", "Центральный угол")
					.replace("АУ", "Ацетабулярный угол")
					.replace("ИМР", "Индекс миграции Реймера")
					.replace("ИК", "Индекс конгруэнтности")
					.replace("ШДУ", "Шеечно-диафизарный угол")));
			
			button.setOnAction(event -> {
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
				clear();
				update();
			});
		});
		
		
		TilePane inputTilePane = new TilePane();
		inputTilePane.setOrientation(Orientation.HORIZONTAL);
		inputTilePane.setPrefColumns(7);
		inputTilePane.setMaxHeight(40);
		inputTilePane.setMinHeight(40);
		inputTilePane.getChildren().addAll(buttons);
		inputTilePane.setBackground(back);
		
		return inputTilePane;
	}
	
	private BorderPane configureContextRoot() {
		
		BorderPane contextRoot = new BorderPane();
		
		contextRoot.setOnMouseReleased(event -> {
			
			if (c.xray != null) {
				for (int i = 0; i < c.pointsRequired.size(); i++) {
					
					String req = c.pointsRequired.get(i);
					
					if (c.points.get(req) == null) {
						c.clearButton.setDisable(false);
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
		
		c.primaryStage = primaryStage;
		
		Screen screen = Screen.getPrimary();
		
		BorderPane root = new BorderPane();
		c.root = root;
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getClassLoader().getResource("style.css").toExternalForm());
		
		primaryStage.setTitle("Runner nodes");
		primaryStage.setMinHeight(687);
		primaryStage.setMinWidth(916);
		primaryStage.setMaxHeight(screen.getVisualBounds().getMaxY());
		primaryStage.setMaxWidth(screen.getVisualBounds().getMaxX());
		primaryStage.setScene(scene);

//		Stream.of(primaryStage.widthProperty(), primaryStage.heightProperty()).forEach(readOnlyDoubleProperty ->
//				readOnlyDoubleProperty.addListener((observable, oldValue, newValue) -> {
//					System.out.println("CTX " + c.ctx.getWidth());
//					System.out.println("IMG " + c.xray.getLayoutBounds().getWidth());
//		}));
		
		// ui pane
		
		VBox uiPane = configureUiPane();
		c.uiPane = uiPane;
		
		// buttons pane
		
		TilePane buttonsPane = configureButtonsPane();
		c.buttonsPane = buttonsPane;
		
		// hint pane
		
		Text textC = new Text(300, 100, "");
		textC.setFont(Font.font(20));
		textC.setFill(Color.WHITE);
		c.textC = textC;
		
		
		Text textL = new Text(90, 100, "");
		textL.setFont(Font.font(20));
		textL.setFill(Color.YELLOW);
		c.textL = textL;
		
		
		Text textR = new Text(600, 100, "");
		textR.setFont(Font.font(20));
		textR.setFill(Color.YELLOW);
		c.textR = textR;
		
		Stream.of(textL, textR).forEach(text ->
				text.textProperty().addListener(((observable, oldValue, newValue) -> {
					if (!changed.get()) changed.set(true);
					if (!newValue.contains(mode.get()) && !newValue.equals("")) {
						text.setText(mode.getValue() + " = " + newValue);
					}
				})));
		
		BorderPane hintPane = new BorderPane();
		hintPane.setMaxHeight(30);
		hintPane.setMinHeight(30);
		hintPane.setBackground(back);
		hintPane.setStyle("-fx-border-width: 0 2 0 2; -fx-border-color: black;");
		hintPane.setPadding(new Insets(0, 40, 0, 40));
		hintPane.setCenter(textC);
		hintPane.setLeft(textL);
		hintPane.setRight(textR);
		c.hintPane = hintPane;
		
		
		// context root
		
		BorderPane contextRoot = configureContextRoot();
		c.ctx = contextRoot;
		
		c.centerRoot = new Pane(contextRoot);
		
		
		// background
		
		HBox background = new HBox();
		
		background.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
		background.prefWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.prefHeightProperty().bind(scene.heightProperty().subtract(buttonsPane.getMaxHeight()).subtract(hintPane.getMaxHeight()));
		background.maxWidthProperty().bind(scene.widthProperty().subtract(uiPane.getMaxWidth()));
		background.maxHeightProperty().bind(scene.heightProperty().subtract(buttonsPane.getMaxHeight()).subtract(hintPane.getMaxHeight()));
		
		background.setAlignment(Pos.CENTER);
		background.setFillHeight(false);
		background.getChildren().addAll(c.centerRoot);
		
		c.background = background;
		
		
		// another root
		
		BorderPane anotherRoot = new BorderPane();
		anotherRoot.setTop(buttonsPane);
		anotherRoot.setCenter(background);
		anotherRoot.setBottom(hintPane);
		c.anotherRoot = anotherRoot;
		
		// root
		
		root.setLeft(uiPane);
		root.setCenter(anotherRoot);


//		File initialFile = new File(System.getProperty("user.home") + "/Desktop/5230e6e4807b8f13fce73b2b7d542f.jpg");
//		configureImage(initialFile, background);
		
		primaryStage.show();
		
		((Button) buttonsPane.getChildren().get(0)).fire();
		update();
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
		
		
		if (c.xray != null) {
			c.ctx.setPrefHeight(c.xray.getLayoutBounds().getHeight());
			c.ctx.setPrefWidth(c.xray.getLayoutBounds().getWidth());
			c.centerRoot.getChildren().clear();
			c.centerRoot.getChildren().addAll(c.xray, c.ctx);
			c.background.getChildren().clear();
			c.background.getChildren().addAll(c.centerRoot);
			c.anotherRoot.setTop(c.buttonsPane);
			c.anotherRoot.setBottom(c.hintPane);
			c.anotherRoot.setCenter(c.background);
			
		}
		c.root.getChildren().clear();
		c.root.setCenter(c.anotherRoot);
		c.root.setLeft(c.uiPane);
		
		c.frontNodes.forEach(Node::toFront);
		c.innerPoints.forEach(Node::toFront);
		c.points.forEach((s, circle) -> {
			if (circle != null) {
				circle.toFront();
			}
		});
	}
	
	private void clear() {
		c.clearButton.setDisable(true);
		c.ctx = configureContextRoot();
		c.points.clear();
		
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
					c.ctx.getChildren().add((Node) field.get(s));
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
		
		c.ctx.getChildren().add(innerPoint);
		c.innerPoints.add(innerPoint);
		
		
		point.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> point.getScene().setCursor(Cursor.HAND));
		
		point.addEventFilter(MouseEvent.MOUSE_EXITED, event -> point.getScene().setCursor(Cursor.DEFAULT));
		
		//point.addEventFilter(MouseEvent.ANY, Event::consume);
	}
}