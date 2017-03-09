package org.dawnsci.plotting.tools.finding;

import java.util.EventListener;

import org.eclipse.january.dataset.IDataset;

/**
 * TODO: data pass for peaks
 * TODO: region bounds might be better off as seperate
 * @author Dean P. Ottewell
 *
 */
public interface IPeakOpportunityListener extends EventListener {

	//TODO: primarily use the change event for now.
	public void peaksChanged(PeakOpportunityEvent  evt);
	
	public void boundsChanged(double upper, double lower);
	
	public void dataChanged(IDataset nXData, IDataset nYData);
	
	
}
