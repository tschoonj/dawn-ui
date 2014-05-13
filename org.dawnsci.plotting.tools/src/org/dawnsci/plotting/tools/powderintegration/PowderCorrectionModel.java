package org.dawnsci.plotting.tools.powderintegration;

import org.dawb.common.ui.databinding.AbstractModelObject;

public class PowderCorrectionModel extends AbstractModelObject {

	boolean isApplySolidAngleCorrection = false;
	boolean isApplyPolarisationCorrection = false;
	double polarisationFactor = 0.9;
	double polarisationAngularOffset = 0;
	
	public boolean isApplySolidAngleCorrection() {
		return isApplySolidAngleCorrection;
	}
	public boolean isApplyPolarisationCorrection() {
		return isApplyPolarisationCorrection;
	}
	public double getPolarisationFactor() {
		return polarisationFactor;
	}
	public double getPolarisationAngularOffset() {
		return polarisationAngularOffset;
	}
	public void setApplySolidAngleCorrection(boolean isApplySolidAngleCorrection) {
		firePropertyChange("isApplySolidAngleCorrection", this.isApplySolidAngleCorrection, this.isApplySolidAngleCorrection = isApplySolidAngleCorrection);
	}
	public void setApplyPolarisationCorrection(boolean isApplyPolarisationCorrection) {
		firePropertyChange("isApplyPolarisationCorrection", this.isApplyPolarisationCorrection, this.isApplyPolarisationCorrection = isApplyPolarisationCorrection);
	}
	public void setPolarisationFactor(double polarisationFactor) {
		firePropertyChange("polarisationFactor", this.polarisationFactor, this.polarisationFactor = polarisationFactor);
	}
	public void setPolarisationAngularOffset(double polarisationAngularOffset) {
		firePropertyChange("polarisationAngularOffset", this.polarisationAngularOffset, this.polarisationAngularOffset = polarisationAngularOffset);
	}
}