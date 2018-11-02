package app;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Stream;

import static app.Geometry.*;
import static app.Runner.c;

class Utils {
	
	static Point2D toPoint(Circle circle) {
		return new Point2D(circle.getCenterX(), circle.getCenterY());
	}
	
	static Circle toCircle(Point2D point2D) {
		return new Circle(point2D.getX(), point2D.getY(), 8);
	}
	
	static Circle toCircle(double x, double y) {
		return new Circle(x, y, 8);
	}
	
	static Circle createADWRCircle(Circle point1, Circle point2, Circle point3) {
		
		Circle cross = toCircle(getIntersection(createPerp(point1, point2), createPerp(point2, point3)));
		
		Circle circle = new Circle(cross.getCenterX(), cross.getCenterY(), toPoint(cross).distance(toPoint(point1)));
		
		Stream.of(point1, point2, point3)
				.forEach(point -> {
					point.centerXProperty().addListener(((observable, oldValue, newValue) -> updateADWRCircle(circle, point1, point2, point3)));
					point.centerYProperty().addListener(((observable, oldValue, newValue) -> updateADWRCircle(circle, point1, point2, point3)));
				});
		
		
		circle.setFill(Color.TRANSPARENT);
		circle.setStroke(Color.RED);
		return circle;
	}
	
	static Line createADWRLine(Circle point1, Circle point2, Circle point3, Text text) {
		
		Point2D ADWRcross = getIntersection(createPerp(point1, point3, point2), createRegularLine(point1, point3));
		Line adwr = new Line(ADWRcross.getX(), ADWRcross.getY(), point2.getCenterX(), point2.getCenterY());
		
		Stream.of(point1, point2, point3)
				.forEach(point -> {
					point.centerXProperty().addListener(((observable, oldValue, newValue) -> updateADWR(adwr, point1, point2, point3, text)));
					point.centerYProperty().addListener(((observable, oldValue, newValue) -> updateADWR(adwr, point1, point2, point3, text)));
				});
		text.setText(Math.round(ADWRcross.distance(toPoint(point2))
				/ toPoint(point1).distance(toPoint(point3)) * 100) + "%");
		return adwr;
	}
	
	static Circle createFemoralCircle(Circle point) {
		Circle femoralCircle = new Circle(point.getCenterX(), point.getCenterY(), 50);
		
		femoralCircle.setFill(Color.TRANSPARENT);
		femoralCircle.setStroke(Color.RED);
		femoralCircle.centerXProperty().bind(point.centerXProperty());
		femoralCircle.centerYProperty().bind(point.centerYProperty());
		SimpleBooleanProperty clicked = new SimpleBooleanProperty();
		point.setOnMousePressed(event -> clicked.set(true));
		point.setOnMouseReleased(event -> clicked.set(false));
		point.setOnScroll((ScrollEvent event) -> {
			if (clicked.get()) {
				double zoomFactor = 1.05;
				double deltaY = event.getDeltaY();
				if (deltaY < 0) {
					zoomFactor = 2.0 - zoomFactor;
				}
				femoralCircle.setRadius(femoralCircle.getRadius() * zoomFactor);
			}
		});
		return femoralCircle;
	}
	
	static Line createRegularLine(Circle point1, Circle point2) {
		
		Line line = new Line(point1.getCenterX(), point1.getCenterY(), point2.getCenterX(), point2.getCenterY());
		
		line.startXProperty().bind(point1.centerXProperty());
		line.startYProperty().bind(point1.centerYProperty());
		line.endXProperty().bind(point2.centerXProperty());
		line.endYProperty().bind(point2.centerYProperty());
		
		line.setStroke(Color.YELLOWGREEN);
		line.toBack();
		return line;
	}
	
