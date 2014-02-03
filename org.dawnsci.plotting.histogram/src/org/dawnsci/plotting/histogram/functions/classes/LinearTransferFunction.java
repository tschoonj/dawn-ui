package org.dawnsci.plotting.histogram.functions.classes;



/**
 * Very basic transfer function definition which provides a simple linear function
 * @author ssg37927
 *
 */
public class LinearTransferFunction extends AbstractTransferFunction {
	
	@Override
	public int[] getArray() {
		int[] result = new int[256];
		for (int i = 0; i < result.length; i++) {
			result[i] = i;
		}
		return result;
	}

	@Override
	public double getPoint(double value) {
		return 0;
	}

}
