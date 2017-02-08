package gui;

import FluidGrid.Grid;
import FluidGrid.cell.Cell;
import FluidGrid.cell.Wall;
import FluidGrid.cell.WaterCell;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

/**
 * Created by silmathoron on 31/01/2017.
 */
public class FluidGridPanel extends JPanel {
	private static final Color WATER_COLOR_CLEAR = new Color(140, 228, 255);
	private static final Color WATER_COLOR_DARK = new Color(39, 69, 104);
	private static final float WATER_COLOR_HUE = 0.541f;
	private static final float WATER_COLOR_SATURATION = 0.45f; //0.45f
	private static final float WATER_COLOR_BRIGHTNESS = 0.95f;
	private static final double DEFAULT_ADDED_VOLUME = 20.0;
	double heightScale = 20.0;
	double widthScale = 20.0;
	int cellHeight = 30;
	int cellWidth = 30;
	boolean debugDisplay = true;
	Grid grid;
	private boolean currentlySwitchingToWalls;
	private boolean running;
	private Graphics2D g2;

	public FluidGridPanel(final Grid grid) {
		this.grid = grid;
		this.setFocusable(true);
		this.requestFocus();
		setupListeners();
	}

	private void setupListeners() {
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyChar() == ' ') {
					startStop();
				} else if (e.getKeyChar() == 'r') {
					reset();
				} else {
					step();
				}
			}
		});
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePress(e);
			}
		});

		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDrag(e);
			}
		});
	}

	public void reset() {
		grid.reset(); //TODO
		repaint();
	}

	private void mousePress(MouseEvent e) {
		Cell cell = eventToCell(e);
		if (SwingUtilities.isLeftMouseButton(e)) {
			leftMouseAction(cell);
		} else if (SwingUtilities.isRightMouseButton(e)) {
			currentlySwitchingToWalls = !(cell instanceof Wall);
			grid.switchWall(cell);
		}
		repaint();
	}

	private void leftMouseAction(Cell cell) {
		if (cell instanceof WaterCell) {
			WaterCell waterCell = (WaterCell) cell;
			waterCell.add(DEFAULT_ADDED_VOLUME);
		}
	}

	private Cell eventToCell(MouseEvent e) {
		Point eventPoint = new Point(e.getX() - getOffset().x, e.getY() - getOffset().y);
		Point gridCoords = pixelToGrid(eventPoint);
		return grid.getCell(gridCoords);
	}

	private void mouseDrag(MouseEvent e) {
		Cell cell = eventToCell(e);
		if (SwingUtilities.isLeftMouseButton(e)) {
			leftMouseAction(cell);
		} else if (SwingUtilities.isRightMouseButton(e)) {
			if (currentlySwitchingToWalls && !(cell instanceof Wall)) {
				grid.switchWall(cell);
			} else if (!currentlySwitchingToWalls && cell instanceof Wall) {
				grid.switchWall(cell);
			}
		}
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < cellHeight; i++) {
			for (int j = 0; j < cellWidth; j++) {
				Cell cell = grid.getCell(new Point(i, j));
				if (cell == null) {
					cell = new WaterCell(new Point(i, j), 0);
					grid.add(cell);
				}
				drawCell(cell);
			}
		}

		g2.setColor(Color.LIGHT_GRAY);
		g2.drawRect(getOffset().x, getOffset().y, (int) widthScale * cellWidth, (int) heightScale * cellHeight);
	}

	private void drawCell(Cell cell) {
		Point origin = new Point(getOffset().x + gridToPixel(cell.getCoords()).x, getOffset().y + gridToPixel(cell.getCoords()).y);
		g2.setColor(Color.WHITE);
		g2.fillRect(origin.x, origin.y, (int) widthScale, (int) heightScale);
		if (debugDisplay) {
			g2.setColor(Color.LIGHT_GRAY);
			g2.drawRect(origin.x, origin.y, (int) widthScale, (int) heightScale);
		}
		if (cell != null) {
			if (cell instanceof WaterCell) {
				WaterCell waterCell = (WaterCell) cell;
				g2.setColor(computeWaterColor(waterCell));
				if (waterCell.isFull()) {
					g2.fillRect(origin.x, origin.y, (int) widthScale, (int) heightScale);
				} else if (waterCell.isFalling()) {
					g2.fillRect(origin.x, origin.y, (int) widthScale, (int) heightScale);
				} else {
					int y = (int) (origin.y + (1 - waterCell.getPortionFilled()) * heightScale);
					g2.fillRect(origin.x, y, (int) widthScale, (int) (heightScale * waterCell.getPortionFilled()) + 1);//+1 ?
				}
				if (debugDisplay) {
					g2.setColor(Color.BLACK);
					g2.setFont(new Font("Courier", Font.PLAIN, 8));
					g2.drawString("[" + waterCell.getWaterVolume() + "]", origin.x + 2, origin.y + (int) heightScale - 1);
				}
			} else if (cell instanceof Wall) {
				g2.setColor(Color.BLACK);
				g2.fillRect(origin.x, origin.y, (int) widthScale, (int) heightScale);
			}
			if (debugDisplay) {
				g2.setColor(Color.GRAY);
				g2.setFont(new Font("Courier", Font.PLAIN, 8));
				int i = cell.getCoords().x;
				int j = cell.getCoords().y;
				g2.drawString("[" + i + "," + j + "]", origin.x + 2, origin.y + 10);
			}
		}
	}

	private Color computeWaterColor(WaterCell cell) {
		return interpolate(WATER_COLOR_DARK, WATER_COLOR_CLEAR, cell.getPressure());
	}

	public void startStop() {
		if (!running) {
			running = true;
			class Run implements Runnable {
				public void run() {
					while (running) {
						step();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
			new Thread(new Run()).start();
		} else {
			running = false;
		}
	}

	public void step() {
		grid.tick();
		repaint();
	}

	private Color interpolate(Color color1, Color color2, float p) {
		int r = (int) (color1.getRed() * p + color2.getRed() * (1.0f - p));
		int g = (int) (color1.getGreen() * p + color2.getGreen() * (1.0f - p));
		int b = (int) (color1.getBlue() * p + color2.getBlue() * (1.0f - p));
		r = r < 0 ? 0 : r > 255 ? 255 : r;
		g = g < 0 ? 0 : g > 255 ? 255 : g;
		b = b < 0 ? 0 : b > 255 ? 255 : b;

		return new Color(r, g, b);
	}


	public Point gridToPixel(Point gridPoint) {
		return new Point((int) (gridPoint.x * widthScale), (int) (gridPoint.y * heightScale));
	}

	public Point2D.Double pixelToFractionalGrid(Point pixelPoint) {
		double x = (double) pixelPoint.x;
		double y = (double) pixelPoint.y;
		return new Point2D.Double(x / widthScale, y / heightScale);
	}

	public Point fractionalGridToGrid(Point2D.Double fractionalGridPoint) {
		return new Point((int) fractionalGridPoint.x, (int) fractionalGridPoint.y);
	}

	public Point pixelToGrid(Point pixelPoint) {
		return fractionalGridToGrid(pixelToFractionalGrid(pixelPoint));
	}

	public Point getOffset() {
		int x = (getWidth() - (int) widthScale * cellHeight) / 2;
		int y = (getHeight() - (int) heightScale * cellHeight) / 2;
		return new Point(x, y);
	}

	public void setHeightScale(int heightScale) {
		this.heightScale = heightScale;
	}

	public void setWidthScale(int widthScale) {
		this.widthScale = widthScale;
	}
}
