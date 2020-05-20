package objects;

import java.util.ArrayList;
import java.util.List;

import constants.Owner;

public class RouteManager extends ArrayList<objects.Route> {

	private static RouteManager instance = null;

	public static boolean cheminsDoubles = false;

	private RouteManager() {

	}

	public static RouteManager getInstance() {
		if (instance == null) {
			instance = new RouteManager();
		}
		return instance;
	}

	public Route getRoute(String ville1, String ville2) {
		for (Route r : this) {
			if (r.isBetween(ville1, ville2)) {
				return r;
			}
		}
		return null;
	}

	public void setRouteOwner(String ville1, String ville2, int newOwner, int originOwner) {
		getRoute(ville1, ville2).setOwner(newOwner, originOwner);
	}

	public List<String> getVoisins(String ville, boolean includeMe) {
		List<String> v = new ArrayList<>();

		for (Route r : iterate(includeMe)) {
			if (r.contains(ville) && r.getOwner().contains(Owner.NEUTRAL)
					|| includeMe && r.getOwner().contains(Owner.ME)) {
				v.add(r.getOppose(ville));
			}
		}

		return v;
	}

	public static boolean containsCity(List<Route> routes, String ville) {
		for (Route r : routes) {
			if (r.contains(ville)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Méthode à appeler pour itérer sur toutes les routes neutres
	 * 
	 * @return
	 */
	public List<Route> iterate(boolean includeMe) {
		List<Route> l = new ArrayList<>();
		for (Route r : this) {
			if (r.getOwner().contains(Owner.NEUTRAL) || includeMe && r.getOwner().contains(Owner.ME)) {
				l.add(r);
			}
		}

		return l;
	}

	public int getgain() {
		int g = 0;
		for (Route r : this) {
			if (r.getOwner().contains(Owner.ME)) {
				g += r.getrealgain();
			}
		}
		return g;

	}
}
