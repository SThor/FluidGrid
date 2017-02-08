package FluidGrid;

/**
 * Created by silmathoron on 08/02/2017.
 */
public class Settings {
	private static final double DEFAULT_BASE_VOLUME = 100;
	private static final double DEFAULT_PRECISION = 0.001;
	private static final double DEFAULT_PRESSURE_DIFFERENCE = 0.1;
	private static final double DEFAULT_FLOW_DOWN_UNDER_PRESSURE = 0.08;
	private static final double DEFAULT_FLOW_UP_UNDER_PRESSURE = 0.08;

	private static double baseVolume;
	private static double precision;
	private static double pressureDifference;
	private static double flowDownUnderPressure;
	private static double flowUpUnderPressure;

	public static double getBaseVolume() {
		return baseVolume;
	}

	public static void setBaseVolume(double baseVolume) {
		Settings.baseVolume = baseVolume;
	}

	public static double getPrecision() {
		return precision;
	}

	public static void setPrecision(double precision) {
		Settings.precision = precision;
	}

	public static double getPressureDifference() {
		return pressureDifference;
	}

	public static void setPressureDifference(double pressureDifference) {
		Settings.pressureDifference = pressureDifference;
	}

	public static double getFlowDownUnderPressure() {
		return flowDownUnderPressure;
	}

	public static void setFlowDownUnderPressure(double flowDownUnderPressure) {
		Settings.flowDownUnderPressure = flowDownUnderPressure;
	}

	public static double getFlowUpUnderPressure() {
		return flowUpUnderPressure;
	}

	public static void setFlowUpUnderPressure(double flowUpUnderPressure) {
		Settings.flowUpUnderPressure = flowUpUnderPressure;
	}

	public static void setFlowUnderPressure(double flowUnderPressure) {
		Settings.flowUpUnderPressure = flowUnderPressure;
		Settings.flowDownUnderPressure = flowUnderPressure;
	}
}
