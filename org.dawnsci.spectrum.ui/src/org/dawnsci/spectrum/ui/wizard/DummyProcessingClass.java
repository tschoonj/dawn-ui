package org.dawnsci.spectrum.ui.wizard;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;

public class DummyProcessingClass {
	
	
	@SuppressWarnings("incomplete-switch")
	public static IDataset DummyProcess(IDataset input, ExampleModel model,DataModel  dm,  GeometricParametersModel gm){
		
		IDataset output =null;
		
		switch(model.getMethodology()){
			case TWOD_TRACKING:
				TwoDTracking twoDTracking = new TwoDTracking();
				output = twoDTracking.TwoDTracking1(input, model, dm);
			case TWOD:
				output = TwoDFitting.TwoDFitting1(input, model);
		}
		
		
		//Double yValue =(Double) DatasetUtils.cast(output,Dataset.FLOAT64).sum();
		
		//(Double) DatasetUtils.cast
		
		
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
			// TODO Auto-generated catch block
		}
		
		Dataset yValue = Maths.multiply(output, correction);
		
		Double fhkl =Math.pow((Double) DatasetUtils.cast(yValue,Dataset.FLOAT64).sum(), 0.5);
		
		dm.addyList(fhkl);
		dm.addOutputDatArray(output);
		
		return output;
	}
}
