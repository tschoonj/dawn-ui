package org.dawnsci.spectrum.ui.wizard;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.Maths;

import uk.ac.diamond.scisoft.analysis.fitting.Fitter;
import uk.ac.diamond.scisoft.analysis.fitting.functions.Polynomial;

public class PolynomialOverlapSXRD {

	
	public static double correctionRatio(Dataset[] xLowerDataset, Dataset yLowerDataset,
			Dataset[] xHigherDataset, Dataset yHigherDataset, double attenuationFactor, int power) {
	
	
		Polynomial polyFitLower = Fitter.polyFit(xLowerDataset, yLowerDataset, 1e-5,power);
		Polynomial polyFitHigher = Fitter.polyFit(xHigherDataset, yHigherDataset, 1e-5,power);
		
		Dataset calculatedValuesHigher = polyFitHigher.calculateValues(xLowerDataset);
		Dataset calculatedValuesLower = polyFitLower.calculateValues(xLowerDataset);
		
		Dataset correctionsRatioDataset = Maths.divide(calculatedValuesLower.sum(), 
				calculatedValuesHigher.sum());
		
		
		double correction = ((double) correctionsRatioDataset.sum())/((double) correctionsRatioDataset.getSize())*attenuationFactor;
		
		
		return correction;
	}
	
}
