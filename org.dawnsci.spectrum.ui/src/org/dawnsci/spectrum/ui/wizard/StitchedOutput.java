package org.dawnsci.spectrum.ui.wizard;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.trace.ILineTrace;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class StitchedOutput {

	public  static IDataset[][] curveStitch (IPlottingSystem<Composite> plotSystem, ArrayList<Button> datSelector
			, ArrayList<DataModel> dms, SuperModel sm, DatDisplayer datDisplayer ){
		
		ILineTrace lt1 = plotSystem.createLineTrace("Concatenated Curve Test");
		
		ArrayList<IDataset> xArrayList = new ArrayList<>();
		ArrayList<IDataset> yArrayList = new ArrayList<>();
		ArrayList<IDataset> yArrayListFhkl = new ArrayList<>();
		
		for(int b = 0;b<datSelector.size();b++){
			if (datSelector.get(b).getSelection()){
				
				int p = (Arrays.asList(datDisplayer.getSelector().getItems())).indexOf(datSelector.get(b).getText());
				
				if (dms.get(p).getyList() == null || dms.get(p).getxList() == null) {
					
				} else {
						xArrayList.add(dms.get(p).xIDataset());
						yArrayList.add(dms.get(p).yIDataset());
						yArrayListFhkl.add(dms.get(p).yIDatasetFhkl());
		    		}
				}
		}
		
		IDataset[] xArray= new IDataset[xArrayList.size()];
		IDataset[] yArray= new IDataset[yArrayList.size()];
		IDataset[] yArrayFhkl= new IDataset[yArrayListFhkl.size()];
		
		for (int b = 0; b< xArrayList.size(); b++){
			xArray[b] = xArrayList.get(b);
			yArray[b] = yArrayList.get(b);
			yArrayFhkl[b] = yArrayListFhkl.get(b);
		}
		
		IDataset[] xArrayCorrected = xArray.clone();
		IDataset[] yArrayCorrected = yArray.clone();
		IDataset[] yArrayCorrectedFhkl = yArrayFhkl.clone();
		
		
		IDataset[][] attenuatedDatasets = new IDataset[2][];
		
		IDataset[][] attenuatedDatasetsFhkl = new IDataset[2][];
		
		
		double[][] maxMinArray = OverlapFinderSXRD.overlapFinderOperation(xArray);
				
		double attenuationFactor =1;
		double attenuationFactorFhkl =1;
				
		double[] correctionRatioArray = new double[xArray.length];
		double[] correctionRatioArrayFhkl = new double[xArray.length];
		
		correctionRatioArray[0]=1;
		correctionRatioArrayFhkl[0]=1;
				
				//TEST
				for (int k=0; k<xArray.length-1;k++){
					
					ArrayList<Integer> overlapLower = new ArrayList<Integer>();
					ArrayList<Integer> overlapHigher = new ArrayList<Integer>();
					
					
					for(int l=0; l<xArrayCorrected[k].getSize();l++){
						if (xArrayCorrected[k].getDouble(l)>=maxMinArray[k][1]){
							overlapLower.add(l);
						}
					}
					for(int m=0; m<xArrayCorrected[k+1].getSize();m++){
						if (xArrayCorrected[k+1].getDouble(m)<maxMinArray[k][0]){
							overlapHigher.add(m);
						}
					}
					
							
					Dataset[] xLowerDataset =new Dataset[1];
					Dataset yLowerDataset =null;
					Dataset yLowerDatasetFhkl =null;
					Dataset[] xHigherDataset =new Dataset[1];
					Dataset yHigherDataset =null;
					Dataset yHigherDatasetFhkl =null;
						
					ArrayList<Double> xLowerList =new ArrayList<>();
					ArrayList<Double> yLowerList =new ArrayList<>();
					ArrayList<Double> yLowerListFhkl =new ArrayList<>();
					ArrayList<Double> xHigherList =new ArrayList<>();
					ArrayList<Double> yHigherList =new ArrayList<>();
					ArrayList<Double> yHigherListFhkl =new ArrayList<>();
					
					for (int l=0; l<overlapLower.size(); l++){
						xLowerList.add(xArray[k].getDouble(overlapLower.get(l)));
						yLowerList.add(yArray[k].getDouble(overlapLower.get(l)));
						yLowerListFhkl.add(yArrayFhkl[k].getDouble(overlapLower.get(l)));
						
						xLowerDataset[0] = DatasetFactory.createFromObject(xLowerList);
						yLowerDataset = DatasetFactory.createFromObject(yLowerList);
						yLowerDatasetFhkl = DatasetFactory.createFromObject(yLowerListFhkl);
					}
							
					for (int l=0; l<overlapHigher.size(); l++){
						xHigherList.add(xArray[k+1].getDouble(overlapHigher.get(l)));
						yHigherList.add(yArray[k+1].getDouble(overlapHigher.get(l)));
						yHigherListFhkl.add(yArrayFhkl[k+1].getDouble(overlapHigher.get(l)));
						
						xHigherDataset[0] = DatasetFactory.createFromObject(xHigherList);
						yHigherDataset = DatasetFactory.createFromObject(yHigherList);
						yHigherDatasetFhkl = DatasetFactory.createFromObject(yHigherListFhkl);
					}
						
					double correctionRatio = PolynomialOverlapSXRD.correctionRatio(xLowerDataset, yLowerDataset, 
							xHigherDataset, yHigherDataset, attenuationFactor,4);
					
					double correctionRatioFhkl = PolynomialOverlapSXRD.correctionRatio(xLowerDataset, yLowerDatasetFhkl, 
							xHigherDataset, yHigherDatasetFhkl, attenuationFactor,4);
					
					attenuationFactor = correctionRatio;
					attenuationFactorFhkl = correctionRatioFhkl;
						
					yArrayCorrected[k+1] = Maths.multiply(yArray[k+1],attenuationFactor);
					yArrayCorrectedFhkl[k+1] = Maths.multiply(yArrayFhkl[k+1],attenuationFactorFhkl);
					
						
					System.out.println("attenuation factor:  " + attenuationFactor + "   k:   " +k);
					System.out.println("attenuation factor Fhkl:  " + attenuationFactorFhkl + "   k:   " +k);
						
					}
			
		attenuatedDatasets[0] = yArrayCorrected;
		attenuatedDatasets[1] = xArrayCorrected;
			
		attenuatedDatasetsFhkl[0] = yArrayCorrectedFhkl;
		attenuatedDatasetsFhkl[1] = xArrayCorrected;
		
		
		
		Dataset[] sortedAttenuatedDatasets = new Dataset[3];
		
		sortedAttenuatedDatasets[0]=DatasetUtils.convertToDataset(DatasetUtils.concatenate(attenuatedDatasets[0], 0));
		sortedAttenuatedDatasets[1]=DatasetUtils.convertToDataset(DatasetUtils.concatenate(attenuatedDatasets[1], 0));
		sortedAttenuatedDatasets[2]=DatasetUtils.convertToDataset(DatasetUtils.concatenate(attenuatedDatasetsFhkl[0], 0));
		
		DatasetUtils.sort(sortedAttenuatedDatasets[1],
				sortedAttenuatedDatasets[0]);
		
		DatasetUtils.sort(sortedAttenuatedDatasets[2],
				sortedAttenuatedDatasets[0]);

		String b =  CurveStateIdentifier.CurveStateIdentifier1(plotSystem);
		
		if (b == "f"){
				lt1.setData(sortedAttenuatedDatasets[1], sortedAttenuatedDatasets[0]);
				lt1.setName("Spliced Curve_Fhkl");
		}
		else{
			lt1.setData(sortedAttenuatedDatasets[1], sortedAttenuatedDatasets[2]);
			lt1.setName("Spliced Curve_Intensity");
		}
		
		
		sm.setSplicedCurveY(sortedAttenuatedDatasets[0]);
		sm.setSplicedCurveX(sortedAttenuatedDatasets[1]);
		sm.setSplicedCurveYFhkl(sortedAttenuatedDatasets[2]);
		
		
		
		plotSystem.addTrace(lt1);
		plotSystem.repaint();	
		
		return null;
	}
}
