package net.bpelunit.suitegenerator.util;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class Copy {

	/**
	 * Takes any collection that stores T and fills it with the content of the second collection, which needs to contain anything that copies to T. Returns the
	 * filled collection.
	 * 
	 * @param dest
	 * @param src
	 * @return
	 */
	public static <T, E extends Collection<T>> E deepCopy(E dest, Collection<? extends Copyable<T>> src) {
		for (Copyable<T> c : src) {
			dest.add(c.copy());
		}
		return dest;
	}

	public static <T, E> Map<E, Collection<T>> deepCopyValues(Map<E, Collection<T>> dest, Map<E, Collection<Copyable<T>>> src) {
		for (E e : src.keySet()) {
			Collection<? extends Copyable<T>> c = src.get(e);
			Collection<T> neu = deepCopy(new LinkedList<T>(), c);
			dest.put(e, neu);
		}
		return dest;
	}

}
