package deprecated;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Iterator;

public class Connectable extends Application {
	
	private final BooleanProperty deleteMode = new SimpleBooleanProperty(this, "deleteMode", false);
	private final BooleanProperty percentageMode = new SimpleBooleanProperty(this, "percentageMode", false);
	
	private final Context context = new Context();
	
	public static void main(String args[]) {
		launch(args);
	}
	
	private static void configureFileChooser(final FileChooser fileChooser) {
		fileChooser.setTitle("View Pictures");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("All Images", "*.*"),
				new FileChooser.ExtensionFilter("JPG", "*.jpg"),
				new FileChooser.ExtensionFilter("PNG", "*.png")
		);
	}
	
	@Override
	public void start(Stage primaryStage) {
		
		context.root = new Pane();
		
		final Scene scene = new Scene(context.root, 600, 300);
		
		primaryStage.setTitle("deprecated.Connectable nodes");
		primaryStage.setMinHeight(768);
		primaryStage.setMinWidth(1024);
		primaryStage.setScene(scene);
		
		final FileChooser fileChooser = new FileChooser();
		
		final Button openButton = new Button("Open a Picture...");
		
		final CheckBox deleteModeCheckbox = new CheckBox("Delete mode");
		
		final GridPane inputGridPane = new GridPane();
		
		GridPane.setConstraints(openButton, 0, 0);
		GridPane.setConstraints(deleteModeCheckbox, 0, 1);
		inputGridPane.setHgap(6);
		inputGridPane.setVgap(6);
		inputGridPane.getChildren().addAll(openButton, deleteModeCheckbox);
		
		deleteMode.bind(deleteModeCheckbox.selectedProperty());
		
		context.root.getChildren().add(inputGridPane);
		
		context.text = new Text(100, 100, "TEXT");
		context.text.setFont(Font.font(20));
		context.text.setStroke(Color.RED);
		context.root.getChildren().add(context.text);
		
		openButton.setOnAction(e -> {
			configureFileChooser(fileChooser);
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				Image image1 = new Image(file.toURI().toString());
				context.xray = new ImageView(image1);
//				BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
//				BackgroundImage backgroundImage = new BackgroundImage(image1,
// BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
//				stac.setBackground(new Background(backgroundImage));
				context.xray.setPreserveRatio(true);
				context.xray.fitHeightProperty().bind(primaryStage.heightProperty());
				context.xray.fitWidthProperty().bind(primaryStage.widthProperty());
				context.root.getChildren().add(context.xray);
			}
		});
		
		scene.setOnMouseReleased(event -> {
			
			if (!deleteMode.get()) {
				if (context.secondPoint == null) {
					
					
					Group newPoint = makeDraggable(new Circle(event.getX(), event.getY(), 8));
					
					context.root.getChildren().add(newPoint);
					
					if (context.selectedPoint == null) {
						context.selectedPoint = newPoint;
					} else {
						Circle point1 = (Circle) context.selectedPoint.getChildren().get(0);
						Circle point2 = (Circle) newPoint.getChildren().get(0);
						int k = 100;
						
						
						double coord1 = ((1 + k) * point1.getCenterX() - k * point2.getCenterX());
						double coord2 = ((1 + k) * point1.getCenterY() - k * point2.getCenterY());
						
						double coord3 = ((1 - k) * point1.getCenterX() + k * point2.getCenterX());
						double coord4 = ((1 - k) * point1.getCenterY() + k * point2.getCenterY());
						
						Line line = new Line(coord1, coord2, coord3, coord4);
						
						Circle center = new Circle(
								(1 - 0.5) * point1.getCenterX() + 0.5 * point2.getCenterX(),
								(1 - 0.5) * point1.getCenterY() + 0.5 * point2.getCenterY(),
								5);
						
						double x = calculateX(center, point1, point2);
						double y = calculateY(center, point1, point2);
						
						Line perp = new Line(center.getCenterX(), center.getCenterY(), x, y);
						
						context.selectedPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							
							line.setStartX((1 + k) * point1.getCenterX() - k * point2.getCenterX());
							line.setStartY((1 + k) * point1.getCenterY() - k * point2.getCenterY());
							line.setEndX((1 - k) * point1.getCenterX() + k * point2.getCenterX());
							line.setEndY((1 - k) * point1.getCenterY() + k * point2.getCenterY());
							center.setCenterX((1 - 0.5) * point1.getCenterX() + 0.5 * point2.getCenterX());
							center.setCenterY((1 - 0.5) * point1.getCenterY() + 0.5 * point2.getCenterY());
							
							perp.setStartX(center.getCenterX());
							perp.setStartY(center.getCenterY());
							perp.setEndX(calculateX(center, point1, point2));
							perp.setEndY(calculateY(center, point1, point2));
						});
						newPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							
							line.setStartX((1 + k) * point1.getCenterX() - k * point2.getCenterX());
							line.setStartY((1 + k) * point1.getCenterY() - k * point2.getCenterY());
							line.setEndX((1 - k) * point1.getCenterX() + k * point2.getCenterX());
							line.setEndY((1 - k) * point1.getCenterY() + k * point2.getCenterY());
						});
						context.root.getChildren().add(line);
						
						
						
						context.root.getChildren().add(perp);
						if (context.l1 == null) {
							context.l1 = line;
						} else {
							context.l2 = line;
						}
						point1.setFill(Color.RED);
						point2.setFill(Color.RED);
						line.setStroke(Color.RED);
						line.toBack();
						if (context.xray != null) context.xray.toBack();
						newPoint.toFront();
						context.selectedPoint.toFront();
						context.selectedPoint = null;
						
					}
				} else {
					Circle point1 = (Circle) context.selectedPoint.getChildren().get(0);
					Circle point2 = (Circle) context.secondPoint.getChildren().get(0);
					
					Line line = new Line(point1.getCenterX(), point1.getCenterY(), point2.getCenterX(), point2.getCenterY());
					
					context.selectedPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
						
						line.setStartX(point1.getCenterX());
						line.setStartY(point1.getCenterY());
					});
					context.secondPoint.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
						
						line.setEndX(point2.getCenterX());
						line.setEndY(point2.getCenterY());
					});
					context.root.getChildren().add(line);
					point1.setFill(Color.DARKGREEN);
					point2.setFill(Color.DARKGREEN);
					line.setStroke(Color.DARKGREEN);
					line.toBack();
					if (context.xray != null) context.xray.toBack();
					context.selectedPoint.toFront();
					context.secondPoint.toFront();
					context.selectedPoint = null;
					context.secondPoint = null;
				}
			}
		});
		primaryStage.show();
	}
	
	private double calculateX(Circle center, Circle point1, Circle point2) {
		return center.getCenterX() + Math.sin(Math.PI / 2 + angle(point1.getCenterX(), point1.getCenterY(),
				point2.getCenterX(), point2.getCenterY())) * 100;
	}
	
	private double calculateY(Circle center, Circle point1, Circle point2) {
		return center.getCenterY() + Math.cos(Math.PI / 2 + angle(point1.getCenterX(), point1.getCenterY(),
				point2.getCenterX(), point2.getCenterY())) * 100;
	}
	
	private double angle (double x1, double y1, double x2, double y2) {
		double xdiff = x1 - x2;
		double ydiff = y1 - y2;
		double tan = xdiff / ydiff;
		double atan = Math.atan2(ydiff, xdiff);
		return atan;
	}
	private Group makeDraggable(final Circle point) {
		
		final Group wrapGroup = new Group(point);
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
			context.moved = false;
			context.mouseAnchorX = mouseEvent.getX();
			context.mouseAnchorY = mouseEvent.getY();
			context.initialCenterX = point.getCenterX();
			context.initialCenterY = point.getCenterY();
			
		});
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
			
			context.moved = true;
			
			point.setCenterX(context.initialCenterX
					+ mouseEvent.getX()
					- context.mouseAnchorX);
			point.setCenterY(context.initialCenterY
					+ mouseEvent.getY()
					- context.mouseAnchorY);
			
			if (context.l1 != null && context.l2 != null) {
				Point2D p1 = new Point2D(context.l1.getStartX(), context.l1.getStartY());
				Point2D p2 = new Point2D(context.l1.getEndX(), context.l1.getEndY());
				Point2D p3 = new Point2D(context.l2.getStartX(), context.l2.getStartY());
				Point2D p4 = new Point2D(context.l2.getEndX(), context.l2.getEndY());
				
				Point2D v1 = p2.subtract(p1);
				Point2D v2 = p4.subtract(p3);
				context.text.setText("" + v2.angle(v1));
				
			}
		});
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> {
			if (!context.moved) {
				if (deleteMode.get()) {
					context.root.getChildren().remove(wrapGroup);
					Iterator<Node> nodeIterator = context.root.getChildren().iterator();
					while (nodeIterator.hasNext()) {
						Node node = nodeIterator.next();
						if (node.getClass() == Line.class) {
							Line line = (Line) node;
							if (line.getEndX() == point.getCenterX() && line.getEndY() == point.getCenterY()
									|| line.getStartX() == point.getCenterX() && line.getStartY() == point.getCenterY()) {
								nodeIterator.remove();
							}
						}
					}
				} else {
					if (context.selectedPoint == null) {
						context.selectedPoint = wrapGroup;
					} else {
						context.secondPoint = wrapGroup;
					}
					((Circle) wrapGroup.getChildren().get(0)).setFill(Color.RED);
				}
			}
		});
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> wrapGroup.getScene().setCursor(Cursor.HAND));
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_EXITED, event -> wrapGroup.getScene().setCursor(Cursor.DEFAULT));
		
		wrapGroup.addEventFilter(MouseEvent.ANY, event -> {
			if (context.selectedPoint == null || context.secondPoint == null) event.consume();
		});
		return wrapGroup;
	}
	
	private static final class Context {
		double mouseAnchorX;
		double mouseAnchorY;
		double initialCenterX;
		double initialCenterY;
		boolean moved;
		Group selectedPoint;
		Group secondPoint;
		ImageView xray;
		Pane root;
		Line l1;
		Line l2;
		Text text;
	}
}