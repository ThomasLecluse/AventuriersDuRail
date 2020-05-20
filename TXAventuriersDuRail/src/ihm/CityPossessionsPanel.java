package ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import constants.Owner;
import objects.Route;
import objects.RouteManager;

public class CityPossessionsPanel extends JPanel {
	private String panelName;

	private JComboBox<String> comboCityA;
	private JComboBox<String> comboCityB;
	private JButton addButton;
	private JTable citiesTable;
	private JLabel gaintotal;
	private boolean displaygain;

	private CityPossessionsPanel dependantPanel = null;

	private static final float WIDTH_PROP = 3.3f;

	// Contient toutes les villes qui seront dans la combo box A
	private List<String> cities;

	public CityPossessionsPanel(String name, boolean dispgain) {
		panelName = name;
		displaygain = dispgain;

		cities = new ArrayList<>();
		initHmi();
	}

	/**
	 * A cause de l'inter-dépendance des combo box, on doit pouvoir accéder au
	 * panneau jumeau pour mettre à jour les combo box
	 * 
	 * @param cpp
	 */
	public void setDependantCPPanel(CityPossessionsPanel cpp) {
		dependantPanel = cpp;
	}

	private void initHmi() {
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Ville A");
		tableModel.addColumn("Ville B");
		citiesTable = new JTable(tableModel);

		if (displaygain) {
			gaintotal = new JLabel();
		}

		// Sélection
		comboCityA = new JComboBox<>();
		comboCityB = new JComboBox<>();
		comboCityA.addActionListener(e -> initComboBoxB());
		addButton = new JButton("+");
		addButton.addActionListener(e -> addSelectionToTable());
		addButton.setToolTipText("Ajouter la sélection");

		JLabel separationLabel = new JLabel("--");

		Box citySelectionBox = Box.createHorizontalBox();
		citySelectionBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		citySelectionBox.add(comboCityA);
		citySelectionBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		citySelectionBox.add(separationLabel);
		citySelectionBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		citySelectionBox.add(comboCityB);
		citySelectionBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		citySelectionBox.add(addButton);
		citySelectionBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));

		// Table des possessions
		citiesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent mouseEvent) {
				Point point = mouseEvent.getPoint();
				int row = citiesTable.rowAtPoint(point);
				if (mouseEvent.getClickCount() == 2 && citiesTable.getSelectedRow() != -1) {
					processRowRemoval(row);
				}
			}
		});
		Box cityTableBox = Box.createVerticalBox();
		if (displaygain) {
			cityTableBox.add(Box.createVerticalStrut(MainWindow.STRUCT));
			cityTableBox.add(gaintotal);
		}
		cityTableBox.add(Box.createVerticalStrut(MainWindow.STRUCT));
		cityTableBox.add(new JScrollPane(citiesTable));
		cityTableBox.add(Box.createVerticalStrut(MainWindow.STRUCT));

		// Ajout des composants
		setLayout(new BorderLayout());
		add(citySelectionBox, BorderLayout.NORTH);
		add(cityTableBox, BorderLayout.CENTER);
		setBorder(BorderFactory.createTitledBorder(new TitledBorder(panelName)));
		Dimension dim = MainWindow.WINDOW_SIZE;
		setPreferredSize(new Dimension((int) (dim.width / WIDTH_PROP), dim.height - 100));

		updateComboCities(true); // Mise à jour des combo box
		citiesTable.updateUI();
	}

	private void updategain() {
		if (displaygain) {
			gaintotal.setText("Mon gain : " + RouteManager.getInstance().getgain());
		}
	}

	private void processRowRemoval(int row) {
		String city1 = citiesTable.getValueAt(row, 0).toString();
		String city2 = citiesTable.getValueAt(row, 1).toString();

		RouteManager.getInstance().setRouteOwner(city1, city2, Owner.NEUTRAL, Owner.getRelatedOwner(panelName));
		DefaultTableModel model = (DefaultTableModel) citiesTable.getModel();
		model.removeRow(row);
		citiesTable.clearSelection();
		citiesTable.updateUI();

		if (!comboCityA.isEnabled()) { // Il n'y avait plus de villes dispo
			enableCitySelections();
			if (dependantPanel != null) {
				dependantPanel.enableCitySelections();
			}
		}
		updateComboCities(true);
		updategain();

	}

	public void enableCitySelections() {
		comboCityA.setEnabled(true);
		comboCityB.setEnabled(true);
		addButton.setEnabled(true);
		comboCityA.setToolTipText("");
		comboCityB.setToolTipText("");
		addButton.setToolTipText("");
	}

	private void addSelectionToTable() {
		String villeA = comboCityA.getSelectedItem().toString();
		String villeB = comboCityB.getSelectedItem().toString();

		DefaultTableModel model = (DefaultTableModel) citiesTable.getModel();
		model.addRow(new Object[] { villeA, villeB });

		// On met à jour l'owner
		RouteManager.getInstance().setRouteOwner(villeA, villeB, Owner.getRelatedOwner(panelName),
				Owner.getRelatedOwner(panelName));
		// On met à jour les combo box
		updateComboCities(true);
		updategain();

	}

	public void updateComboCities(boolean internalCall) {
		initComboBoxA(); // La combo B va être mise à jour après la A, car l'index de A va changer

		if (internalCall && dependantPanel != null) {
			dependantPanel.updateComboCities(false);
		}
	}

	/**
	 * La combo box A possède toutes les villes disponnibles.
	 */
	private void initComboBoxA() {
		comboCityA.removeAllItems(); // On vide
		cities.clear();

		// Pour chaque route du manager, on ajoute les villes à la liste des villes
		for (Route r : RouteManager.getInstance().iterate(false)) {
			if (!cities.contains(r.getVille1())) {
				cities.add(r.getVille1());
			}
			if (!cities.contains(r.getVille2())) {
				cities.add(r.getVille2());
			}
		}

		Collections.sort(cities);
		cities.forEach(city -> comboCityA.addItem(city));
		if (!cities.isEmpty()) {
			comboCityA.setSelectedIndex(0);
			comboCityA.setToolTipText("");
		} else {
			// Si la liste est vide : il n'y a plus de routes dispo -> on verrouille tout
			comboCityA.setEnabled(false);
			comboCityB.setEnabled(false);
			comboCityB.removeAllItems();
			addButton.setEnabled(false);
			comboCityA.setToolTipText("Toutes les routes sont occupées");
			comboCityB.setToolTipText("Toutes les routes sont occupées");
			addButton.setToolTipText("Toutes les routes sont occupées");
			comboCityB.updateUI();
			addButton.updateUI();
		}
		comboCityA.updateUI();
	}

	/**
	 * La combo box B possède uniquement les voisins de la ville sélectionnée en A.
	 */
	private void initComboBoxB() {
		if (comboCityB.isEnabled() && comboCityA.getSelectedItem() != null) {
			comboCityB.removeAllItems();

			for (String s : RouteManager.getInstance().getVoisins(comboCityA.getSelectedItem().toString(), false)) {
				comboCityB.addItem(s);
			}
			comboCityB.setSelectedIndex(0);
			comboCityB.updateUI();
		}
	}
}
