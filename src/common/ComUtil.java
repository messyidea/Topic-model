package common;

import java.util.ArrayList;
import java.util.HashSet;

public class ComUtil {
	
	public static void getTop(float[] array, ArrayList<Integer> rankList, int cnt) {
		int p = 0;
		HashSet<Integer> scanned = new HashSet<Integer>();
		double max = Double.MIN_VALUE;
		for (int i = 0; i < cnt && i < array.length; ++i) {
			max = Double.MIN_VALUE;
			p = -1;
			for (int j = 0; j < array.length; ++j) {
				if (!scanned.contains(j)) {
					if (array[j] > max) {
						max = array[j];
						p = j;
					}
				}
			}
			if (!scanned.contains(p)) {
				rankList.add(p);
				scanned.add(p);
			}
		}
	}
	
	public static void getTop(ArrayList<Float>  array, ArrayList<Integer> rankList, int cnt) {
		int p = 0;
		HashSet<Integer> scanned = new HashSet<Integer>();
		double max = Double.MIN_VALUE;
		for (int i = 0; i < cnt && i < array.size(); ++i) {
			max = Double.MIN_VALUE;
			p = -1;
			for (int j = 0; j < array.size(); ++j) {
				if (!scanned.contains(j)) {
					if (array.get(j) > max) {
						max = array.get(j);
						p = j;
					}
				}
			}
			if (!scanned.contains(p)) {
				rankList.add(p);
				scanned.add(p);
			}
		}
	}
	
}
