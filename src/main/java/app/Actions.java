package app;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.Locale;
import java.util.stream.Stream;

import static app.Geometry.*;
import static app.Runner.c;
import static app.Runner.s;
import static app.Utils.*;

public class Actions {
	
	static void calculateAdr() {
		
		Circle pointL2 = c.points.get("lp2");
		Circle pointL3 = c.points.get("lp3");
		Circle pointL4 = c.points.get("lp4");
		Circle pointR2 = c.points.get("rp2");
		Circle pointR3 = c.points.get("rp3");
		Circle pointR4 = c.points.get("rp4");
		
		
		s.L2R2 = createProlongedLine(pointL2, pointR2);
		
		s.hilgenreinerLine = createProlongedLine(pointL3, pointR3);
		
		s.L4R4 = createProlongedLine(pointL4, pointR4);
		
		s.L2L4 = createRegularLine(pointL2, pointL4);
		
		s.R2R4 = createRegularLine(pointR2, pointR4);
		
		s.ADWRCircleL = createADWRCircle(pointL2, pointL3, pointL4);
		
		s.adwrL = createADWRLine(pointL2, pointL3, pointL4, c.textL);
		
		s.ADWRCircleR = createADWRCircle(pointR2, pointR3, pointR4);
		
		s.adwrR = createADWRLine(pointR2, pointR3, pointR4, c.textR);
		
		s.L2L4.setStroke(Color.YELLOW);
		s.R2R4.setStroke(Color.YELLOW);
		s.adwrL.setStroke(Color.YELLOW);
		s.adwrR.setStroke(Color.YELLOW);
		s.adwrL.setStrokeWidth(2);
		s.adwrR.setStrokeWidth(2);
	}
	
	static void calculateAi() {
		Circle pointL2 = c.points.get("lp2");
		Circle pointL3 = c.points.get("lp3");
		Circle pointR2 = c.points.get("rp2");
		Circle pointR3 = c.points.get("rp3");
		
		s.hilgenreinerLine = createProlongedLine(pointL3, pointR3);
		s.L2L3 = createRegularLine(pointL2, pointL3);
		s.R2R3 = createRegularLine(pointR2, pointR3);
		
		calculateAcetabular(pointL2, pointL3, pointR2, pointR3);
		
		s.L2L3.setStroke(Color.YELLOW);
		s.R2R3.setStroke(Color.YELLOW);
	}
	
