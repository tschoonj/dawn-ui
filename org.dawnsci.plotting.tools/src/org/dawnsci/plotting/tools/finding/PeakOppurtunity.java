package org.dawnsci.plotting.tools.finding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.january.dataset.IDataset;

import uk.ac.diamond.scisoft.analysis.peakfinding.Peak;

/**
 * @author Dean P. Ottewell
 *
 */
public class PeakOppurtunity implements IPeakOpportunity {

	//TODO: tmp default intialise
	List<Peak> peaks = new ArrayList<Peak>();
	private IDataset xData; 
	private IDataset yData;
	
	@Override
	public List<Peak> getPeaks() {
		return peaks;
	}

	@Override
	public IDataset getXData() {
		return xData;
	}

	@Override
	public IDataset getYData() {
		return yData;
	}

	@Override
	public void setPeaks(List<Peak> peaks) {
		this.peaks = peaks;
	}

	@Override
	public void setXData(IDataset xData) {
		this.xData = xData;
	}

	@Override
	public void setYData(IDataset yData) {
		this.yData = yData;
	}
}
