package process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tools {

	/**
	 * Duplique les éléments d'une liste dans une nouvelle liste
	 * 
	 * @param liste : liste à dupliquer
	 * @return nouvelle liste contenant tous les éléments de la liste passée en
	 *         paramètre
	 */
	public static <T> List<T> cloneListe(List<T> liste) {
		List<T> l = new ArrayList<>();

		for (T elem : liste) {
			l.add(elem);
		}

		return l;
	}

	public static <K, V> Map<K, V> cloneMap(Map<K, V> map) {
		Map<K, V> m = new HashMap<>();

		for (Map.Entry<K, V> entry : map.entrySet()) {
			m.put(entry.getKey(), entry.getValue());
		}

		return m;
	}
}
