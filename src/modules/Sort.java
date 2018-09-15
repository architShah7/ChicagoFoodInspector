package modules;

import java.util.ArrayList;

/**
 * used to implement sorting module
 * @author Debonair Coders 
 */
 
public class Sort {
    /**
	 * used to initialize quicksort
	 * @param f array that needs to be sorted
	 * @param comparator comparator object that controls sorting order
	 */
	public static <T> void Quick(ArrayList<T> f, Comparator<T> comparator) {
		Quick(f, 0, f.size() - 1, comparator);
	}
	
	/**
	 * three way quicksort implementation
	 * @param f array that needs to be sorted
	 * @param lo lower bound of the array that needs to be sorted
	 * @param hi higher bound of the array that needs to be sorted
	 * @param comparator comparator object that controls sorting order
	 */
	private static <T> void Quick(ArrayList<T> f, int lo, int hi, Comparator<T> comparator) {
		if (hi <= lo)
			return;

		int lt = lo, i = lo + 1, gt = hi;

		T v = f.get(lo);

		while (i <= gt) {
			int cmp = comparator.compare(f.get(i), v);

			if (cmp < 0)
				swap(f, lt++, i++);
			else if (cmp > 0)
				swap(f, i, gt--);
			else
				i++;
		}

		Quick(f, lo, lt - 1, comparator);
		Quick(f, gt + 1, hi, comparator);
	}

    /**
	 * swaps two elements in an array
	 * @param f an array that contains two elements that need to be swapped
	 * @param i index of element that needs to be swapped
	 * @param j other index of element that needs to be swapped
	 */
	private static <T> void swap(ArrayList<T> f, int i, int j) {
		T t = f.get(i);
		f.set(i, f.get(j));
		f.set(j, t);
	}

}
