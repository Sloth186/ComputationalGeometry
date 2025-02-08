import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class hw3 {
	public static void main(String[] args) {
		// Random number generator for generating set of points
		Random rg = new Random();
		// Object to store a set of points
		Set set = new Set();
		// Number of points in our set
		final int SIZE = 10;
		// Bound on possible x and y coordinates that can be generated
		final double BOUND = 10.0;
		// Object to store points on the convex hull
		Set hull = new Set();
		
		// Generate random points and add them to the set
		for (int i = 0; i < SIZE; ++i)
			set.add(rg.nextDouble(BOUND), rg.nextDouble(BOUND));
		// Print the set of points
		System.out.println("Set of points: " + set);
		
		// Set up for the Naive Algorithm
		Point[] points = set.toArray(); // Converts set of points to array
		int i, j, k; // Helper variables
		boolean isHullEdge; // Tracks whether the "current" edge is a hull edge or not
		
		// Loops through every point in the set
		for (i = 0; i < points.length; ++i) {
			// Loops through every point in the set
			for (j = 0; j < points.length; ++j) {
				// Excludes the point currently used by the outer loop
				if (j != i) {
					// Start checking whether if it is a hull edge
					isHullEdge = true;
					// Loops through every point in the set
					for (k = 0; k < points.length && isHullEdge; ++k) {
						// Excludes the two points currently used by the outer and middle loops
						if (k != i && k != j)
							// Checks if the kth point is left or right to the line segment i-j
							isHullEdge = leftOf(points[i], points[j], points[k]);
					}
					// If the previous loop never returned false, it means all points (other
					// than i, j) were left of i-j, and therefore i-j is a hull edge
					if (isHullEdge)
						hull.add(points[i]);
				}
			}
		}
		
		// Print the points on the convex hull boundary
		System.out.println("Hull: " + hull);
	}
	
	// Give the area of the polygon formed by a-b and a-c
	private static double signedArea(Point a, Point b, Point c) {
		return ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));
	}
	
	// Check if point c is to the left of a-b
	private static boolean leftOf(Point a, Point b, Point c) {
		return signedArea(a, b, c) > 0;
	}
	
	// Class to hold information about a point
	private static class Point {
		double x, y;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return "{" + x + ", " + y + "}";
		}
	}
	
	// Class to hold information about a set of points
	private static class Set {
		List<Point> points;
		
		public Set() {
			points = new ArrayList<Point>();
		}
		
		public void add(double x, double y) {
			points.add(new Point(x, y));
		}
		
		public void add(Point point) {
			add(point.x, point.y);
		}
		
		public Point[] toArray() {
			return points.toArray(new Point[0]);
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			for (int i = 0; i < points.size() - 1; ++i) {
				sb.append(points.get(i)).append(", ");
			}
			sb.append(points.getLast()).append("}");
			return sb.toString();
		}
	}
}