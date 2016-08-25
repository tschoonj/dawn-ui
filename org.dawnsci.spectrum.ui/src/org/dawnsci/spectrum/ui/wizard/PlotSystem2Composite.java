package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.dawb.common.ui.widgets.ActionBarWrapper;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.dawnsci.plotting.api.region.IROIListener;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.dawnsci.plotting.api.region.IRegion.RegionType;
import org.eclipse.dawnsci.plotting.api.region.ROIEvent;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class PlotSystem2Composite extends Composite {



    private IPlottingSystem<Composite> plotSystem2;
    private IDataset image1;



    
    
    public PlotSystem2Composite(Composite parent, int style
    		, AggregateDataset aggDat, ExampleModel model) throws Exception {
        super(parent, style);
        //composite = new Composite(parent, SWT.NONE);

        new Label(this, SWT.NONE).setText("Region of Interest");
        
        try {
			plotSystem2 = PlottingFactory.createPlottingSystem();
		} 
        catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        

        this.createContents(aggDat, model); 
//        System.out.println("Test line");
        
    }
     
    public void createContents(AggregateDataset aggDat, ExampleModel model) throws Exception {

    	
    	final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        setLayout(gridLayout);
        
        ActionBarWrapper actionBarComposite = ActionBarWrapper.createActionBars(this, null);;
        
        plotSystem2.createPlotPart(PlotSystem2Composite.this, "ExamplePlot2", actionBarComposite, PlotType.IMAGE, null);


        
		model.addPropertyChangeListener(new PropertyChangeListener() {
		
		
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IROI box = model.getROI();
				IRectangularROI roi = box.getBounds();
				int[] newLen = roi.getIntLengths();
				int[] newPt = roi.getIntPoint();
				int[][] newLenPt = {newLen, newPt};
				IDataset j = ImageSlicerUtils.ImageSliceUpdate(model.getSliderPos(), aggDat,newLenPt);
				image1= j;
				plotSystem2.createPlot2D(j, null, null);
				model.setCurrentImage(j);

				
			}
		});
		
		
		

   
        
        final GridData gd_secondField = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_secondField.grabExcessVerticalSpace = true;
        gd_secondField.grabExcessVerticalSpace = true;
        

//        plotSystem2.createPlotPart(this, "ExamplePlot2", actionBarComposite, PlotType.IMAGE, null);
        plotSystem2.getPlotComposite().setLayoutData(gd_secondField);
//        plotSystem2.createPlot2D(image1, null, null);
        


		}
   
   
   public Composite getComposite(){   	
   	return this;
   }
   
   public IPlottingSystem<Composite> getPlotSystem(){
	   return plotSystem2;
   }

   public IDataset getImage(){
	   return image1;
   }
   
//   public IRegion returnRegion(){
//	   return region;
//   }
   
   
   
   

}
