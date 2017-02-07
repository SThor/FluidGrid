package gui;

import FluidGrid.Grid;

import javax.swing.*;
import java.awt.*;

/**
 * Created by silmathoron on 31/01/2017.
 */
public class Main {
	public static void main(String[] args) {

		JFrame frame = new JFrame("Fluid Grid");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(new Dimension(500, 500));
		frame.setContentPane(new FluidGridPanel(new Grid()));
		frame.pack();
		frame.setVisible(true);
	}
}
