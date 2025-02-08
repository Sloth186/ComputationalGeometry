import java.util.*;

public class FlipGraphAlgorithm {
	public static void main(String[] args) {
		final int NUM_POINTS = 5;
		final double BOUND = 10.;
		
		final Random rg = new Random();
		
		Set set = new Set();
		
		// Set of points that causes a bug
		// {{4.84, 0.70}, {8.28, 1.03}, {5.48, 8.00}, {5.98, 1.29}, {5.07, 8.49}, {2.13, 8.46}}
		/*set.add(4.84,0.70);
		set.add(8.28,1.03);
		set.add(5.48,8.00);
		set.add(5.98,1.29);
		set.add(5.07,8.49);
		set.add(2.13,8.46);*/
		
		// Set of points that causes a bug - appears to be related to modifying triangles when making flip edge??
		// {{7.73, 0.13}, {2.26, 1.27}, {8.75, 3.53}, {7.59, 7.86}, {2.53, 5.35}, {0.15, 6.34}, {6.37, 9.46}}
		/*set.add(7.73, 0.13);
		set.add(2.26, 1.27);
		set.add(8.75, 3.53);
		set.add(7.59, 7.86);
		set.add(2.53, 5.35);
		set.add(0.15, 6.34);
		set.add(6.37, 9.46);*/
		
		// Test for set of points from a homework
		/*set.add(0,0);
		set.add(1,4);
		set.add(3,5);
		set.add(5,4);
		set.add(6,0);*/
		
		// Regular polygon, slightly angled (coordinates generated from Mathematica)
		/*set.add(0.952669, 6.96701);
		set.add(1.87857, 1.7586);
		set.add(5.62004, 9.45708);
		set.add(7.11818,1.0297);
		set.add(9.43054, 5.78762);*/
		
		// A nice set of points (flip graph may not be fully accurate, however)
		set.add(7.57,1.23);
		set.add(9.50,2.58);
		set.add(8.87,9.78);
		set.add(4.43,6.99);
		set.add(2.37,8.72);
		set.add(4.12,5.02);
		set.add(4.61,2.52);
		
		/*for (int i = 0; i < NUM_POINTS; ++i)
			set.add(rg.nextDouble(BOUND), rg.nextDouble(BOUND));*/
		System.out.println("Set: " + set);
		
		// Prints output for copying to Mathematica and generate visualizations
		// Contains points and the edges & triangles of the graham scan triangulation
		// Also contains data about convex quadrilaterals present in the gst, but
		// currently is verified manually with the coder's greatest tools: eyes.
		/*System.out.printf("S=%s;\nedgesIndices=%s;\ntrianglesIndices=%s;\nconvexQuadrilateralsIndices=%s;\n\n",
			  set, set.edges(0), set.triangles(0), set.convexQuadrilaterals(0));*/
		
		set.constructTriangulations();
		
		System.out.println();
		
		System.out.println("TRIANGULATIONS: \n");
		for (int i = 0; i < set.triangulationsEdges.size(); ++i)
			System.out.printf("S=%s;\nedgesIndices=%s;\ntrianglesIndices=%s;\nconvexQuadrilateralsIndices=%s;\n\n",
				  set, set.edges(i), set.triangles(i), set.convexQuadrilaterals(i));
		
		int i;
		System.out.println("FOR COPY PASTE: \n");
		StringBuilder sb = new StringBuilder();
		sb.append("S=").append(set).append(";\nedgesIndicesMass={");
		for (i = 0; i < set.triangulationsEdges.size() - 1; ++i)
			sb.append(set.edges(i)).append(",");
		sb.append(set.edges(i)).append("};\nconnections=").append(set.connections()).append(";\n");
		
		System.out.println(sb);
	}
	
	// Return the deep copy of a 2D array (no shared references)
	public static int[][] deepCopy(int[][] original) {
		int[][] copy = new int[original.length][];
		for (int i = 0; i < original.length; ++i)
			copy[i] = Arrays.copyOf(original[i], original[i].length);
		return copy;
	}
	