	static Line createProlongedLine(Circle point1, Circle point2) {
		
		
		Line line = new Line(calculateProlongedStartX(point1, point2), calculateProlongedStartY(point1, point2),
				calculateProlongedEndX(point1, point2), calculateProlongedEndY(point1, point2));
		
		Stream.of(point1, point2).forEach(point -> {
			
			point.centerXProperty().addListener(((observable, oldValue, newValue) -> moveLine(line, point1, point2)));
			point.centerYProperty().addListener(((observable, oldValue, newValue) -> moveLine(line, point1, point2)));
		});
		
		line.setStroke(Color.YELLOWGREEN);
		line.toBack();
		return line;
	}
	
	
	static void createStartButt(Line line) {
		
		Line dlp1 = new Line();
		
		dlp1.startXProperty().bind(line.startXProperty());
		dlp1.startYProperty().bind(line.startYProperty());
		dlp1.endXProperty().bind(line.startXProperty());
		dlp1.endYProperty().bind(line.startYProperty().subtract((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		Line dlp2 = new Line();
		
		dlp2.startXProperty().bind(line.startXProperty());
		dlp2.startYProperty().bind(line.startYProperty());
		dlp2.endXProperty().bind(line.startXProperty());
		dlp2.endYProperty().bind(line.startYProperty().add((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		
		dlp1.setStroke(Color.YELLOW);
		dlp2.setStroke(Color.YELLOW);
		c.root.getChildren().addAll(dlp1, dlp2);
		c.frontNodes.addAll(Arrays.asList(dlp1, dlp2));
	}
	
	static void createEndButt(Line line) {
		
		Line dlp3 = new Line();
		
		dlp3.startXProperty().bind(line.endXProperty());
		dlp3.startYProperty().bind(line.endYProperty());
		dlp3.endXProperty().bind(line.endXProperty());
		dlp3.endYProperty().bind(line.endYProperty().subtract((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		Line dlp4 = new Line();
		
		dlp4.startXProperty().bind(line.endXProperty());
		dlp4.startYProperty().bind(line.endYProperty());
		dlp4.endXProperty().bind(line.endXProperty());
		dlp4.endYProperty().bind(line.endYProperty().add((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		dlp3.setStroke(Color.YELLOW);
		dlp4.setStroke(Color.YELLOW);
		c.root.getChildren().addAll(dlp3, dlp4);
		c.frontNodes.addAll(Arrays.asList(dlp3, dlp4));
	}
	
	static void createPointButt(Line line, Circle point) {
		
		Line pp1 = new Line();
		
		pp1.startXProperty().bind(point.centerXProperty());
		pp1.startYProperty().bind(point.centerYProperty());
		pp1.endXProperty().bind(point.centerXProperty());
		pp1.endYProperty().bind(point.centerYProperty().subtract((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		Line pp2 = new Line();
		
		pp2.startXProperty().bind(point.centerXProperty());
		pp2.startYProperty().bind(point.centerYProperty());
		pp2.endXProperty().bind(point.centerXProperty());
		pp2.endYProperty().bind(point.centerYProperty().add((line.endXProperty().subtract(line.startXProperty())).divide(6)));
		
		
		pp1.setStroke(Color.YELLOW);
		pp2.setStroke(Color.YELLOW);
		c.root.getChildren().addAll(pp1, pp2);
		c.frontNodes.addAll(Arrays.asList(pp1, pp2));
	}
	
	
	private static void moveLine(Line line, Circle point1, Circle point2) {
		line.setStartX(calculateProlongedStartX(point1, point2));
		line.setStartY(calculateProlongedStartY(point1, point2));
		line.setEndX(calculateProlongedEndX(point1, point2));
		line.setEndY(calculateProlongedEndY(point1, point2));
	}
	
	static void updateADWRCircle(Circle circle, Circle point1, Circle point2, Circle point3) {
		
		Circle cross = toCircle(getIntersection(createPerp(point1, point2), createPerp(point2, point3)));
		
		circle.setCenterX(cross.getCenterX());
		circle.setCenterY(cross.getCenterY());
		circle.setRadius(toPoint(cross).distance(toPoint(point1)));
		
	}
	
	static void updateADWR(Line adwr, Circle point1, Circle point2, Circle point3, Text text) {
		
		Point2D ADWRcross = getIntersection(createPerp(point1, point3, point2), createRegularLine(point1, point3));
		adwr.setStartX(ADWRcross.getX());
		adwr.setStartY(ADWRcross.getY());
		adwr.setEndX(point2.getCenterX());
		adwr.setEndY(point2.getCenterY());
		text.setText(Math.round(ADWRcross.distance(toPoint(point2))
				/ toPoint(point1).distance(toPoint(point3)) * 100) + "%");
	}
	
	static void updateCEA(Line cea, Circle point, Circle basepoint1, Circle basepoint2, Circle anglePoint) {
		Line perpTemp = createPerp(basepoint1, basepoint2, point);
		
		Circle perpPoint = new Circle(perpTemp.getEndX(), perpTemp.getEndY(), 8);
		
		cea.setStartX(calculateProlongedStartX(point, perpPoint));
		cea.setEndX(calculateProlongedEndX(point, perpPoint));
		cea.setStartY(calculateProlongedStartY(point, perpPoint));
		cea.setEndY(calculateProlongedEndY(point, perpPoint));
	}
	
	static void calculateAcetabular(Circle pointL2, Circle pointL3, Circle pointR2, Circle pointR3) {
		Stream.of(pointL2, pointL3, pointR3).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent ->
						calcLeftAngle(pointL2, pointL3, pointR3)));
		Stream.of(pointL3, pointR2, pointR3).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent ->
						calcRightAngle(pointR2, pointL3, pointR3)));
		calcLeftAngle(pointL2, pointL3, pointR3);
		calcRightAngle(pointR2, pointL3, pointR3);
	}
	
	
	static final class Context {
		double mouseAnchorX;
		double mouseAnchorY;
		double initialCenterX;
		double initialCenterY;
		boolean moved;
		
		BorderPane rootRoot;
		BorderPane root;
		VBox uiPane;
		ImageView xray;
		
		int k = 100;
		
		List<Circle> innerPoints = new ArrayList<>();
		
		SortedMap<String, Circle> points = new TreeMap<>();
//			put("lp1", null);
//			put("lp2", null);
//			put("lp3", null);
//			put("lp4", null);
//			put("rp1", null);
//			put("rp2", null);
//			put("rp3", null);
//			put("rp4", null);
		;
		List<String> pointsRequired = new ArrayList<>();
		List<Node> frontNodes = new ArrayList<>();
		Text textL;
		Text textR;
		Text textC;
		
		String currentPoint = "lp1";
		boolean pointsDrawn = false;

//		Circle L1;
//		Circle L2;
//		Circle L3;
//		Circle L4;
//		Circle R1;
//		Circle R2;
//		Circle R3;
//		Circle R4;
	}
	
	static final class Shapes {
		Line L1R1;
		Line L2R2;
		Line hilgenreinerLine;
		Line L4R4;
		
		Line L1L2;
		Line R1R2;
		Line L2L3;
		Line R2R3;
		
		
		Line L2L4;
		Line R2R4;
		Line adwrL;
		Line adwrR;
		Circle ADWRCircleL;
		Circle ADWRCircleR;
		Circle femoralCricleL;
		Circle femoralCricleR;
		
		Line L1Perp;
		Line R1Perp;
	}
}
