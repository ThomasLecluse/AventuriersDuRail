package ihm;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import constants.RiskLevel;
import objects.Chemin;
import process.RoadProcessor;

public class RiskScale extends JPanel {
	private static final int LAB_WIDTH = 30;
	private static final Dimension SCALE_ELT_DIM = new Dimension(LAB_WIDTH, 30);

	private JLabel l1;
	private JLabel l2;
	private JLabel l3;
	private JLabel l4;
	private JLabel l5;

	public RiskScale() {
		initScale();
	}

	private void initScale() {
		l1 = new JLabel("");
		l1.setPreferredSize(SCALE_ELT_DIM);
		l1.setBackground(Color.green);
		l1.setOpaque(true);

		l2 = new JLabel("");
		l2.setPreferredSize(SCALE_ELT_DIM);
		l2.setBackground(Color.yellow);
		l2.setOpaque(true);

		l3 = new JLabel("");
		l3.setPreferredSize(SCALE_ELT_DIM);
		l3.setBackground(Color.orange);
		l3.setOpaque(true);

		l4 = new JLabel("");
		l4.setPreferredSize(SCALE_ELT_DIM);
		l4.setBackground(Color.pink);
		l4.setOpaque(true);

		l5 = new JLabel("");
		l5.setPreferredSize(SCALE_ELT_DIM);
		l5.setBackground(Color.red);
		l5.setOpaque(true);

		Box b = Box.createHorizontalBox();
		b.add(l1);
		b.add(l2);
		b.add(l3);
		b.add(l4);
		b.add(l5);

		add(b);

		clearAllLabels();
	}

	public void positionRisk(Chemin c) {
		clearAllLabels();

		int riskLevelP = RiskLevel.getPrecisedScale(RoadProcessor.getRiskLevel(c));
		int riskLevel = RiskLevel.toScale(RoadProcessor.getRiskLevel(c));

		switch (riskLevel) {
		case RiskLevel.MINIMAL:
			l1.setText(getString(riskLevelP));
			break;
		case RiskLevel.LOW:
			l2.setText(getString(riskLevelP));
			break;
		case RiskLevel.INTERMEDIATE:
			l3.setText(getString(riskLevelP));
			break;
		case RiskLevel.HIGH:
			l4.setText(getString(riskLevelP));
			break;
		case RiskLevel.CRITICAL:
			l5.setText(getString(riskLevelP));
			break;
		default:
			break;
		}
	}

	/**
	 * Récupère la chaine associée à l'étalonnage du niveau de risque
	 * 
	 * @param p
	 * @return
	 */
	private String getString(int p) {
		if (p == 1) {
			return "X \t\t \t";
		} else if (p == 2) {
			return "\t X\t \t";
		} else if (p == 3) {
			return "\t \t X\t";
		} else { // P = 4
			return "\t \t \tX";
		}
	}

	public void clearAllLabels() {
		String blankSpaces = "\t \t \t \t";
		l1.setText(blankSpaces);
		l2.setText(blankSpaces);
		l3.setText(blankSpaces);
		l4.setText(blankSpaces);
		l5.setText(blankSpaces);
	}
}
