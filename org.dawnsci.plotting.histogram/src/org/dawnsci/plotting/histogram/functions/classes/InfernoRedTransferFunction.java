package org.dawnsci.plotting.histogram.functions.classes;

import org.dawnsci.plotting.histogram.data.HistogramData;
import org.dawnsci.plotting.histogram.data.HistogramData.RGBChannel;

public class InfernoRedTransferFunction extends AbstractTransferFunction {

	@Override
	public double getPoint(double value) {
		return HistogramData.getPointFromRGBData(value, HistogramData.INFERNO, RGBChannel.RED);

	}

}
