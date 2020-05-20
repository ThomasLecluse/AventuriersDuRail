import ihm.MainWindow;
import parser.BoardParser;

/**
 * Main launch class for the application
 * 
 * @author thomas
 */
public class AventurierDuRail {

	public static void main(String[] args) {

		BoardParser.parseAndMap();
		MainWindow.getInstance().initiate(); // Initialisation de la fenêtre graphique
	}
}