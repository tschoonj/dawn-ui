package org.dawnsci.plotting.tools.finding;

import java.util.List;

import org.eclipse.january.dataset.IDataset;

import uk.ac.diamond.scisoft.analysis.peakfinding.Peak;

public class PeakOppurtunity implements IPeakOpportunity {

	List<Peak> peaks;
	IDataset xData;
	IDataset yData;
	
	@Override
	public List<Peak> getPeaks() {
		return peaks;
	}

	@Override
	public IDataset getRawXData() {
		return xData;
	}

	@Override
	public IDataset getRawYData() {
		return yData;
	}

	@Override
	public void setPeaks(List<Peak> peaks) {
		this.peaks = peaks;
	}

	@Override
	public void setRawXData(IDataset xData) {
		this.xData = xData;
	}

	@Override
	public void getRawYData(IDataset yData) {
		this.yData = yData;
	}
}
