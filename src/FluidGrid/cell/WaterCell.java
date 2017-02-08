package FluidGrid.cell;

import java.awt.*;
import java.util.List;

/**
 * Created by silmathoron on 31/01/2017.
 */
public class WaterCell extends Cell {
	public final static double BASE_VOLUME = 100;
	private static final int LEFT = 0;
	private static final int RIGHT = 1;
	private static final double PRECISION = 0.001;
	private static final double PRESSURE_DIFFERENCE = 0.1;
	private static final double FLOW_DOWN_UNDER_PRESSURE = 0.08;
	private static final double FLOW_UP_UNDER_PRESSURE = 0.08;

	private double waterVolume;
	private double nextTickWaterVolume;
	private boolean falling = false;

	public WaterCell(Point c, double volume) {
		super(c);
		this.waterVolume = volume;
	}

	@Override
	public void tick(List<Cell> neighbors, List<Cell> nextTickNeighbors) {
		falling = false;
		round();
		nextTickWaterVolume = waterVolume;
		setNextTickNeighbors(nextTickNeighbors);
		setNeighbors(neighbors);

		if (nextTickWaterVolume > 0.0)
			tryFlowingDown();
		if (nextTickWaterVolume > 0.0)
			tryFlowingSide();
		if (nextTickWaterVolume > 0.0)
			tryOverFlowing();
	}

	public void round() {
		if (waterVolume < PRECISION)
			waterVolume = 0.0;
	}

	private void tryOverFlowing() {
		if (waterVolume > BASE_VOLUME && upCell instanceof WaterCell) {
			WaterCell upWaterCell = (WaterCell) upCell;
			if (upWaterCell.waterVolume * (1 + PRESSURE_DIFFERENCE) <= waterVolume) {
				double transferedVolume = waterVolume * FLOW_UP_UNDER_PRESSURE; //TODO modifier pour avoir une fontaine

				WaterCell nextTickWaterCell = (WaterCell) nextTickCell;
				WaterCell nextTickUpWaterCell = (WaterCell) nextTickUpCell;

				flow(nextTickWaterCell, nextTickUpWaterCell, transferedVolume);
			}
		}
	}

	private boolean tryFlowingDown() {
		boolean success = false;
		if (waterVolume > 0 && downCell instanceof WaterCell) {
			WaterCell downWaterCell = (WaterCell) downCell;
			if (!downWaterCell.isFull()) {
				double transferedVolume = Math.min(downWaterCell.getEmptyVolume(), waterVolume);

				WaterCell nextTickWaterCell = (WaterCell) nextTickCell;
				WaterCell nextTickDownWaterCell = (WaterCell) nextTickDownCell;
				nextTickDownWaterCell.falling = true;

				flow(nextTickWaterCell, nextTickDownWaterCell, transferedVolume);
				success = true;
			} else if (downWaterCell.waterVolume < (1 + PRESSURE_DIFFERENCE) * waterVolume) {
				double transferedVolume = FLOW_DOWN_UNDER_PRESSURE * waterVolume;

				WaterCell nextTickWaterCell = (WaterCell) nextTickCell;
				WaterCell nextTickDownWaterCell = (WaterCell) nextTickDownCell;

				flow(nextTickWaterCell, nextTickDownWaterCell, transferedVolume);
				success = true;
			}
		}
		return success;
	}

	private boolean tryFlowingSide() {
		boolean success = false;
		if (rightCell instanceof WaterCell && leftCell instanceof WaterCell) {
			WaterCell rightWaterCell = (WaterCell) rightCell;
			WaterCell leftWaterCell = (WaterCell) leftCell;

			if (rightWaterCell.getWaterVolume() < waterVolume && leftWaterCell.getWaterVolume() < waterVolume) {
				flowBothSides();
				success = true;
			} else if (rightWaterCell.getWaterVolume() < waterVolume) {
				flowSide(RIGHT);
				success = true;
			} else if (leftWaterCell.getWaterVolume() < waterVolume) {
				flowSide(LEFT);
				success = true;
			}
		} else if (rightCell instanceof WaterCell) {
			WaterCell rightWaterCell = (WaterCell) rightCell;
			if (rightWaterCell.getWaterVolume() < waterVolume) {
				flowSide(RIGHT);
				success = true;
			}
		} else if (leftCell instanceof WaterCell) {
			WaterCell leftWaterCell = (WaterCell) leftCell;
			if (leftWaterCell.getWaterVolume() < waterVolume) {
				flowSide(LEFT);
				success = true;
			}
		}
		return success;
	}

