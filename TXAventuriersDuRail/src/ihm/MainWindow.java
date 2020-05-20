package ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import constants.Owner;
import objects.Chemin;
import objects.Route;
import objects.RouteManager;
import process.RoadProcessor;

public class MainWindow extends JFrame {
	private static MainWindow instance = null;

	public static final String WINDOW_TITLE = "Aide à la décision - Aventuriers du Rail";
	public static final Dimension WINDOW_SIZE = Toolkit.getDefaultToolkit().getScreenSize(); // Taille de l'écran

	public static final int STRUCT = 5;

	private DisplayCheminPanel c_opti_pan;
	private DisplayCheminPanel c_short_pan;

	private JTextArea chemin_short;
	private JLabel short_cout;
	private JLabel short_gain;
	private JLabel short_risque;
	private JLabel short_ratio;

	private JTable etapesTable;
	private JComboBox<String> etapesCombo;
	private JButton etapesGo;
	private List<String> etapes;

	private JButton validate;
	private JComboBox<String> comboCityDep;
	private JComboBox<String> comboCityArr;

	/**
	 * Constructeur, appelé une fois : premier getInstance().
	 */
	private MainWindow() {
		super(WINDOW_TITLE);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				terminate();
			}
		});
	}

	/**
	 * A appeler une fois l'opération de Parsing est réalisée.
	 */
	public void initiate() {
		initHmi();

		etapes = new ArrayList<>();
	}

	/**
	 * Quitter l'application
	 */
	private void terminate() {
		dispose();
		System.exit(0);
	}

	private void initHmi() {
		// Composants
		JPanel mainPan = new JPanel();

		mainPan.setLayout(new BorderLayout());
		CityPossessionsPanel otherCitiesPanel = new CityPossessionsPanel(Owner.OTHER_ROADS, false);
		CityPossessionsPanel myCitiesPanel = new CityPossessionsPanel(Owner.MY_ROADS, true);

		// Inter-dépendance des combo box
		otherCitiesPanel.setDependantCPPanel(myCitiesPanel);
		myCitiesPanel.setDependantCPPanel(otherCitiesPanel);

		mainPan.add(otherCitiesPanel, BorderLayout.WEST);
		mainPan.add(mainPanel(), BorderLayout.CENTER);
		mainPan.add(myCitiesPanel, BorderLayout.EAST);

		// Placement, dimensions
		setContentPane(mainPan); // Utilisation du panneau ci-dessus comme contenu pour la fenêtre
		setSize(WINDOW_SIZE); // Redimensionner
		setLocationRelativeTo(null); // Centrer l'application
		setVisible(true); // Afficher

		checkCitiesOk();
	}

	private JPanel mainPanel() {
		JPanel pan = new JPanel();

		// Chemin selection
		Dimension dim = MainWindow.WINDOW_SIZE;
		Dimension mainPanSize = new Dimension((int) (dim.width / 2.5), dim.height - 100);
		Dimension childPanSize = new Dimension((int) (dim.width / 2.5 - 50), 250);
		pan.setPreferredSize(mainPanSize);

		comboCityDep = new JComboBox<>();
		initBoxWillAllCities(comboCityDep);
		comboCityDep.addActionListener(e -> checkCitiesOk());
		comboCityArr = new JComboBox<>();
		initBoxWillAllCities(comboCityArr);
		comboCityArr.addActionListener(e -> checkCitiesOk());

		validate = new JButton("Go !");
		validate.addActionListener(
				e -> process(comboCityDep.getSelectedItem().toString(), comboCityArr.getSelectedItem().toString()));

		Box mainBox = Box.createHorizontalBox();
		mainBox.add(Box.createHorizontalStrut(STRUCT));
		mainBox.add(comboCityDep);
		mainBox.add(Box.createHorizontalStrut(STRUCT));
		mainBox.add(comboCityArr);
		mainBox.add(Box.createHorizontalStrut(STRUCT));
		mainBox.add(validate);
		mainBox.add(Box.createHorizontalStrut(STRUCT));

		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Ville étape");
		etapesTable = new JTable(tableModel);
		etapesTable.setPreferredSize(new Dimension((int) (childPanSize.width / 2.5), childPanSize.height / 2));
		JScrollPane jscp = new JScrollPane(etapesTable);
		jscp.setPreferredSize(etapesTable.getPreferredSize());
		jscp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		// Table des étapes
		etapesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				Point point = mouseEvent.getPoint();
				int row = etapesTable.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && etapesTable.getSelectedRow() != -1) {
					processRowRemoval(row);
				}
			}
		});

		etapesCombo = new JComboBox<>();
		initBoxWillAllCities(etapesCombo);

		etapesGo = new JButton("+");
		etapesGo.addActionListener(e -> addEtape());

		Box etapeBox = Box.createHorizontalBox();
		etapeBox.add(Box.createHorizontalStrut(STRUCT));
		etapeBox.add(jscp);
		etapeBox.add(Box.createHorizontalStrut(STRUCT));
		etapeBox.add(etapesCombo);
		etapeBox.add(Box.createHorizontalStrut(STRUCT));
		etapeBox.add(etapesGo);
		etapeBox.add(Box.createHorizontalStrut(STRUCT));

		JPanel mainPan = new JPanel();
		mainPan.setPreferredSize(childPanSize);
		mainPan.setBorder(BorderFactory.createTitledBorder(new TitledBorder("Choix du chemin")));

		JPanel etapesPan = new JPanel();
		etapesPan.setPreferredSize(new Dimension(childPanSize.width - 50, (int) (childPanSize.height / 1.5)));
		etapesPan.setBorder(BorderFactory.createTitledBorder(new TitledBorder("Étapes du chemin")));
		etapesPan.add(etapeBox);

		mainPan.add(mainBox);
		mainPan.add(etapesPan);

		// Chemin opti
		c_opti_pan = new DisplayCheminPanel("Chemin le plus optimisé", childPanSize);

		// Chemin short
		c_short_pan = new DisplayCheminPanel("Chemin le plus court", childPanSize);

		Box mainPanBox = Box.createVerticalBox();
		mainPanBox.add(Box.createVerticalStrut(STRUCT));
		mainPanBox.add(mainPan);
		mainPanBox.add(Box.createVerticalStrut(STRUCT));
		mainPanBox.add(c_opti_pan);
		mainPanBox.add(Box.createVerticalStrut(STRUCT));
		mainPanBox.add(c_short_pan);
		mainPanBox.add(Box.createVerticalStrut(STRUCT));

		pan.add(mainPanBox);

		etapesTable.updateUI();

		return pan;
	}

	private void processRowRemoval(int row) {
		String city = etapesTable.getValueAt(row, 0).toString();
		etapes.remove(city);

		DefaultTableModel model = (DefaultTableModel) etapesTable.getModel();
		model.removeRow(row);
		etapesTable.clearSelection();
		etapesTable.updateUI();

		if (model.getRowCount() < 6) {
			etapesGo.setEnabled(true);
		}
	}

	private void addEtape() {
		String ville = etapesCombo.getSelectedItem().toString();
		etapes.add(ville);

		DefaultTableModel model = (DefaultTableModel) etapesTable.getModel();
		model.addRow(new Object[] { ville });

		if (model.getRowCount() >= 6) {
			etapesGo.setEnabled(false);
		}
	}

	/**
	 * Les deux mêmes villes ne peuvent pas etre sélectionnées
	 */
	private void checkCitiesOk() {
		if (comboCityArr.getSelectedItem().toString().equals(comboCityDep.getSelectedItem().toString())) {
			validate.setEnabled(false);
			validate.setToolTipText("Les deux mêmes villes doivent être différentes");
		} else {
			validate.setEnabled(true);
			validate.setToolTipText("");
		}
	}

	private void process(String villeDep, String villeArr) {
		RoadProcessor.chercherAvecEtapes(villeDep, villeArr, etapes);

		updateMainDisplay();
	}

	private void updateMainDisplay() {
		Chemin c_opti = RoadProcessor.getOptChemin(RoadProcessor.getMapChemin());

		if (c_opti != null) {
			c_opti_pan.updateDisplay(c_opti);
		} else {
			c_opti_pan.updateDisplayNoDispo();
		}

		Chemin c_short = RoadProcessor.getBestShortestChemin(RoadProcessor.getMapChemin());
		if (c_short != null) {
			if (c_opti != null && c_opti.getStringChemin().equals(c_short.getStringChemin())) {
				c_short_pan.updateDisplayIdentical();
			} else {
				c_short_pan.updateDisplay(c_short);
			}
		} else {
			c_short_pan.updateDisplayNoDispo();
		}

	}

	private void initBoxWillAllCities(JComboBox combo) {
		List<String> cities = new ArrayList<>();

		// Pour chaque route du manager, on ajoute les villes à la liste des villes
		for (Route r : RouteManager.getInstance()) {
			if (!cities.contains(r.getVille1())) {
				cities.add(r.getVille1());
			}
			if (!cities.contains(r.getVille2())) {
				cities.add(r.getVille2());
			}
		}

		Collections.sort(cities); // Ordre alphabétique
		cities.forEach(city -> combo.addItem(city));

		combo.setSelectedIndex(0);
	}

	/**
	 * Unique instance dans la fenêtre principale
	 * 
	 * @return
	 */
	public static MainWindow getInstance() {
		if (instance == null) {
			instance = new MainWindow();
		}
		return instance;
	}
}
