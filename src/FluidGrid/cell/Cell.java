package FluidGrid.cell;

import FluidGrid.Settings;

import java.awt.*;
import java.util.List;

/**
 * Created by silmathoron on 31/01/2017.
 */
public abstract class Cell {
	protected Cell nextTickCell;
	protected Cell nextTickDownCell;
	protected Cell nextTickLeftCell;
	protected Cell nextTickRightCell;
	protected Cell nextTickUpCell;
	protected Cell downCell;
	protected Cell leftCell;
	protected Cell rightCell;
	protected Cell upCell;
	private Point coords;
	private Settings settings;

	public Cell(Point c) {
		coords = c;
	}

	public void tick(List<Cell> neighbors, List<Cell> nextTickNeighbors) {
	}

	public Point getCoords() {
		return coords;
	}

	public abstract Cell deepCopy();

	public void setNextTickNeighbors(List<Cell> nextTickNeighbors) {
		this.nextTickCell = nextTickNeighbors.get(4);
		this.nextTickDownCell = nextTickNeighbors.get(2);
		this.nextTickLeftCell = nextTickNeighbors.get(3);
		this.nextTickRightCell = nextTickNeighbors.get(1);
		this.nextTickUpCell = nextTickNeighbors.get(0);
	}

	public void setNeighbors(List<Cell> neighbors) {
		this.downCell = neighbors.get(2);
		this.leftCell = neighbors.get(3);
		this.rightCell = neighbors.get(1);
		this.upCell = neighbors.get(0);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof Cell) {
			Cell cell = (Cell) obj;
			return coords.equals(cell.coords);
		}
		return false;
	}
}
