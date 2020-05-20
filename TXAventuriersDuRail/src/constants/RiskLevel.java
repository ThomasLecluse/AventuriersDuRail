package constants;

public class RiskLevel {

	public static final int MINIMAL = 0;
	public static final int LOW = 36;
	public static final int INTERMEDIATE = 50;
	public static final int HIGH = 60;
	public static final int CRITICAL = 72;

	/**
	 * Renvoie le label correspondant au niveau de risque
	 * 
	 * @param riskLevel
	 * @return
	 */
	public static String getLabel(int riskLevel) {
		switch (riskLevel) {
		case MINIMAL:
			return "Minime";
		case LOW:
			return "Faible";
		case INTERMEDIATE:
			return "Modéré";
		case HIGH:
			return "Élevé";
		case CRITICAL:
			return "Critique";
		default:
			return "Inconnu";
		}
	}

	/**
	 * Mise à l'échelle (inférieure) du risque précisé
	 * 
	 * @param r
	 * @return
	 */
	public static int toScale(int r) {
		if (r < LOW) {
			return MINIMAL;
		} else if (r < INTERMEDIATE) {
			return LOW;
		} else if (r < HIGH) {
			return INTERMEDIATE;
		} else if (r < CRITICAL) {
			return HIGH;
		} else {
			return CRITICAL;
		}
	}

	/**
	 * Récupère le rapprochement par rapport aux bornes inf et sup d'un niveau de
	 * risque
	 * 
	 * @param r
	 * @return
	 */
	public static int getPrecisedScale(int r) {
		int s = toScale(r);

		if (s == MINIMAL) {
			return etalonner(MINIMAL, LOW, r);
		} else if (s == MINIMAL) {
			return etalonner(LOW, INTERMEDIATE, r);
		} else if (s == INTERMEDIATE) {
			return etalonner(INTERMEDIATE, HIGH, r);
		} else if (s == HIGH) {
			return etalonner(HIGH, CRITICAL, r);
		} else {
			return etalonner(CRITICAL, 100, r);
		}
	}

	/**
	 * Etant donné les bornes et le risque associé, étalonne le risque sur ces
	 * bornes sur 4 niveaux
	 * 
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	private static int etalonner(int min, int max, int n) {
		int intervalle = max - min;
		int e = max - n;

		if (e < intervalle / 4) {
			return 1;
		} else if (e < intervalle / 2) {
			return 2;
		} else if (e < intervalle * 3 / 4) {
			return 3;
		} else {
			return 4;
		}
	}

}
