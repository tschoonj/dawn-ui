package org.dawnsci.spectrum.ui.wizard;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;

public class DummyProcessingClass {
	
	
	@SuppressWarnings("incomplete-switch")
	public static IDataset DummyProcess(IDataset input, ExampleModel model, DataModel dm){
		
		IDataset output =null;
		
		switch(model.getMethodology()){
			case TWOD_TRACKING:
				TwoDTracking twoDTracking = new TwoDTracking();
				output = twoDTracking.TwoDTracking1(input, model, dm);
			case TWOD:
				output = TwoDFitting.TwoDFitting1(input, model);
		}
		
		double yValue = (Double) DatasetUtils.cast(output, Dataset.FLOAT64).sum();
		
		dm.addyList(yValue);
		dm.addOutputDatArray(output);
		
		return output;
	}
}
