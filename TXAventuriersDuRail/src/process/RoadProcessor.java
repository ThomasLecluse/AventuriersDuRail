package process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import objects.Chemin;
import objects.Route;
import objects.RouteManager;

/**
 * Objet contenant toutes les méthodes de calculs
 * 
 * @author thomas
 */
public class RoadProcessor {
	private static List<Chemin> listeChemins = new ArrayList<>();
	private static Map<Chemin, Float> mapChemins = new HashMap<>();
	private static final int MAX_VALUE = 13;

	/**
	 * A partir d'une liste de chemins, crée et renvoie une Map des ratios associés
	 * à chaque chemins.
	 * 
	 * @param listeChemins : Chemins à calculer les ratios correspondants
	 * @param dep          : ville de départ
	 * @param arr          : ville d'arrivée
	 * @return Map des ratios correspondant aux chemins
	 */
	public static Map<Chemin, Float> mapRatiosChemin(List<Chemin> listeChemins, String dep, String arr) {
		Map<Chemin, Float> mapRes = new HashMap<>();

		for (Chemin c : listeChemins) {
			c.setVilleDepart(dep);
			c.setVilleArrivee(arr);
			mapRes.put(c, c.getRatio());
		}

		return mapRes;
	}

	/**
	 * Recherche le chemin ayant le meilleur ratio parmi la Map précisée
	 * 
	 * @param mapChemin : Map dans laquelle recherher le meilleur chemin
	 * @return le chemin le plus optimisé
	 */
	public static Chemin getOptChemin(Map<Chemin, Float> mapChemin) {
		float min = 100f;
		Chemin best = null;
		for (Map.Entry<Chemin, Float> entry : mapChemin.entrySet()) {
			if (entry.getValue() < min) {
				min = entry.getValue();
				best = entry.getKey();
			}
		}

		return best;
	}

	/**
	 * Cherche le meilleur chemin le plus court
	 * 
	 * @param mapChemin : Map dans laquelle recherher le meilleur chemin
	 * @return meilleur chemin le plus court
	 */
	public static Chemin getBestShortestChemin(Map<Chemin, Float> mapChemin) {
		int longueurMin = 100;
		Map<Chemin, Float> listPlusPetits = new HashMap<>();

		for (Map.Entry<Chemin, Float> entry : mapChemin.entrySet()) {
			int longueur = entry.getKey().getLongueur();
			if (longueur < longueurMin) {
				longueurMin = longueur;

				listPlusPetits.clear();
				listPlusPetits.put(entry.getKey(), entry.getKey().getRatio());
			} else if (longueur == longueurMin) { // On ajoute tous les plus petits
				listPlusPetits.put(entry.getKey(), entry.getKey().getRatio());
			}
		}

		// On renvoie le plus petit possédant le meilleur ratio
		return getOptChemin(listPlusPetits);
	}

	public static final List<Chemin> getChemins() {
		return listeChemins;
	}

	public static final Map<Chemin, Float> getMapChemin() {
		return mapChemins;
	}

	public static void chercher(String villeDepart, String villeArrivee) {
		chercherAvecEtapes(villeDepart, villeArrivee, new ArrayList<>());
	}

	public static void chercherAvecEtapes(String villeDepart, String villeArrivee, List<String> etapes) {
		listeChemins.clear();
		mapChemins.clear();

		trouveChemins(villeDepart, villeArrivee, villeDepart, 0, 0, 0f);

		listeChemins.removeIf(c -> !c.passesThrough(etapes));
		mapChemins = mapRatiosChemin(listeChemins, villeDepart, villeArrivee);
	}

	/**
	 * Etudie le niveau de risque selon l'échelle définie dans les constantes
	 * 
	 * @param c
	 * @return valeur du niveau de risque à étaloner sur l'échelle RiskLevel
	 */
	public static int getRiskLevel(Chemin c) {
		return (int) (c.getRisk() / c.getNbTroncons() * 100);
	}

	/**
	 * Remplis la liste des chemins avec toutes les possibilités entre la ville de
	 * départ et la ville d'arrivée
	 * 
	 * @param villeDepart
	 * @param villeArrivee
	 * @param routesEmpruntees
	 * @param cout
	 * @param pts
	 * @param risque
	 */
	private static void trouveChemins(String villeDepart, String villeArrivee, String routesEmpruntees, int cout,
			int pts, float risque) {
		if (villeDepart.equals(villeArrivee)) {
			listeChemins.add(new Chemin("", "", cout, pts, risque, routesEmpruntees));
		} else {
			for (String v : RouteManager.getInstance().getVoisins(villeDepart, true)) {
				Route routeCorrespondante = RouteManager.getInstance().getRoute(villeDepart, v);
				if (routesEmpruntees.split("-").length <= MAX_VALUE && !routesEmpruntees.contains(v)) {
					trouveChemins(v, villeArrivee, routesEmpruntees + "-" + v, cout + routeCorrespondante.getCout(),
							pts + routeCorrespondante.getGain(), risque + routeCorrespondante.getRisk());
				}
			}
		}
	}

}
