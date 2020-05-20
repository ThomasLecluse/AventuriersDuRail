package objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chemin {
	private String villeDepart;
	private String villeArrivee;
	private int cout = 0;
	private int gain = 0;
	private float risk = 0f;
	private List<Route> routes;

	protected Chemin() {
		// Seulement pour l'héritage
	}

	public Chemin(String villeDepart, String villeArrivee, int cout, int gain, float risk, String routes) {
		this.villeDepart = villeDepart;
		this.villeArrivee = villeArrivee;
		this.cout = cout;
		this.gain = gain;
		this.risk = risk;

		this.routes = new ArrayList<>();

		String ville1 = "";
		String ville2 = "";
		for (String s : routes.split("-")) {
			if (!ville1.equals("")) {
				ville2 = s;
				this.routes.add(RouteManager.getInstance().getRoute(ville1, ville2));
			}
			ville1 = s;
		}
	}

	public String getVilleDepart() {
		return villeDepart;
	}

	public void setVilleDepart(String villeDepart) {
		this.villeDepart = villeDepart;
	}

	public String getVilleArrivee() {
		return villeArrivee;
	}

	public void setVilleArrivee(String villeArrivee) {
		this.villeArrivee = villeArrivee;
	}

	public int getCout() {
		return cout;
	}

	public int getGain() {
		return gain;
	}

	public float getRisk() {
		return risk;
	}

	public List<Route> getRoutes() {
		return routes;
	}

	public void setCout(int cout) {
		this.cout = cout;
	}

	public void setGain(int gain) {
		this.gain = gain;
	}

	public void setRisk(float risk) {
		this.risk = risk;
	}

	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}

	public float getRatio() {
		return (float) getCout() / getGain() * getRisk();
	}

	public int getLongueur() {
		int l = 0;
		for (Route r : getRoutes()) {
			l += r.getCout();
		}

		return l;
	}

	public int getNbTroncons() {
		return getRoutes().size();
	}

	public boolean passesThrough(List<String> etapes) {
		if (etapes.isEmpty()) {
			return true;
		}

		boolean found = false;
		for (String ve : etapes) {
			found = false;
			for (Route r : getRoutes()) {
				if (r.contains(ve)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}

		return found;
	}

	public String getStringChemin() {
		String previous = getVilleDepart();
		StringBuilder sb = new StringBuilder(previous);

		for (Route r : routes) {
			String str = r.toString();
			str = str.replace(" - ", "").replace(previous, ""); // reste le nom de l'autre ville

			sb.append(" -> ").append(str);
			previous = str;
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		String previous = getVilleDepart();
		StringBuilder sb = new StringBuilder(previous);

		Map<String, Integer> mapColor1 = new HashMap<>();
		Map<List<String>, Integer> mapColor2 = new HashMap<>();
		boolean differentPossibility = false;
		// Routes empruntées + couleurs
		for (Route r : routes) {
			String str = r.toString();
			str = str.replace(" - ", "").replace(previous, ""); // reste le nom de l'autre ville
			sb.append(", ").append(str);
			previous = str;

			// Couleur 1
			String color1 = r.getCouleur().get(0);
			if (!mapColor1.containsKey(color1)) {
				mapColor1.put(color1, 0);
			}
			int nb1 = mapColor1.get(color1) + r.getCout();
			mapColor1.remove(color1);
			mapColor1.put(color1, nb1);

			// Couleur 2
			if (r.getCouleur().size() > 1) {
				mapColor2.put(r.getCouleur(), r.getCout());
				differentPossibility = true;
			}
		}

		// Cout / gain / risque / ratio
		sb.append("\n").append("- Coût total : ").append(getCout());
		sb.append("\n").append("- Gain total : ").append(getGain());
		sb.append("\n").append("- Risque total : ").append(getRisk());
		sb.append("\n").append("- Ratio du chemin : ").append(getRatio());

		// Cout en couleur
		sb.append("\nCouleur nécessaires : \n");
		for (Map.Entry<String, Integer> entry : mapColor1.entrySet()) {
			sb.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
		}

		if (differentPossibility) {

			StringBuilder altColorSB = new StringBuilder();
			for (Map.Entry<List<String>, Integer> entry : mapColor2.entrySet()) {
				if (entry.getValue() > 0) {
					altColorSB.append("Remplace : ").append(entry.getValue()).append(entry.getKey().get(0))
							.append(" par ").append(entry.getValue()).append(entry.getKey().get(1)).append("\n");
				}
			}

			if (altColorSB.length() > 0) {
				sb.append("\nCouleurs alternatives : \n").append(altColorSB);
			}
		}

		return sb.toString();
	}
}
