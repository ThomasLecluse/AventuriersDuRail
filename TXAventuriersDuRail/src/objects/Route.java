package objects;

import java.util.ArrayList;
import java.util.List;

import constants.Owner;

public class Route {

	private int longueur;
	private List<String> couleur;
	private String ville1;
	private String ville2;
	private List<Integer> owner;

	public Route(int longueur, List<String> couleur, String ville1, String ville2) {
		this.longueur = longueur;
		this.couleur = couleur;
		this.ville1 = ville1;
		this.ville2 = ville2;

		owner = new ArrayList<>();
		owner.add(Owner.NEUTRAL); // neutre par défaut
		if (couleur.size() == 2 && RouteManager.cheminsDoubles) {
			owner.add(Owner.NEUTRAL);
		}
	}

	public List<Integer> getOwner() {
		return owner;
	}

	public void setOwner(int newOwner, int originOwner) {
		if (RouteManager.cheminsDoubles && couleur.size() == 2) { // Chemin double autorisé
			if (newOwner == Owner.NEUTRAL) {
				if (originOwner == Owner.ME) {
					owner.remove((Integer) Owner.ME);
					owner.add(newOwner);
				} else if (originOwner == Owner.OTHER) {
					owner.remove((Integer) Owner.OTHER);
					owner.add(newOwner);
				} else {
					owner.clear();
					owner.add(newOwner);
					owner.add(newOwner);
				}
			} else {
				if (owner.contains(Owner.NEUTRAL)) {
					owner.remove((Integer) Owner.NEUTRAL);
					owner.add(newOwner);
				}
			}
		} else {
			owner.clear();
			owner.add(newOwner);
		}
	}

	public int getLongueur() {
		return longueur;
	}

	public List<String> getCouleur() {
		return couleur;
	}

	public String getVille1() {
		return ville1;
	}

	public String getVille2() {
		return ville2;
	}

	/**
	 * Vérifie si la route est entre v1 et v2 (sans prendre en compte l'ordre)
	 * 
	 * @param v1 : ville de la route
	 * @param v2 : autre ville de la route
	 * @return true si la route relie les 2 villes
	 */
	public boolean isBetween(String v1, String v2) {
		return getVille1().equals(v1) && getVille2().equals(v2) || getVille1().equals(v2) && getVille2().equals(v1);
	}

	/**
	 * Cherche si une ville est une des extrémités de la route
	 * 
	 * @param ville : Ville à chercher
	 * @return true si la ville est en position 1 ou 2 de la route
	 */
	public boolean contains(String ville) {
		return getVille1().equals(ville) || getVille2().equals(ville);
	}

	/**
	 * Si la ville est contenue dans la route, retourne l'autre ville
	 * 
	 * @param ville
	 * @return
	 */
	public String getOppose(String ville) {
		if (contains(ville)) {
			if (getVille1().equals(ville)) {
				return getVille2();
			} else {
				return getVille1();
			}
		}
		return "";
	}

	public int getCout() {
		if (getOwner().contains(Owner.ME)) {
			return 0;
		} else if (getOwner().contains(Owner.NEUTRAL)) {
			return longueur;
		} else {
			return Owner.OTHER;
		}
	}

	public int getGain() {
		if (getOwner().contains(Owner.ME)) {
			return 0;
		} else if (getOwner().contains(Owner.NEUTRAL)) {
			switch (getCout()) {
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 4;
			case 4:
				return 7;
			case 6:
				return 15;
			case 8:
				return 21;
			default:
				return 0;
			}
		}

		return Owner.OTHER_VALUE; // Cas Owner.OTHER
	}

	public int getrealgain() {
		switch (longueur) {
		case 1:
			return 1;
		case 2:
			return 2;
		case 3:
			return 4;
		case 4:
			return 7;
		case 6:
			return 15;
		case 8:
			return 21;
		default:
			return 0;
		}

	}

	public float getRisk() {
		if (getOwner().contains(Owner.ME)) {
			return 0;
		} else if (getOwner().contains(Owner.NEUTRAL)) {
			float risque = (float) (1 / Math.sqrt(longueur));

			/*
			 * Dans le cas d'un chemin double, il faut que les deux owner soient neutres
			 * pour que le risque soit divisé par 2
			 */
			if (owner.size() == 2 && RouteManager.cheminsDoubles && owner.get(0) == owner.get(1)) {
				risque /= 2;
			}

			return risque;
		}

		return Owner.OTHER_VALUE; // Cas Owner.OTHER

	}

	@Override
	public String toString() {
		return getVille1() + " - " + getVille2();
	}
}