	private void flowBothSides() {
		WaterCell rightWaterCell = (WaterCell) rightCell;
		WaterCell leftWaterCell = (WaterCell) leftCell;
		if (rightWaterCell.getWaterVolume() < leftWaterCell.getWaterVolume()) {
			flowSide(RIGHT);
		} else if (leftWaterCell.getWaterVolume() < rightWaterCell.getWaterVolume()) {
			flowSide(LEFT);
		} else {
			double transferedVolume = (waterVolume - (rightWaterCell.getWaterVolume())) / 3.1; //le right est choisi arbitrairement, les deux étant égaux.
			WaterCell nextTickWaterCell = (WaterCell) nextTickCell;
			WaterCell nextTickRightWaterCell = (WaterCell) nextTickRightCell;
			WaterCell nextTickLeftWaterCell = (WaterCell) nextTickLeftCell;

			flow(nextTickWaterCell, nextTickRightWaterCell, transferedVolume);
			flow(nextTickWaterCell, nextTickLeftWaterCell, transferedVolume);
		}
	}

	private void flowSide(int side) {
		WaterCell sideWaterCell;
		WaterCell nextTickSideWaterCell;
		switch (side) {
			case LEFT:
				sideWaterCell = (WaterCell) leftCell;
				nextTickSideWaterCell = (WaterCell) nextTickLeftCell;
				break;
			case RIGHT:
				sideWaterCell = (WaterCell) rightCell;
				nextTickSideWaterCell = (WaterCell) nextTickRightCell;
				break;
			default:
				sideWaterCell = (WaterCell) rightCell;
				nextTickSideWaterCell = (WaterCell) nextTickRightCell;
		}
		double transferedVolume = (waterVolume - sideWaterCell.getWaterVolume()) / 2.1; //TODO changer en 3 si je veux épaissir le liquide

		WaterCell nextTickWaterCell = (WaterCell) nextTickCell;

		flow(nextTickWaterCell, nextTickSideWaterCell, transferedVolume);
	}


	private void flow(WaterCell startCell, WaterCell endCell, double transferedVolume) {
		remove(transferedVolume);
		startCell.remove(transferedVolume);
		endCell.add(transferedVolume);

		round();
		startCell.round();
		endCell.round();
	}

	private void remove(double transferedVolume) {
		if (transferedVolume > waterVolume) {
			//TODO raise exception
		} else {
			this.waterVolume -= transferedVolume;
			nextTickWaterVolume -= transferedVolume;
		}
	}

	public void add(double transferedVolume) {
		this.waterVolume += transferedVolume;
	}

	@Override
	public Cell deepCopy() {
		return new WaterCell(getCoords(), waterVolume);
	}

	public double getEmptyVolume() {
		return BASE_VOLUME - waterVolume;
	}

	public boolean isFull() {
		return getEmptyVolume() <= 0;
	}

	public boolean isFullyPressured() {
		return BASE_VOLUME * 2 - waterVolume <= 0;
	}

	public double getWaterVolume() {
		return waterVolume;
	}

	@Override
	public String toString() {
		return "[" + getCoords().x + "," + getCoords().y + "] : " + waterVolume + " Water";
	}

	public double getPortionFilled() {
		return waterVolume / BASE_VOLUME;
	}

	public float getPressure() {
		float pressure = (float) (waterVolume) / 200.0f;
		if (pressure > 1.0f) {
			return 1.0f;
		} else {
			return pressure;
		}
	}

	public boolean isFalling() {
		return falling;
	}
}
