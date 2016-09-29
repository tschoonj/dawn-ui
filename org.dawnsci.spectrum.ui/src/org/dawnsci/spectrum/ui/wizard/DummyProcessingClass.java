package org.dawnsci.spectrum.ui.wizard;

import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.region.IRegion.RegionType;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class DummyProcessingClass {
	
	
	@SuppressWarnings("incomplete-switch")
	public static IDataset DummyProcess(IDataset input, ExampleModel model
			, DataModel dm, GeometricParametersModel gm, PlotSystemComposite customComposite){
		
		IDataset output =null;
		IPlottingSystem<Composite> pS = customComposite.getPlotSystem();
		
		switch(model.getMethodology()){
			case TWOD_TRACKING:
				if (pS.getRegion("Background Region")!=null){
					pS.removeRegion(pS.getRegion("Background Region"));
				}
				else{
				}				
				TwoDTracking twoDTracking = new TwoDTracking();
				output = twoDTracking.TwoDTracking1(input, model, dm);
				break;
			case TWOD:
				if (pS.getRegion("Background Region")!=null){
					pS.removeRegion(pS.getRegion("Background Region"));
				}
				else{
				}
				output = TwoDFitting.TwoDFitting1(input, model);
				break;
			case SECOND_BACKGROUND_BOX:
				output = SecondConstantROI.secondROIConstantBg(input, model, customComposite, dm);
				break;
			case OVERLAPPING_BACKGROUND_BOX:
				output = OverlappingBackgroundBox.OverlappingBgBox(input, model, customComposite, dm);
				break;
		}
		
		Dataset correction = null;
		
		try {
			correction = Maths.multiply(SXRDGeometricCorrections.lorentz(model), SXRDGeometricCorrections.areacor(model
					, gm.getBeamCorrection(), gm.getSpecular(),  gm.getSampleSize()
					, gm.getOutPlaneSlits(), gm.getInPlaneSlits(), gm.getBeamInPlane()
					, gm.getBeamOutPlane(), gm.getDetectorSlits()));
			correction = Maths.multiply(SXRDGeometricCorrections.polarisation(model, gm.getInplanePolarisation()
					, gm.getOutplanePolarisation()), correction);
			correction = Maths.multiply(
					SXRDGeometricCorrections.polarisation(model, gm.getInplanePolarisation(), gm.getOutplanePolarisation()),
					correction);
		} catch (DatasetException e) {

		}
		
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Dataset yValue = Maths.multiply(output, correction);
		
		
		Double intensity = (Double) DatasetUtils.cast(yValue,Dataset.FLOAT64).sum();
		Double fhkl =Math.pow((Double) DatasetUtils.cast(yValue,Dataset.FLOAT64).sum(), 0.5);
		
		dm.addyList(intensity);
		dm.addyListFhkl(fhkl);
		dm.addOutputDatArray(output);
		
		return output;
	}
}
