/*-
 * Copyright 2016 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.dawnsci.spectrum.ui.wizard;

import org.eclipse.dawnsci.analysis.api.processing.OperationException;
import org.eclipse.dawnsci.analysis.dataset.slicer.SliceFromSeriesMetadata;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.Maths;

import uk.ac.diamond.scisoft.analysis.dataset.function.Interpolation1D;
import uk.ac.diamond.scisoft.analysis.processing.operations.reflectivityandsxrd.RecoverNormalisationFluxBatch;

public class ReflectivityFluxCorrectionsForDialog{

	
	protected static Dataset reflectivityFluxCorrections (IDataset input, String path){

		/*
		 * Method takes in a 2D image (Dataset) and returns flux corrected image.
		 */
		
		SliceFromSeriesMetadata tmp1 = input.getFirstMetadata(SliceFromSeriesMetadata.class);
		IDataset m = null;
		
		Dataset[] fluxData = RecoverNormalisationFluxBatch.normalisationFlux(input, path);
		
		ILazyDataset qdcd = null;
		
		if ((boolean) (path.equalsIgnoreCase("NO") ||(path.equalsIgnoreCase(null)))){
			try {
				qdcd = ProcessingUtilsForDialog.getLazyDataset(input.getFirstMetadata(SliceFromSeriesMetadata.class).getFilePath(), ReflectivityMetadataTitlesForDialog.getqsdcd()).getSlice();
				m = tmp1.getMatchingSlice(qdcd);
			} catch (OperationException e) {
				// TODO Auto-generated catch block
				
			} catch (DatasetException e) {
				// TODO Auto-generated catch block
			}
		}
		
		else{
			try {
				qdcd = ProcessingUtilsForDialog.getLazyDataset(tmp1.getFilePath(), ReflectivityMetadataTitlesForDialog.getqdcd()).getSlice();
				m = tmp1.getMatchingSlice(qdcd);
			} catch (OperationException e) {
				// TODO Auto-generated catch block
			} catch (DatasetException e) {
				// TODO Auto-generated catch block
			}
		}
		
		
		//test1
		Dataset flux =  (Dataset) Interpolation1D.splineInterpolation(fluxData[0], fluxData[1], m);;

		
		Dataset output = Maths.divide(1,flux);
		
		
		return output;
		}
}
//test

