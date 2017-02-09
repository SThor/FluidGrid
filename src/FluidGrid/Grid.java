package FluidGrid;

import FluidGrid.cell.Cell;
import FluidGrid.cell.Wall;
import FluidGrid.cell.WaterCell;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by silmathoron on 31/01/2017.
 */
public class Grid {
	private final static List<Point> NEIGHBORS = new ArrayList<>(Arrays.asList(
			new Point(0, -1),    //up
			new Point(1, 0),    //right
			new Point(0, 1),    //down
			new Point(-1, 0)    //left
	));
	Map<Point, Cell> map = new HashMap<>();
	private boolean running;

	public Grid(Map<Point, Cell> map) {
		this.map = map;
	}

	public Grid() {
		Settings.reset();
		this.map = new HashMap<>();
	}

	public Cell getCell(Point coords) {
		return map.get(coords);
	}

	public List<Cell> getNeighbors(Point cell) {
		List<Cell> neighbors = new ArrayList<>();
		for (Point relativeNeighbor : NEIGHBORS) {
			Point neighbor = new Point(relativeNeighbor.x + cell.x, relativeNeighbor.y + cell.y);
			neighbors.add(getCell(neighbor));
		}
		return neighbors;
	}

	public void tick() {
		Grid nextTickGrid = deepCopy();
		Map<Point, Cell> nextTickMap = nextTickGrid.getMap();
		for (Point cell : map.keySet()) {
			Cell nextTickCell = nextTickMap.get(cell);
			List<Cell> nextTickNeighbors = nextTickGrid.getNeighbors(cell);
			nextTickNeighbors.add(nextTickCell);

			map.get(cell).tick(getNeighbors(cell), nextTickNeighbors);
		}
		this.setMap(nextTickMap);
	}

	public Map<Point, Cell> getMap() {
		return map;
	}

	public void setMap(Map<Point, Cell> map) {
		this.map = map;
	}

	public Grid deepCopy() {
		Map<Point, Cell> nextTickMap = new HashMap<>();
		for (Point coords : map.keySet()) {
			Cell nextTickCell = map.get(coords).deepCopy();
			nextTickMap.put(coords, nextTickCell);
		}
		return new Grid(nextTickMap);
	}

	public void add(Cell cell) {
		map.put(cell.getCoords(), cell);
	}

	public void reset() {
		map.clear();
	}

	public void switchWall(Cell cell) {
		if (cell != null) {
			Point gridCoords = cell.getCoords();
			if (cell instanceof Wall) {
				cell = new WaterCell(gridCoords, 0);
			} else {
				cell = new Wall(gridCoords);
			}
			map.put(gridCoords, cell);
		}
	}
}
