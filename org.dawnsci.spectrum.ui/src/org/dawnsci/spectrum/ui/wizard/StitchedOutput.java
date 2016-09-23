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
		
		IDataset[] xArray= new IDataset[datSelector.size()];
		IDataset[] yArray= new IDataset[datSelector.size()];
		
		for(int b = 0;b<datSelector.size();b++){
			if (datSelector.get(b).getSelection()){
				
				int p = (Arrays.asList(datDisplayer.getSelector().getItems())).indexOf(datSelector.get(b).getText());
				
				if (dms.get(p).getyList() == null || dms.get(p).getxList() == null) {
					
					//IDataset filler  = dms.get(0).backupDataset();
					
				} else {
						xArray[b] = (dms.get(p).xIDataset());
						yArray[b] = (dms.get(p).yIDataset());
		    		}
				}
		}
		IDataset[] xArrayCorrected = xArray.clone();
		IDataset[] yArrayCorrected = yArray.clone();
		
		IDataset[][] attenuatedDatasets = new IDataset[2][];
				
		double[][] maxMinArray = OverlapFinderSXRD.overlapFinderOperation(xArray);
				
		double attenuationFactor =1;
				
		double[] correctionRatioArray = new double[xArray.length];
		correctionRatioArray[0]=1;
				
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
					Dataset[] xHigherDataset =new Dataset[1];
					Dataset yHigherDataset =null;
						
					ArrayList<Double> xLowerList =new ArrayList<>();
					ArrayList<Double> yLowerList =new ArrayList<>();
					ArrayList<Double> xHigherList =new ArrayList<>();
					ArrayList<Double> yHigherList =new ArrayList<>();
							
					for (int l=0; l<overlapLower.size(); l++){
						xLowerList.add(xArray[k].getDouble(overlapLower.get(l)));
						yLowerList.add(yArray[k].getDouble(overlapLower.get(l)));
						xLowerDataset[0] = DatasetFactory.createFromObject(xLowerList);
						yLowerDataset = DatasetFactory.createFromObject(yLowerList);
					}
							
					for (int l=0; l<overlapHigher.size(); l++){
						xHigherList.add(xArray[k+1].getDouble(overlapHigher.get(l)));
						yHigherList.add(yArray[k+1].getDouble(overlapHigher.get(l)));
						xHigherDataset[0] = DatasetFactory.createFromObject(xHigherList);
						yHigherDataset = DatasetFactory.createFromObject(yHigherList);
					}
						
					double correctionRatio = PolynomialOverlapSXRD.correctionRatio(xLowerDataset, yLowerDataset, 
							xHigherDataset, yHigherDataset, attenuationFactor,4);
					
					attenuationFactor = correctionRatio;
						
					yArrayCorrected[k+1] = Maths.multiply(yArray[k+1],attenuationFactor);
						
					System.out.println("attenuation factor:  " + attenuationFactor + "   k:   " +k);
						
					}
			
		attenuatedDatasets[0] = yArrayCorrected;
		attenuatedDatasets[1] = xArrayCorrected;
			
		
		Dataset[] sortedAttenuatedDatasets = new Dataset[2];
		
		sortedAttenuatedDatasets[0]=DatasetUtils.convertToDataset(DatasetUtils.concatenate(attenuatedDatasets[0], 0));
		sortedAttenuatedDatasets[1]=DatasetUtils.convertToDataset(DatasetUtils.concatenate(attenuatedDatasets[1], 0));
		
		DatasetUtils.sort(sortedAttenuatedDatasets[1],
				sortedAttenuatedDatasets[0]);

		lt1.setData(sortedAttenuatedDatasets[1], sortedAttenuatedDatasets[0]);

		plotSystem.addTrace(lt1);
		plotSystem.repaint();	
		
		return null;
	}
}
