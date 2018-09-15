package modules;

/**
 * Interface to compare two objects.
 * 
 * @author Debonair Coders
 * @param <T> The type of object to compare.
 */
public interface Comparator<T> {
	/**
	 * Compare two objects
	 * 
	 * @param a The first object
	 * @param b The second object
	 * @return -1 : a < b, 0 : a = b, 1 : a > b
	 */
	public int compare(T a, T b);
}
