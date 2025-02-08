import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class hw4 {
	public static void main(String[] args) {
		// Random number generator for generating set of points
		Random rg = new Random();
		// Object to store a set of points
		Set set = new Set();
		// Number of points in our set
		final int SIZE = 500;
		// Bound on possible x and y coordinates that can be generated
		final double BOUND = 25.0;
		// Object to store points on the convex hull
		Set hull = new Set();
		
		// Generate random points and add them to the set
		for (int i = 0; i < SIZE; ++i)
			set.add(rg.nextDouble(BOUND), rg.nextDouble(BOUND));
		// Print the set of points
		System.out.println("Set of points: " + set);
		
		// Graham Scan Algorithm
		// Calculate the anchor point of the set (and remove it from the set itself)
		set.findAnchorPoint();
		// Sort the remaining points by angle to anchor point, from largest to smallest
		set.sort();
		
		// Prints the anchor point and set points for manual verification of calculations
		// System.out.println("Anchor point: " + set.anchorPoint + "\nSorted set: " + set);
		
		// Convert to an array for ease of work
		Point[] points = set.toArray();
		
		// Add anchor point to hull
		hull.add(set.anchorPoint);
		
		// Check size of the set
		if (points.length > 1) {
			// Add next two points of the convex hull
			hull.add(points[0], points[1]);
			// Helper variables to track indices of the previous two points added to the hull
			int prevSecond = 1, prevFirst = 2, current = 3;
			
			// Loop through all points in the set, using the GSA to build the convex hull
			for (int pointsCurrent = 2; pointsCurrent < points.length; ++pointsCurrent) {
				// Add the current point to the hull
				hull.add(points[pointsCurrent]);
				
				// Print statements for debugging
				// System.out.println("Checking {" + hull.get(prevSecond) + ", " + hull.get(prevFirst) + ", " + hull.get(current) + "}");
				// System.out.println("\t" + leftOf(hull.get(prevSecond), hull.get(prevFirst), hull.get(current)));
				
				// If the most recent three points of the hull has a right turn, delete the middle
				// of the three and repeat, until the three points has a left turn
				// (treats collinearity the same as a right turn)
				while (prevSecond >= 0 && !leftOf(hull.get(prevSecond), hull.get(prevFirst), hull.get(current))) {
					hull.remove(prevFirst);
					--current; // No I do
					--prevFirst; // not know what
					--prevSecond; // I am doing
				} // here and I
				// do not care
				++current; // anymore as long
				++prevFirst; // as it
				++prevSecond; // works :D
			}
		} else {
			// Small set of points, hull is immediate
			if (points.length == 1)
				hull.add(points[0]);
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
			return "{" + Math.round(x * 100.) / 100.0 + ", " + Math.round(y * 100.) / 100.0 + "}";
		}
	}
	
	// Class to hold information about a set of points
	private static class Set {
		List<Point> points;
		// Stores the anchor point (separately from the rest of the points
		// for convenience in the Graham Scan Algorithm implementation)
		Point anchorPoint;
		
		public Set() {
			points = new ArrayList<Point>();
			anchorPoint = null;
		}
		
		public void add(double x, double y) {
			points.add(new Point(x, y));
		}
		
		public void add(Point... points) {
			for (Point point : points)
				add(point.x, point.y);
		}
		
		public Point get(int index) {
			return points.get(index);
		}
		
		public void remove(int index) {
			points.remove(index);
		}
		
		// Calculates the anchor point as the point with the lowest y-coordinate,
		// breaking ties with the greatest x-coordinate
		public void findAnchorPoint() {
			int anchorPointIndex = 0;
			for (int i = 1; i < points.size(); ++i)
				if (points.get(i).y < points.get(anchorPointIndex).y || (points.get(i).y == points.get(anchorPointIndex).y && points.get(i).x > points.get(anchorPointIndex).x))
					anchorPointIndex = i;
			anchorPoint = points.get(anchorPointIndex);
			// Removes the anchor point from the set for convenience
			// in the implementation of the algorithm
			points.remove(anchorPointIndex);
		}
		
		// Credit to Geeks For Geeks for guidance
		public void sort() {
			quickSort(0, points.size() - 1);
		}
		
		// Partitions the array and pivots it in place so that elements to the left
		// of the pivot are LeftOf, and elements to the right are not LeftOf
		private int partition(int left, int right) {
			// Assigns the rightmost point to be the pivot
			Point pivot = points.get(right);
			
			// Tracks the point that would be next swapped
			int i = left - 1;
			
			for (int j = left; j < right; ++j) {
				// Checks if the current point is left of the line whose endpoints are
				// the anchor point and the pivot point - if not, then swap points
				if (!leftOf(anchorPoint, pivot, points.get(j))) {
					++i;
					swap(i, j);
				}
			}
			
			// Final swap to place the pivot point at the correct index in the list
			swap(i + 1, right);
			return i + 1;
		}
		
		// Swap points in the list
		private void swap(int i, int j) {
			Point temp = points.get(i);
			points.set(i, points.get(j));
			points.set(j, temp);
		}
		
		// Quick sort implementation using LeftOf instead of values
		// This is one of the most interesting exercises I've done in coding so far
		private void quickSort(int left, int right) {
			if (left < right) {
				int partitionIndex = partition(left, right);
				
				quickSort(left, partitionIndex - 1);
				quickSort(partitionIndex + 1, right);
			}
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