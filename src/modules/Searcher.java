package modules;

/**
 * Interface to match items while searching.
 * 
 * @author Debonair Coders
 * @param <T> The type of item to search.
 */
public interface Searcher<T> {
	public boolean matches(T a);
}
