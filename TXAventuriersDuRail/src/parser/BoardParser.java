package parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import objects.Route;
import objects.RouteManager;

/**
 * Parser chargé de l'import du fichier de plateau de jeu.
 * 
 * @author thomas
 */
public class BoardParser {
	public static final String CONFIG_FILE = "./src/resources/config.xml";

	public static String IMPORT_FILE = "";

	private static Document document;
	private static Element racine;

	/**
	 * Appelle les différentes méthodes permettant d'initialiser le parser et de
	 * mapper les objets contenus dans le fichier dans les objets conceptuels.
	 */
	public static void parseAndMap() {
		initConfig();
		initParser();
		importAll();
	}

	private static void initConfig() {
		SAXBuilder sxb = new SAXBuilder();
		Document conf;

		try {
			// fichier de configuration
			conf = sxb.build(new File(CONFIG_FILE));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// On récpère la racine du fichier
		Element configRoot = conf.getRootElement();

		RouteManager.cheminsDoubles = Boolean.parseBoolean(configRoot.getChildText("routeDouble"));
		IMPORT_FILE = configRoot.getChildText("boardPath");

		System.out.println("---- Configuration ----");
		System.out.println("Plateau de jeu : " + IMPORT_FILE);
		System.out.println("Troncons doubles : " + RouteManager.cheminsDoubles);
		System.out.println("-----------------------");
	}

	/**
	 * Initialisation du processus d'import et de parsing.
	 */
	private static void initParser() {
		SAXBuilder sxb = new SAXBuilder();
		try {
			// On crée un nouveau document JDOM à partir du plateau de jeu
			document = sxb.build(new File(IMPORT_FILE));
		} catch (Exception e) {
			e.printStackTrace();
		}

		// On récpère la racine du fichier
		racine = document.getRootElement();
	}

	/**
	 * Importe les objets à partir de la racine du fichier dans les objets
	 * conceptuels correspondants.
	 */
	private static void importAll() {
		// On récupère tous les éléments de la racine (ici toutes les routes)
		List<Element> listEtudiants = racine.getChildren("route");

		// Pour chacun de ces éléments
		Iterator<Element> i = listEtudiants.iterator();
		while (i.hasNext()) {
			Element route = i.next();

			int l = Integer.valueOf(route.getChild("cout").getText());
			List<String> col = new ArrayList<>();
			String couleur = route.getChild("couleur").getText();
			for (String s : couleur.split(",")) {
				col.add(s);
			}

			RouteManager.getInstance()
					.add(new Route(l, col, route.getChild("ville1").getText(), route.getChild("ville2").getText()));
		}
	}
}
