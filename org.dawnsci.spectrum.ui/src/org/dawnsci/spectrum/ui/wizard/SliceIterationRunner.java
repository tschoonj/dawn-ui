package org.dawnsci.spectrum.ui.wizard;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;

public class SliceIterationRunner {
	
	public static IDataset sliceIterationRunner1 (ExampleModel model, DataModel dm){
	 
		IDataset output = null;
		SliceND slice = new SliceND(model.getAggDat().getShape());
		int k = 0;
		
		for (k = model.getSliderPos(); k<model.getAggDat().getShape()[0]; k++){
			dm.addxList((double) k);
			IDataset j = null;
			slice.setSlice(0, k, k+1, 1);
			try {
				j = model.getAggDat().getSlice(slice);
				} 
				catch (Exception e1) {
				}
				
			j.squeeze();
			
//			model.setIterationMarker((model.getIterationMarker())+1);
			System.out.println("Iteration marker:    " + model.getIterationMarker());
//			model.setImageNumber(k);
			
			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			output = DummyProcessingClass.DummyProcess(j, model, dm);
		}
		
		
		return output;
	}

}