	static void calculateCea() {
		Circle pointL1 = c.points.get("lp1");
		Circle pointL2 = c.points.get("lp2");
		Circle pointR1 = c.points.get("rp1");
		Circle pointR2 = c.points.get("rp2");
		
		s.L1R1 = createProlongedLine(pointL1, pointR1);
		s.L1R1.setStroke(Color.YELLOW);
		
		Line l1PerpTemp = createPerp(pointL1, pointR1, pointL1);
		Line r1PerpTemp = createPerp(pointL1, pointR1, pointR1);
		
		Circle leftPerpPoint = new Circle(l1PerpTemp.getEndX(), l1PerpTemp.getEndY(), 8);
		Circle rightPerpPoint = new Circle(r1PerpTemp.getEndX(), r1PerpTemp.getEndY(), 8);
		
		s.L1Perp = createProlongedLine(pointL1, leftPerpPoint);
		s.R1Perp = createProlongedLine(pointR1, rightPerpPoint);
		
		s.L1L2 = createRegularLine(pointL1, pointL2);
		s.R1R2 = createRegularLine(pointR1, pointR2);
		
		calcLeftAngle(pointL2, pointL1, leftPerpPoint, 180);
		calcRightAngle(pointR2, rightPerpPoint, pointR1, 180);
		
		c.ctx.widthProperty().addListener(((observable, oldValue, newValue) -> {
			updateCEA(s.L1Perp, pointL1, pointL1, pointR1);
			updateCEA(s.R1Perp, pointR1, pointL1, pointR1);
		}));
		
		c.ctx.heightProperty().addListener(((observable, oldValue, newValue) -> {
			updateCEA(s.L1Perp, pointL1, pointL1, pointR1);
			updateCEA(s.R1Perp, pointR1, pointL1, pointR1);
		}));
		
		Stream.of(pointL1, pointR1).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
					updateCEA(s.L1Perp, pointL1, pointL1, pointR1);
					updateCEA(s.R1Perp, pointR1, pointL1, pointR1);
				}));
		
		Stream.of(pointL2, pointL1, pointR1).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							Line perpTemp = createPerp(pointL1, pointR1, pointL1);
							calcLeftAngle(pointL2, pointL1, new Circle(perpTemp.getEndX(), perpTemp.getEndY(), 8), 180);
						}
				));
		
		Stream.of(pointR2, pointL1, pointR1).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							Line perpTemp = createPerp(pointL1, pointR1, pointR1);
							calcRightAngle(pointR2, new Circle(perpTemp.getEndX(), perpTemp.getEndY(), 8), pointR1, 180);
						}
				));
		
		s.L1Perp.setStroke(Color.YELLOW);
		s.R1Perp.setStroke(Color.YELLOW);
	}
	
	static void calculateAa() {
		
		Circle pointL2 = c.points.get("lp2");
		Circle pointL4 = c.points.get("lp4");
		Circle pointR2 = c.points.get("rp2");
		Circle pointR4 = c.points.get("rp4");
		
		s.L4R4 = createProlongedLine(pointL4, pointR4);
		s.L2L4 = createRegularLine(pointL2, pointL4);
		s.R2R4 = createRegularLine(pointR2, pointR4);
		
		calculateAcetabular(pointL2, pointL4, pointR2, pointR4);
		
		s.L2L4.setStroke(Color.YELLOW);
		s.R2R4.setStroke(Color.YELLOW);
	}
	
	static void calculateRi() {
		
		Circle pointL2 = c.points.get("lp2");
		Circle pointL1 = c.points.get("lp1");
		Circle pointR2 = c.points.get("rp2");
		Circle pointR1 = c.points.get("rp1");
		
		Circle femoralL = createFemoralCircle(pointL1);
		Circle femoralR = createFemoralCircle(pointR1);
		
		s.femoralCricleL = femoralL;
		s.femoralCricleR = femoralR;
		
		createRiStuff(pointL1, pointL2, femoralL, c.textL, -1);
		createRiStuff(pointR1, pointR2, femoralR, c.textR, 1);
	}
	
	static void calculateCi() {
		Circle pointL1 = c.points.get("lp1");
		Circle pointL2 = c.points.get("lp2");
		Circle pointL4 = c.points.get("lp4");
		Circle pointR1 = c.points.get("rp1");
		Circle pointR2 = c.points.get("rp2");
		Circle pointR4 = c.points.get("rp4");
		
		Circle femoralL = createFemoralCircle(pointL1);
		Circle femoralR = createFemoralCircle(pointR1);
		
		s.femoralCricleL = femoralL;
		s.femoralCricleR = femoralR;
		
		createCiStuff(pointL1, pointL2, pointL4, femoralL, c.textL, -1);
		createCiStuff(pointR1, pointR2, pointR4, femoralR, c.textR, 1);
	}
	
	private static void createRiStuff(Circle point1, Circle point2, Circle circle, Text text, int multiplier) {
		
		Line perpendicular1 = new Line();
		
		perpendicular1.startXProperty().bind(point1.centerXProperty().subtract(circle.radiusProperty().multiply(multiplier)));
		perpendicular1.startYProperty().bind(point1.centerYProperty());
		
		perpendicular1.endXProperty().bind(point1.centerXProperty().add(circle.radiusProperty().multiply(multiplier)));
		perpendicular1.endYProperty().bind(point1.centerYProperty());
		
		Line perpendicular2 = new Line();
		
		perpendicular2.startXProperty().bind(perpendicular1.startXProperty());
		perpendicular2.startYProperty().bind(point2.centerYProperty());
		
		perpendicular2.endXProperty().bind(perpendicular1.endXProperty());
		perpendicular2.endYProperty().bind(point2.centerYProperty());
		
		perpendicular1.setStroke(Color.YELLOW);
		perpendicular2.setStroke(Color.YELLOW);
		
		createStartButt(perpendicular1);
		createEndButt(perpendicular1);
		createStartButt(perpendicular2);
		createPointButt(perpendicular2, point2);
		
		c.ctx.getChildren().addAll(perpendicular1, perpendicular2);
		
		setTextRICI(text, perpendicular1, perpendicular2, point2);
	}
	
	private static void createCiStuff(Circle point1, Circle point2, Circle point4, Circle circle, Text text, int multiplier) {
		
		
		Circle cross = new Circle();
		
		cross.centerXProperty().bind(point4.centerXProperty());
		cross.centerYProperty().bind(point1.centerYProperty());
		
		Circle sub = new Circle();
		
		sub.centerXProperty().bind(point1.centerXProperty().add(circle.radiusProperty().multiply(multiplier)));
		sub.centerYProperty().bind(point1.centerYProperty());
		
		Line vertical4 = createRegularLine(point4, cross);
		
		Line horizontal1 = createRegularLine(sub, cross);
		
		Circle subL = new Circle();
		Circle subR = new Circle();
		
		subL.centerXProperty().bind(sub.centerXProperty());
		subL.centerYProperty().bind(point2.centerYProperty());
		subR.centerXProperty().bind(cross.centerXProperty());
		subR.centerYProperty().bind(point2.centerYProperty());
		
		Line horizontal2 = createRegularLine(subL, subR);
		
		c.ctx.getChildren().addAll(vertical4, horizontal1, horizontal2);
		
		createStartButt(horizontal1);
		createEndButt(horizontal1);
		
		createPointButt(horizontal2, point2);
		createEndButt(horizontal2);
		
		setTextRICI(text, horizontal1, horizontal2, point2);
	}
	
	private static void setTextRICI(Text text, Line horizontal1, Line horizontal2, Circle point2) {
		text.setText(String.format(Locale.ROOT, "%.1f",
				(horizontal2.getEndX() - point2.getCenterX()) / (horizontal1.getEndX() - horizontal1.getStartX()) * 100) + "%");
		Stream.of(
				point2.centerXProperty(), point2.centerYProperty(),
				horizontal1.startXProperty(), horizontal1.startYProperty(),
				horizontal1.endXProperty(), horizontal1.endYProperty())
				.forEach(prop ->
						prop.addListener(((observable, oldValue, newValue) ->
								text.setText(String.format(Locale.ROOT, "%.1f",
										(horizontal2.getEndX() - point2.getCenterX()) / (horizontal1.getEndX() - horizontal1.getStartX()) * 100)
										+ "%"))));
	}
	
	static void calculateSdu() {
		
		Circle pointL1 = c.points.get("lp1");
		Circle pointL5 = c.points.get("lp5");
		Circle pointL6 = c.points.get("lp6");
		
		Circle pointR1 = c.points.get("rp1");
		Circle pointR5 = c.points.get("rp5");
		Circle pointR6 = c.points.get("rp6");
		
		Line l1l5 = createRegularLine(pointL1, pointL5);
		Line l5l6 = createRegularLine(pointL5, pointL6);
		
		Line r1r5 = createRegularLine(pointR1, pointR5);
		Line r5r6 = createRegularLine(pointR5, pointR6);
		
		calcLeftAngle(pointL1, pointL5, pointL6, 180);
		calcRightAngle(pointR1, pointR6, pointR5, 180);
		
		Stream.of(pointL1, pointL5, pointL6).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							calcLeftAngle(pointL1, pointL5, pointL6, 180);
						}
				));
		
		Stream.of(pointR1, pointR5, pointR6).forEach(point ->
				point.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
							calcRightAngle(pointR1, pointR6, pointR5, 180);
						}
				));
		
		c.ctx.getChildren().addAll(l1l5, l5l6, r1r5, r5r6);
	}
}
