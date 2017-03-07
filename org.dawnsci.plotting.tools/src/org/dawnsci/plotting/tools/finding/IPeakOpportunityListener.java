package org.dawnsci.plotting.tools.finding;

import java.util.EventListener;

/**
 * TODO: data pass for peaks
 * TODO: region bounds might be better off as seperate
 * @author Dean P. Ottewell
 *
 */
public interface IPeakOpportunityListener extends EventListener {

	//TODO: primarily use the change event for now.
	public void peaksChanged(PeakOpportunityEvent evt);
	
	//TODO: realyl whats the difference between adding and removing event... is this distiction important?
//	public void peaksAdded(PeakOpportunityEvent evt);
//  public void peaksRemoved(PeakOpportunityEvent evt);
	
	//public void regionBoundsAdjustment();
	
}
