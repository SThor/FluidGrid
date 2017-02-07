package FluidGrid.cell;

import java.awt.*;

/**
 * Created by silmathoron on 31/01/2017.
 */
public class Wall extends Cell {

	public Wall(Point c) {
		super(c);
	}

	@Override
	public Cell deepCopy() {
		return new Wall(getCoords());
	}

	@Override
	public String toString() {
		return "[" + getCoords().x + "," + getCoords().y + "] : Wall";
	}
}
