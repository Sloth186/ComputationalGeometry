public class hw1 {
	public static void main(String[] args) {
		double[][] polygon1 = {{0, 0}, {4, 0}, {4, 6}};
		double[][] polygon2 = {{0, 0}, {8, 0}, {8, 3}, {0, 3}};
		double[][] polygon3 = {{0, 0}, {6, 0}, {4, 2}, {2, 2}};
		double[][] polygon4 = {{0, 0}, {1, 0}, {1, 2}, {2, 2}, {3, 0}, {3, 3}, {1, 4}, {0, 4}};
		
		System.out.println("Checking area of a 3-gon with vertices (0,0), (4,0), (4,6), expecting area of 12");
		System.out.println("\tCalculated area: " + calculateArea(polygon1));
		
		System.out.println("Checking area of a 4-gon with vertices (0,0), (8,0), (8,3), (0,3), expecting area of 24");
		System.out.println("\tCalculated area: " + calculateArea(polygon2));
		
		System.out.println("Checking area of a 4-gon with vertices (0,0), (6,0), (4,2), (2,2), expecting area of 8");
		System.out.println("\tCalculated area: " + calculateArea(polygon3));
		
		System.out.println("Checking area of an 8-gon with vertices (0,0), (1,0), (1,2), (2,2), (3,0), (3,3), (1,4), (0,4), expecting area of 8");
		System.out.println("\tCalculated area: " + calculateArea(polygon4));
	}
	
	public static double calculateArea(double[][] polygon) {
		double area = 0;
		for (int i = 1; i < polygon.length; ++i)
			area += (polygon[i][0] * polygon[i - 1][1]) - (polygon[i - 1][0] * polygon[i][1]);
		area = Math.abs(area);
		area /= 2.;
		return area;
	}
}