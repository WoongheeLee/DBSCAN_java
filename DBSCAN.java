package dbscan;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DBSCAN {
	public static final int N = 2048;
	public static final int D = 8;
	
	public static int[] getTrueLabel(String fileName) {
		int[] label = new int[N];
		int n = 0;
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			String l;
			while ((l = inputStream.readLine()) != null) {
				if (n > 3 && l.trim().length() == 1) {
					System.out.println((n-4)+" "+l);
					label[n-4] = Integer.parseInt(l.trim());
				}
				n++;
			}
			
		} catch (IOException e) {
			System.err.println("getTrueLabel: "+e.getMessage());
			System.exit(1);
		}
		
		return label;
	}
	
	public static double[][] getData(String fileName) {
		double[][] vectors = new double[N][D];
		
		BufferedReader inputStream = null;
		try {
			inputStream = new BufferedReader(new FileReader(fileName));
			String l;
			int n = 0;
			while ((l = inputStream.readLine()) != null) {			
				String[] arr = l.trim().split(" ");
				int m = 0;
				for (int i = 0; i < arr.length; i++) {
					if (arr[i].length() > 1) 
						vectors[n][m++] = Double.parseDouble(arr[i]);
				}
				n++;
			}
		} catch (IOException e) {
			System.err.println("getData: "+e.getMessage());
			System.exit(1);
		}
		
		return vectors;
	}
	
	public static void printData(double[][] data) {
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++)
				System.out.print(data[i][j]+" ");
			System.out.println();
		}
	}
	
	public static double getDist (double[] A, double[] B) {
		double dist = 0d;
		for (int i = 0; i < A.length; i++) {
			dist += (A[i]-B[i])*(A[i]-B[i]);
		}
		return Math.sqrt(dist);
	}
	
	public static int[] getDBSCAN(double[][] vectors, double eps, int minPts) {
		int[] label = new int[N];
		// 0: undefined
		// -1: noise (border)
		// 1 ... : cluster number
		int C = 0; // cluster initializing
		
		for (int i = 0; i < vectors.length; i++) {
			if (label[i] != 0) 
				continue;
			
			ArrayList<Integer> neighbors = rangeQuery(vectors, i, eps);
			if (neighbors.size() < minPts) {
				label[i] = -1;
				continue;
			}
			
			C++;
			label[i] = C;
			ArrayList<Integer> seed = new ArrayList<Integer>();
			seed.addAll(neighbors);
			for (int j = 0; j < seed.size(); j++) {
				int Q = seed.get(j);
				if (label[Q] == -1)
					label[Q] = C;
				if (label[Q] != 0)
					continue;
				label[Q] = C;
				neighbors = rangeQuery(vectors, Q, eps);
				if (neighbors.size() >= minPts)
					seed.addAll(neighbors);
			}
		}
		
		return label;
	}
	
	public static ArrayList<Integer> rangeQuery(double[][] vectors, int Q, double eps) {
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		
		for (int i = 0; i < vectors.length; i++) {
			double[] P = vectors[i];
			if (getDist(P, vectors[Q]) <= eps && i!=Q)
				neighbors.add(i);
		}
		
		return neighbors;
	}
	
	public static void printLabel(int[] label) {
		for (int i = 0; i < label.length; i++)
			System.out.println(i+" "+label[i]);
	}
	
	public static double getAccuracy(int[] A, int[] B) {
		if (A.length != B.length) {
			System.out.println("A, B are not same length!");
			return -1d;
		}

		int count = 0;
		
		for (int i = 0; i < A.length; i++) {
			if (A[i] == B[i])
				count++;
		}
		
		return (double)count / (double)A.length;
	}
	
	public static void main(String[] args) {
		String file1 = "C:\\Users\\Woonghee\\workspace\\DBSCAN\\g2-txt\\g2-8-30.txt";
		String file2 = "C:\\Users\\Woonghee\\workspace\\DBSCAN\\g2-gt-pa\\g2-8-30-gt.pa";
		final double eps = 75.0;
		final int minPts = 3;
		
		int[] trueLabel = getTrueLabel(file2);
		
		double[][] vectors = getData(file1);
//		printData(vectors);

		// DBSCAN
		int[] label = getDBSCAN(vectors, eps, minPts);
//		printLabel(label);
		
		System.out.println(getAccuracy(label, trueLabel));
	}
}
