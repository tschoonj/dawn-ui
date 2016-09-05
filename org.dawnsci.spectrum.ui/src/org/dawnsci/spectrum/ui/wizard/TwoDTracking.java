/*-
 * Copyright 2016 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.dawnsci.spectrum.ui.wizard;

import java.util.ArrayList;

import org.dawnsci.boofcv.BoofCVImageTrackerServiceCreator;
import org.eclipse.dawnsci.analysis.api.image.IImageTracker;
import org.eclipse.dawnsci.analysis.api.image.IImageTracker.TrackerType;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.IndexIterator;
import org.eclipse.january.dataset.LinearAlgebra;
import org.eclipse.january.dataset.Maths;

import uk.ac.diamond.scisoft.analysis.fitting.functions.Polynomial2D;

/**
 * Cuts out the region of interest and fits it with a 2D polynomial background.
 */

public class TwoDTracking{
	
	private IImageTracker tracker = null;
	private Polynomial2D g2;
	private double[] location;
	private double[] initialLocation;
	private IDataset input1;
	private RectangularROI box;
	private Dataset in1;
	private int[] len;
	private int[] pt;
	

	public IDataset TwoDTracking1(IDataset input, ExampleModel model, DataModel dm) {
		
		RectangularROI box = model.getBox();
		
		box = model.getBox();
		
		input1=input;
		
		len = model.getLenPt()[0];
		pt = model.getLenPt()[1];
		
		initialLocation = new double[] {(double) pt[1],(double)pt[0], (double) (pt[1] +len[1]),(double) (pt[0]),(double) pt[1],
				(double) pt[0]+len[0], (double) (pt[1]+len[1]),(double) (pt[0]+len[0])};
		
		if (model.getInput() == null){	
			len = model.getLenPt()[0];
			pt = model.getLenPt()[1];
			
			in1 = BoxSlicerRodScanUtilsForDialog.rOIBox(input, len, pt);
			tracker =  BoofCVImageTrackerServiceCreator.createImageTrackerService();
			
			initialLocation = new double[] {(double) pt[1],(double)pt[0], (double) (pt[1] +len[1]),(double) (pt[0]),(double) pt[1],
					(double) pt[0]+len[0], (double) (pt[1]+len[1]),(double) (pt[0]+len[0])};
			try {
				tracker.initialize(input, initialLocation, TrackerType.TLD);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
//			System.out.println("Loop No:  " + model.getLoopNo() + "  initialLocation:  " + initialLocation[0] +" , "+ initialLocation[1] +" , "+ initialLocation[6] +" , "+ initialLocation[7]);
			model.setTrackerCoordinates(new double[]{initialLocation[1], initialLocation[0], 
					initialLocation[5], initialLocation[0], initialLocation[1], initialLocation[2], 
					initialLocation[5], initialLocation[2]});
			model.setInput(input);
			location =initialLocation;
		}
		
		
		else{
//			System.out.println("In here!");
			
			
			try {
//				System.out.println("First tracker line");
				//tracker = new IImageTracker
				//IImageTracker tracker1 = null;
				tracker =  BoofCVImageTrackerServiceCreator.createImageTrackerService();
				tracker.initialize(model.getInput(), model.getTrackerCoordinates(), TrackerType.TLD);
				//System.out.println("Now In here!");
				location = tracker.track(input);
				if (location != null){
					model.setTrackerCoordinates(location);
				}
				
				int[] len1 = model.getLenPt()[0];
				
				int[] newPt = new int[] {(int) location[0],(int) location[1]};
				int[][] newLenPt = new int[2][];
				newLenPt[0] = len1;
				newLenPt[1] = newPt;
				model.setLenPt(newLenPt);
				
				System.out.println("~~~~~~~~~~~~~~~~~~~success!~~~~~~~~~~~~~~~~~");
			} catch (Exception e) {
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Failed to track");// TODO Auto-generated catch block
//				if (model.getTrackerCoordinates() == initialLocation){
//					
//				}
//				if (model.getTrackerCoordinates() != initialLocation && model.getTrackerCoordinates() != )
//				
//				model.setTrackerCoordinates(new double[]{initialLocation[1], initialLocation[0], 
//						initialLocation[5], initialLocation[0], initialLocation[1], initialLocation[2], 
//						initialLocation[5], initialLocation[2]});
//				model.setInput(input);
//				
				
			}
			
		}
		
		if (location ==null){
			location = model.getTrackerCoordinates();
		}

		
		len = model.getLenPt()[0];
		pt = model.getLenPt()[1];

		in1 = BoxSlicerRodScanUtilsForDialog.rOIBox(input, len, pt);
	
		if (g2 == null)
			g2 = new Polynomial2D(AnalaysisMethodologies.toInt(model.getFitPower()));
		if ((int) Math.pow(AnalaysisMethodologies.toInt(model.getFitPower()) + 1, 2) != g2.getNoOfParameters())
			g2 = new Polynomial2D(AnalaysisMethodologies.toInt(model.getFitPower()));
	
		Dataset[] fittingBackground = BoxSlicerRodScanUtilsForDialog.LeftRightTopBottomBoxes(input, len,
				pt, model.getBoundaryBox());
		
		Dataset offset = DatasetFactory.ones(fittingBackground[2].getShape(), Dataset.FLOAT64);
		
		System.out.println("Tracker position:  " + location[1] + " , " + location[0]);
		
		Dataset intermediateFitTest = Maths.add(offset, fittingBackground[2]);
		Dataset matrix = LinearLeastSquaresServicesForDialog.polynomial2DLinearLeastSquaresMatrixGenerator(
				AnalaysisMethodologies.toInt(model.getFitPower()), fittingBackground[0], fittingBackground[1]);
		
		DoubleDataset test = (DoubleDataset)LinearAlgebra.solveSVD(matrix, intermediateFitTest);
		double[] params = test.getData();
		
		DoubleDataset in1Background = g2.getOutputValues0(params, len, model.getBoundaryBox(),
				AnalaysisMethodologies.toInt(model.getFitPower()));
	
		IndexIterator it = in1Background.getIterator();
	
		while (it.hasNext()) {
			double v = in1Background.getElementDoubleAbs(it.index);
			if (v < 0)
				in1Background.setObjectAbs(it.index, 0);
		}
	
		dm.addBackgroundDatArray(in1Background);
		
		Dataset pBackgroundSubtracted = Maths.subtract(in1, in1Background, null);
	
		pBackgroundSubtracted.setName("pBackgroundSubtracted");
	
		IndexIterator it1 = pBackgroundSubtracted.getIterator();
	
		while (it1.hasNext()) {
			double q = pBackgroundSubtracted.getElementDoubleAbs(it1.index);
			if (q < 0)
				pBackgroundSubtracted.setObjectAbs(it1.index, 0);
		}
		
		Dataset output = DatasetUtils.cast(pBackgroundSubtracted, Dataset.FLOAT64);
		
		dm.addOutputDatArray(output);
		
		output.setName("Region of Interest, polynomial background removed");
		
		
		
		return output;
	}
	
	public void resetTracker(){
		tracker =null;
	}

}