	/**
	 * Given line ab, return:
	 * 0 if c is left of
	 * 1 if c is collinear
	 * 2 if c is right of
	 */
	private static int relative(Point a, Point b, Point c) {
		double signedArea = ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));
		if (signedArea > 0) return 0;
		else if (signedArea < 0) return 2;
		else return 1;
	}
	
	// Returns whether point c is left to the line ab
	private static boolean isLeft(Point a, Point b, Point c) {
		return relative(a, b, c) == 0;
	}
	
	// Returns whether point c is collinear to the line ab
	private static boolean isCollinear(Point a, Point b, Point c) {
		return relative(a, b, c) == 1;
	}
	
	// Returns whether point c is right to the line ab
	private static boolean isRight(Point a, Point b, Point c) {
		return relative(a, b, c) == 2;
	}
	
	// A small class that represents a point in 2D space
	private static class Point {
		public double x, y;
		
		public Point(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public String toString() {
			return String.format("{%.2f, %.2f}", x, y);
		}
	}
	
	// A class that represents a set of points in 2D space
	private static class Set {
		private final List<Point> points;
		private int numPoints;
		private Point anchorPoint;
		private List<Integer> triangulationsHashes;
		private List<int[][]> triangulationsEdges;
		private List<int[][]> triangulationsTriangles;
		private List<int[][]> triangulationsConvexQuadrilaterals;
		private List<int[]> triangulationsFlippableEdges;
		private List<int[]> triangulationsConnections;
		
		public Set() {
			points = new ArrayList<>();
			numPoints = 0;
			anchorPoint = null;
			triangulationsHashes = new ArrayList<>();
			triangulationsEdges = new ArrayList<>();
			triangulationsTriangles = new ArrayList<>();
			triangulationsConvexQuadrilaterals = new ArrayList<>();
			triangulationsFlippableEdges = new ArrayList<>();
			triangulationsConnections = new ArrayList<>();
		}
		
		public void add(double x, double y) {
			Point temp = new Point(x, y);
			
			if (anchorPoint == null) {
				// First point of the set
				anchorPoint = temp;
				points.add(temp);
			} else if (temp.y < anchorPoint.y || (temp.y == anchorPoint.y && temp.x > anchorPoint.x)) {
				// Check if the point should be the new anchor point
				anchorPoint = temp;
				points.addFirst(anchorPoint);
			} else {
				points.add(temp);
			}
			
			++numPoints;
		}
		
		private void add(Point point) {
			add(point.x, point.y);
		}
		
		// Credit to GeeksForGeeks for guidance
		private void sort() {
			quickSort(1, numPoints - 1);
		}
		
		// Quick sort implementation using isLeft comparisons
		private void quickSort(int left, int right) {
			if (left < right) {
				int partitionIndex = partition(left, right);
				
				quickSort(left, partitionIndex - 1);
				quickSort(partitionIndex + 1, right);
			}
		}
		
		// Partition the array and pivots in place such that given the line
		// from the anchor to the pivot, elements on the left are LeftOf
		// and elements on the right are not LeftOf
		private int partition(int left, int right) {
			// Assign the rightmost point to be the pivot
			Point pivot = points.get(right);
			
			// Track the point that would next be swapped
			int i = left - 1;
			for (int j = left; j < right; ++j) {
				// Check if the current point is left of the line from the anchor
				// to the pivot - if not, then swap points
				if (!isLeft(anchorPoint, pivot, points.get(j))) {
					++i;
					swap(i, j);
				}
			}
			
			// Final swap to position the pivot at its correct index in the list
			swap(i + 1, right);
			
			// Return index of the pivot, which further partitions the list
			return i + 1;
		}
		
		// Swap two points in the list
		private void swap(int i, int j) {
			Point temp = points.get(i);
			points.set(i, points.get(j));
			points.set(j, temp);
		}
		
		private boolean isConvexQuadrilateral() {
			sort();
			Point[] pointsArray = toArray();
			
			if (pointsArray.length != 4) return false;
			
			List<Point> hull = new ArrayList<>();
			hull.add(pointsArray[0]);
			hull.add(pointsArray[1]);
			hull.add(pointsArray[2]);
			
			for (int i = 3; i < pointsArray.length; ++i) {
				hull.add(pointsArray[i]);
				
				int j = hull.size() - 2;
				while (!isLeft(hull.get(j - 1), hull.get(j), hull.get(j + 1)))
					hull.remove(j--);
			}
			
			return hull.size() == 4;
		}
		
		private void grahamScanTriangulation() {
			if (triangulationsEdges.isEmpty()) {
				sort();
				
				// Initialize the list of edges and triangles
				List<int[]> edges = new ArrayList<>();
				List<int[]> triangles = new ArrayList<>();
				
				edges.add(new int[]{0, 1});
				for (int i = 2; i < numPoints; ++i) {
					// Add an edge from the anchor to each other point
					edges.add(new int[]{0, i});
					// Add an edge between each consecutive point
					edges.add(new int[]{i - 1, i});
					// Add a triangle with vertices anchor, i-1, and i
					triangles.add(new int[]{0, i - 1, i});
				}
				
				// Initialize the hull
				List<Integer> hull = new ArrayList<>();
				hull.add(0);
				hull.add(1);
				hull.add(2);
				
				// Build the hull from the sorted points, adding edges for the triangulation
				for (int i = 3; i < numPoints; ++i) {
					// Add the next point to the hull
					hull.add(i);
					
					// Check if any reflex vertices result from the recent addition
					int j = hull.size() - 2;
					while (!isLeft(points.get(hull.get(j - 1)), points.get(hull.get(j)), points.get(hull.get(j + 1)))) {
						// Check if the recent point is right of the 2nd most recent hull edge
						// - if so, we add a new edge and a new triangle to the triangulation
						if (!isLeft(points.get(hull.get(j - 1)), points.get(hull.get(j)), points.get(i))) {
							edges.add(new int[]{hull.get(j - 1), i});
							triangles.add(new int[]{hull.get(j - 1), hull.get(j), i});
						}
						
						// Remove the reflex vertex from the hull
						hull.remove(j--);
					}
				}
				
				triangulationsEdges.add(edges.toArray(new int[0][]));
				triangulationsTriangles.add(triangles.toArray(new int[0][]));
				triangulationsHashes.add(Arrays.deepHashCode(triangles.toArray(new int[0][])));
			}
		}
		
		// Check for convex quadrilaterals in a triangulation
		private int[][] findConvexQuadrilaterals(int triangulationIndex) {
			ArrayList<int[]> convexQuadrilaterals = new ArrayList<>();
			ArrayList<Integer> flippableEdges = new ArrayList<>();
			// Get triangulation as an array
			int[][] triangulationTriangles = triangulationsTriangles.get(triangulationIndex);
			int[][] triangulationEdges = triangulationsEdges.get(triangulationIndex);
			
			int sharedVertexA, sharedVertexB, i, j, k, l;
			// Iterate through each triangle in the triangulation
			for (i = 0; i < triangulationTriangles.length; ++i) {
				// Iterate through each triangle after the above triangle
				for (j = i + 1; j < triangulationTriangles.length; ++j) {
					// Save what vertices are shared between the two triangles
					sharedVertexA = -1;
					sharedVertexB = -1;
					
					// Iterate through each vertex of the first triangle
					for (k = 0; sharedVertexB == -1 && k < 3; ++k) {
						// Iterate through each vertex of the second triangle
						for (l = 0; sharedVertexB == -1 && l < 3; ++l) {
							// If both triangles share a vertex, record it
							if (triangulationTriangles[i][k] == triangulationTriangles[j][l]) {
								if (sharedVertexA == -1) {
									// If it is the first shared vertex found, save to A
									sharedVertexA = triangulationTriangles[i][k];
									l = 3;
								} else {
									// If it is the second found, save to B
									sharedVertexB = triangulationTriangles[i][k];
									
									// Construct quadrilateral (indices)
									for (k = 0; triangulationTriangles[i][k] == sharedVertexA || triangulationTriangles[i][k] == sharedVertexB; ++k);
									for (l = 0; triangulationTriangles[j][l] == sharedVertexA || triangulationTriangles[j][l] == sharedVertexB; ++l);
									
									// Store the quadrilateral as a set of points and use the Graham Scan algorithm to identify the
									// convex hull, and check if the convex hull consists of exactly the same points as the set itself
									Set quadrilateral = new Set();
									quadrilateral.add(points.get(triangulationTriangles[i][k]));
									quadrilateral.add(points.get(triangulationTriangles[j][l]));
									quadrilateral.add(points.get(sharedVertexA));
									quadrilateral.add(points.get(sharedVertexB));
									
									// The quadrilateral's convex hull is the quadrilateral itself, hence it is a convex quadrilateral
									if (quadrilateral.isConvexQuadrilateral()) {
										int[] quadrilateralIndices = new int[]{triangulationTriangles[i][k], triangulationTriangles[j][l], sharedVertexA, sharedVertexB};
										Arrays.sort(quadrilateralIndices);
										convexQuadrilaterals.add(quadrilateralIndices);
										for (k = 0; k < triangulationEdges.length; ++k) {
											if (triangulationEdges[k][0] == sharedVertexA && triangulationEdges[k][1] == sharedVertexB) {
												flippableEdges.add(k);
												k = triangulationEdges.length;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			
			triangulationsConvexQuadrilaterals.add(triangulationIndex, convexQuadrilaterals.toArray(new int[0][]));
			triangulationsFlippableEdges.add(triangulationIndex, flippableEdges.stream().mapToInt(num->num).toArray());
			return triangulationsConvexQuadrilaterals.get(triangulationIndex);
		}
		
		public void constructTriangulations() {
			if (triangulationsEdges.isEmpty())
				grahamScanTriangulation();
			
			Queue<Integer> triangulationsQueue = new LinkedList<>();
			triangulationsQueue.add(0);
			
			int triangulationIndex;
			int[][] triangles;
			int[][] edges;
			int[][] convexQuadrilateralIndices;
			int[] flippableEdges;
			int sharedVertexA, sharedVertexB, unsharedVertexA, unsharedVertexB;
			while (!triangulationsQueue.isEmpty()) {
				triangulationIndex = triangulationsQueue.remove();
				convexQuadrilateralIndices = findConvexQuadrilaterals(triangulationIndex);
				flippableEdges = triangulationsFlippableEdges.get(triangulationIndex);
				
				for (int i = 0; i < convexQuadrilateralIndices.length; ++i) {
					triangles = deepCopy(triangulationsTriangles.get(triangulationIndex));
					edges = deepCopy(triangulationsEdges.get(triangulationIndex));
					
					if (convexQuadrilateralIndices[i][0] == edges[flippableEdges[i]][0]) {
						sharedVertexA = convexQuadrilateralIndices[i][0];
						sharedVertexB = convexQuadrilateralIndices[i][2];
						unsharedVertexA = convexQuadrilateralIndices[i][1];
						unsharedVertexB = convexQuadrilateralIndices[i][3];
					} else {
						sharedVertexA = convexQuadrilateralIndices[i][1];
						sharedVertexB = convexQuadrilateralIndices[i][3];
						unsharedVertexA = convexQuadrilateralIndices[i][0];
						unsharedVertexB = convexQuadrilateralIndices[i][2];
					}
					
					for (int[] triangle : triangles) {
						if (triangle[0] == sharedVertexA && triangle[1] == sharedVertexB) {
							// Triangle is left of shared edge
							triangle[0] = unsharedVertexA;
						} else if (triangle[0] == sharedVertexA && triangle[2] == sharedVertexB) {
							if (triangle[1] == unsharedVertexA) {
								// Triangle is right of shared edge
								triangle[2] = unsharedVertexB;
							} else {
								// Triangle is left of shared edge
								triangle[0] = unsharedVertexA;
							}
						} else if (triangle[1] == sharedVertexA && triangle[2] == sharedVertexB) {
							// Triangle is right of shared edge
							triangle[2] = unsharedVertexB;
						}
						
						Arrays.sort(triangle);
					}
					
					edges[flippableEdges[i]][0] = unsharedVertexA;
					edges[flippableEdges[i]][1] = unsharedVertexB;
					
					int match = -1;
					int newHash = Arrays.deepHashCode(triangles);
					for (int hash = 0; match == -1 && hash < triangulationsHashes.size(); ++hash)
						if (triangulationsHashes.get(hash) == newHash)
							match = hash;
					
					if (match == -1) {
						triangulationsHashes.add(newHash);
						triangulationsTriangles.add(triangles);
						triangulationsEdges.add(edges);
						match = triangulationsTriangles.size() - 1;
						triangulationsQueue.add(match);
					}
					
					if (triangulationIndex != match) {
						int[] currentConnection = new int[]{triangulationIndex, match};
						
						boolean isNew = true;
						Arrays.sort(currentConnection);
						for (int[] connection : triangulationsConnections)
							if (connection[0] == currentConnection[0] && connection[1] == currentConnection[1])
								isNew = false;
						if (isNew)
							triangulationsConnections.add(currentConnection);
					}
				}
			}
		}
		
		// Solely for grabbing output to copy over to Mathematica and generate visualizations
		public int[][] getTriangulationEdges(int i) {
			if (triangulationsEdges.isEmpty())
				grahamScanTriangulation();
			
			if (i < triangulationsEdges.size())
				return triangulationsEdges.get(i);
			else
				return null;
		}
		
		// Solely for grabbing output to copy over to Mathematica and generate visualizations
		public int[][] getTriangulationTriangles(int i) {
			if (triangulationsTriangles.isEmpty())
				grahamScanTriangulation();
			
			if (i < triangulationsTriangles.size())
				return triangulationsTriangles.get(i);
			else
				return null;
		}
		
		// Solely for grabbing output to copy over to Mathematica and generate visualizations
		public String edges(int i) {
			int[][] edges = getTriangulationEdges(i);
			if (edges == null)
				return "{null}";
			else
				return Arrays.deepToString(edges).replaceAll("\\[", "{").replaceAll("]", "}");
		}
		
		// Solely for grabbing output to copy over to Mathematica and generate visualizations
		public String triangles(int i) {
			int[][] triangles = getTriangulationTriangles(i);
			if (triangles == null)
				return "{null}";
			else
				return Arrays.deepToString(triangles).replaceAll("\\[", "{").replaceAll("]", "}");
		}
		
		// Solely for grabbing output to verify proper code execution
		public String convexQuadrilaterals(int i) {
			int[][] convexQuadrilaterals = findConvexQuadrilaterals(i);
			if (convexQuadrilaterals == null)
				return "{null}";
			else
				return Arrays.deepToString(findConvexQuadrilaterals(i)).replaceAll("\\[", "{").replaceAll("]", "}");
		}
		
		// Solely for grabbing output to copy over to Mathematica and indicate connections between triangulations
		public String connections() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("{");
			for (int[] edge : triangulationsConnections)
				sb.append(edge[0]).append("\\[UndirectedEdge]").append(edge[1]).append(",");
			sb.deleteCharAt(sb.length() - 1).append("}");
			
			return sb.toString();
		}
		
		private Point[] toArray() {
			return points.toArray(new Point[numPoints]);
		}
		
		// Print the points of the set
		public String toString() {
			StringBuilder sb = new StringBuilder();
			
			sb.append("{");
			for (Point point : points)
				sb.append(point.toString()).append(", ");
			sb.delete(sb.length() - 2, sb.length()).append("}");
			
			return sb.toString();
		}
	}
}