import java.util.ArrayList;
import java.util.List;

public class hw5 {
	public static void main(String[] args) {
		Set set1 = new Set(6.04399, 3.67118, 4.97863, 2.02978, 7.62406, 5.53849, 3.60126,
			  8.31985);
		System.out.println(isConvexQuadrilateral(set1.getPoints()));
		
		Set set2 = new Set(5.18857, 0.315407, 6.15757, 5.68853, 5.06168, 8.87515, 1.98366, 7.87051);
		System.out.println(isConvexQuadrilateral(set2.getPoints()));
		
		Set set3 = new Set(4.20669, 6.16911, 7.65283, 4.90153, 7.99532, 2.99096, 9.57413, 8.43098);
		System.out.println(isConvexQuadrilateral(set3.getPoints()));
		
		Set set4 = new Set(0.598561, 3.50918, 7.52086, 5.26124, 8.79013, 6.41196, 5.98564, 6.5419);
		System.out.println(isConvexQuadrilateral(set4.getPoints()));
	}
	
	private static double signedArea(Point a, Point b, Point c) {
		return ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));
	}
	
	private static boolean leftOf(Point a, Point b, Point c) {
		return signedArea(a, b, c) > 0;
	}
	
	private static boolean isConvexQuadrilateral(Point[] points) {
		// Checks if it even has exactly four vertices in the first place
		if (points.length != 4)
			return false;
		
		// Checks that every point is left of the line connected between the previous two points
		for (int i = 0; i < 4; ++i) {
			/*System.out.printf("Checking if point (%.2f, %.2f) is left of line segment (%.2f, %.2f)-(%.2f, %.2f) ",
				  points[(i + 2) % 4].x, points[(i + 2) % 4].y,
				  points[i].x, points[i].y,
				  points[(i + 1) % 4].x, points[(i + 1) % 4].y);*/
			if (!leftOf(points[i], points[(i + 1) % 4], points[(i + 2) % 4]))
				return false;
			//System.out.println("It is.");
		}
		return true;
	}
	
	private static class Point {
		double x, y;
		
		Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}
	
	private static class Set {
		List<Point> points;
		
		Set(double... coordinates) {
			points = new ArrayList<>();
			for (int i = 1; i < coordinates.length; i += 2)
				points.add(new Point(coordinates[i - 1], coordinates[i]));
		}
		
		public Point get(int i) {
			return points.get(i);
		}
		
		public Point[] getPoints() {
			return points.toArray(new Point[0]);
		}
	}
}