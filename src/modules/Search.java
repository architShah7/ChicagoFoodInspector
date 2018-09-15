package modules;

import java.util.ArrayList;

/**
 * Searchs for items in an arraylist
 * 
 * @author Debonair Coders
 */
public class Search {
	/**
	 * Linear Search operation
	 * @param array The array to search for items.
	 * @param searcher The searcher to match the items.
	 * @return The items.
	 */
	public static <T> ArrayList<T> search(ArrayList<T> array, Searcher<T> searcher) {
		ArrayList<T> result = new ArrayList<T>();
		
		for (T t : array)
			if (searcher.matches(t))
				result.add(t);
		
		return result;
	}
}