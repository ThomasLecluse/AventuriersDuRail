package ihm;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import constants.RiskLevel;
import objects.Chemin;
import process.RoadProcessor;

public class DisplayCheminPanel extends JPanel {

	private JTextArea chemin;
	private JLabel coutVal;
	private JLabel gainVal;
	private JLabel risqueVal;
	private JLabel scaledRiskVal;
	private JLabel ratioVal;

	private String panTitle;
	private Dimension panDim;

	private RiskScale rScale;

	public DisplayCheminPanel(String title, Dimension dim) {
		panTitle = title;
		panDim = dim;

		initHmi();
	}

	private void initHmi() {
		JPanel mainPan = new JPanel();
		mainPan.setPreferredSize(panDim);
		mainPan.setBorder(BorderFactory.createTitledBorder(new TitledBorder(panTitle)));

		JLabel cout = new JLabel("Coût :");
		cout.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel gain = new JLabel("Gain :");
		gain.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel risque = new JLabel("Risque calculé :");
		risque.setAlignmentX(RIGHT_ALIGNMENT);
		JLabel ratio = new JLabel("Ratio :");
		ratio.setAlignmentX(RIGHT_ALIGNMENT);

		chemin = new JTextArea();
		chemin.setPreferredSize(new Dimension(panDim.width - 50, 60));
		chemin.setWrapStyleWord(true);
		chemin.setLineWrap(true);
		chemin.setEditable(false);
		coutVal = new JLabel("-");
		gainVal = new JLabel("-");
		risqueVal = new JLabel("-");
		scaledRiskVal = new JLabel("");
		scaledRiskVal.setAlignmentX(CENTER_ALIGNMENT);
		ratioVal = new JLabel("-");

		rScale = new RiskScale();

		Box box_lab = Box.createVerticalBox();
		box_lab.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_lab.add(cout);
		box_lab.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_lab.add(gain);
		box_lab.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_lab.add(ratio);
		box_lab.add(Box.createVerticalStrut(MainWindow.STRUCT));

		Box box_val = Box.createVerticalBox();
		box_val.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_val.add(coutVal);
		box_val.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_val.add(gainVal);
		box_val.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_val.add(ratioVal);
		box_val.add(Box.createVerticalStrut(MainWindow.STRUCT));

		Box labRisqueBox = Box.createVerticalBox();
		labRisqueBox.add(Box.createVerticalStrut(MainWindow.STRUCT));
		labRisqueBox.add(risque);
		labRisqueBox.add(Box.createVerticalStrut(MainWindow.STRUCT));

		Box valRisqueBox = Box.createVerticalBox();
		valRisqueBox.add(Box.createVerticalStrut(MainWindow.STRUCT));
		valRisqueBox.add(risqueVal);
		valRisqueBox.add(Box.createVerticalStrut(MainWindow.STRUCT));

		Box labValRisqueBox = Box.createHorizontalBox();
		labValRisqueBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		labValRisqueBox.add(labRisqueBox);
		labValRisqueBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		labValRisqueBox.add(valRisqueBox);
		labValRisqueBox.add(Box.createHorizontalStrut(MainWindow.STRUCT));

		Box box_risque = Box.createVerticalBox();
		box_risque.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_risque.add(labValRisqueBox);
		box_risque.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_risque.add(scaledRiskVal);
		box_risque.add(Box.createVerticalStrut(MainWindow.STRUCT));
		box_risque.add(rScale);
		box_risque.add(Box.createVerticalStrut(MainWindow.STRUCT));

		JPanel riskPan = new JPanel();
		riskPan.setPreferredSize(new Dimension(panDim.width / 3, panDim.height / 2));
		riskPan.setBorder(BorderFactory.createTitledBorder(new TitledBorder("Risque")));
		riskPan.add(box_risque);

		Box main_box = Box.createHorizontalBox();
		main_box.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		main_box.add(box_lab);
		main_box.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		main_box.add(box_val);
		main_box.add(Box.createHorizontalStrut(MainWindow.STRUCT));
		main_box.add(riskPan);
		main_box.add(Box.createHorizontalStrut(MainWindow.STRUCT));

		Box main_box1 = Box.createVerticalBox();
		main_box1.add(Box.createVerticalStrut(MainWindow.STRUCT));
		main_box1.add(chemin);
		main_box1.add(Box.createVerticalStrut(MainWindow.STRUCT));
		main_box1.add(main_box);
		main_box1.add(Box.createVerticalStrut(MainWindow.STRUCT));

		mainPan.add(main_box1);

		add(mainPan);
	}

	public void updateDisplay(Chemin c) {
		chemin.setText(c.getStringChemin());

		coutVal.setText(String.valueOf(c.getCout()));
		gainVal.setText(String.valueOf(c.getGain()));
		risqueVal.setText(String.valueOf(c.getRisk()));
		scaledRiskVal.setText(RiskLevel.getLabel(RiskLevel.toScale(RoadProcessor.getRiskLevel(c))));
		ratioVal.setText(String.valueOf(c.getRatio()));

		rScale.positionRisk(c);
	}

	public void updateDisplayNoDispo() {
		chemin.setText("Aucun chemin disponible (ou trop long).");

		coutVal.setText("-");
		gainVal.setText("-");
		risqueVal.setText("-");
		scaledRiskVal.setText("");
		ratioVal.setText("-");

		rScale.clearAllLabels();
	}

	public void updateDisplayIdentical() {
		chemin.setText("Chemin identique au plus optimisé.");

		coutVal.setText("-");
		gainVal.setText("-");
		risqueVal.setText("-");
		scaledRiskVal.setText("");
		ratioVal.setText("-");

		rScale.clearAllLabels();
	}
}
