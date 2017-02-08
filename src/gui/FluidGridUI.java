package gui;

import FluidGrid.Grid;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by silmathoron on 08/02/2017.
 */
public class FluidGridUI {
	private JPanel panel1;
	private JSlider baseVolumeSlider;
	private JSpinner baseVolumeSpinner;
	private JPanel baseVolumePanel;
	private JPanel precisionPanel;
	private JSlider precisionSlider;
	private JSpinner precisionSpinner;
	private JPanel pressureDifferencePanel;
	private JSlider pressureDifferenceSlider;
	private JSpinner pressureDifferenceSpinner;
	private JPanel flowUnderPressurePanel;
	private JSlider flowUnderPressureSlider;
	private JSpinner flowUnderPressureSpinner;
	private JButton playPauseButton;
	private JButton stepButton;
	private JButton resetButton;
	private JPanel buttonsPanel;
	private JPanel fluidGridPanel;
	private FluidGridPanel gridPanel;

	private Grid grid;

	public FluidGridUI() {
		playPauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gridPanel.startStop();
			}
		});
		stepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gridPanel.step();
			}
		});
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				gridPanel.reset();
			}
		});
		precisionSpinner.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				//TODO
			}
		});
	}

	private void createUIComponents() {
		grid = new Grid();
		fluidGridPanel = new FluidGridPanel(grid);
		gridPanel = (FluidGridPanel) fluidGridPanel;
	}
}
