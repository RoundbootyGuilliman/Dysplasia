package app;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.lang.Math;

import java.util.Locale;

import static app.Runner.*;
import static app.Utils.*;

public class Geometry {
	
	static Line createPerp(Circle base1, Circle base2) {
		Point2D center = new Point2D(
				(1 - 0.5) * base1.getCenterX() + 0.5 * base2.getCenterX(),
				(1 - 0.5) * base1.getCenterY() + 0.5 * base2.getCenterY());
		
		return createPerp(base1, base2, toCircle(center));
	}
	
	static Line createPerp(Circle base1, Circle base2, Circle startPoint) {
		
		return createPerp(base1, base2, startPoint, 50);
	}
	
	static Line createPerp(Circle base1, Circle base2, Circle startPoint, double distance) {
		
		double x = calculatePerpendicularX(startPoint, base1, base2, distance);
		double y = calculatePerpendicularY(startPoint, base1, base2, distance);
		
		return new Line(startPoint.getCenterX(), startPoint.getCenterY(), x, y);
	}
	
	static Point2D getIntersection(Line a, Line b) {
		
		double x, y;
		
		double ma, mb, ta, tb;
		
		ma = (a.getStartY() - a.getEndY()) / (a.getStartX() - a.getEndX());
		mb = (b.getStartY() - b.getEndY()) / (b.getStartX() - b.getEndX());
		ta = a.getStartY() - ma * a.getStartX();
		tb = b.getStartY() - mb * b.getStartX();
		
		x = (tb - ta) / (ma - mb);
		y = ma * x + ta;
		
		return new Point2D(x, y);
	}
	
	private static double calculatePerpendicularX(Circle start, Circle point1, Circle point2, double distance) {
		return start.getCenterX() + Math.cos(angle(point1.getCenterX(), point1.getCenterY(),
				point2.getCenterX(), point2.getCenterY())) * distance;
	}
	
	private static double calculatePerpendicularY(Circle start, Circle point1, Circle point2, double distance) {
		return start.getCenterY() + Math.sin(angle(point1.getCenterX(), point1.getCenterY(),
				point2.getCenterX(), point2.getCenterY())) * distance;
	}
	
	private static double angle(double x1, double y1, double x2, double y2) {
		double xdiff = x1 - x2;
		double ydiff = y1 - y2;
		//double tan = xdiff / ydiff;
		return Math.PI / 2 + Math.atan2(ydiff, xdiff);
	}
	
	static void calcLeftAngle(Circle leftPoint, Circle baseLeftPoint, Circle baseRightPoint, double offset) {
		Point2D left = toPoint(leftPoint);
		Point2D baseL = toPoint(baseLeftPoint);
		Point2D baseR = toPoint(baseRightPoint);
		
		Point2D vector = baseL.subtract(left);
		Point2D base = baseR.subtract(baseL);
		c.textL.setText(String.format(Locale.ROOT, "%.1f", Math.abs(base.angle(vector) - offset)) + "\u00B0");
	}
	
	static void calcRightAngle(Circle rightPoint, Circle baseLeftPoint, Circle baseRightPoint, double offset) {
		Point2D right = toPoint(rightPoint);
		Point2D baseL = toPoint(baseLeftPoint);
		Point2D baseR = toPoint(baseRightPoint);
		
		Point2D vector = baseR.subtract(right);
		Point2D base = baseL.subtract(baseR);
		c.textR.setText(String.format(Locale.ROOT, "%.1f", Math.abs(base.angle(vector) - offset)) + "\u00B0");
	}
	
	static void calcLeftAngle(Circle leftPoint, Circle baseLeftPoint, Circle baseRightPoint) {
		calcLeftAngle(leftPoint, baseLeftPoint, baseRightPoint, 0);
	}
	
	static void calcRightAngle(Circle rightPoint, Circle baseLeftPoint, Circle baseRightPoint) {
		calcRightAngle(rightPoint, baseLeftPoint, baseRightPoint, 0);
	}
	
	static double calculateProlongedStartX(Circle point1, Circle point2) {
		return ((1 + c.k) * point1.getCenterX() - c.k * point2.getCenterX());
	}
	
	static double calculateProlongedStartY(Circle point1, Circle point2) {
		return ((1 + c.k) * point1.getCenterY() - c.k * point2.getCenterY());
	}
	
	static double calculateProlongedEndX(Circle point1, Circle point2) {
		return ((1 - c.k) * point1.getCenterX() + c.k * point2.getCenterX());
	}
	
	static double calculateProlongedEndY(Circle point1, Circle point2) {
		return ((1 - c.k) * point1.getCenterY() + c.k * point2.getCenterY());
	}
}
