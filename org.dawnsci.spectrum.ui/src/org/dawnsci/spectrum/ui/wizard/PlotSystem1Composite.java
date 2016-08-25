package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.dawb.common.ui.widgets.ActionBarWrapper;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotSystem1Composite  extends Composite {

	private final static Logger logger = LoggerFactory.getLogger(PlotSystem1Composite.class);

    private IPlottingSystem<Composite> plotSystem1;
    private IDataset image1;
    private Button button; 
    private Button button1;
    private Button button2;
    
    
    public PlotSystem1Composite(Composite parent, int style
    		, AggregateDataset aggDat, String test0, String test1, ExampleModel model, DataModel dm) {
    	
        super(parent, style);
        //composite = new Composite(parent, SWT.NONE);

        new Label(this, SWT.NONE).setText("Operation Window");
        
        
        new Label(this, SWT.NONE).setText("test0: " + test0);
        new Label(this, SWT.NONE).setText("test1: " + test1);
        try {
			plotSystem1 = PlottingFactory.createPlottingSystem();
		} catch (Exception e2) {
			logger.error("Can't make plotting system", e2);
		}
        
        
        
        this.createContents(aggDat, model, dm); 
//        System.out.println("Test line");
        
    }
     
    public void createContents(AggregateDataset aggDat, ExampleModel model, DataModel dm) {
        
        Group controlButtons = new Group(this, SWT.NULL);
        controlButtons.setText("Control Buttons");
        GridLayout gridLayoutButtons = new GridLayout();
        gridLayoutButtons.numColumns = 3;
        controlButtons.setLayout(gridLayoutButtons);
        GridData gridDataButtons = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridDataButtons.horizontalSpan = 1;
        controlButtons.setLayoutData(gridDataButtons);
        
        button = new Button (controlButtons, SWT.CHECK);
        button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        button1 = new Button (controlButtons, SWT.PUSH);
        button1.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        button2 = new Button (controlButtons, SWT.PUSH);
        button2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        
        
        ActionBarWrapper actionBarComposite = ActionBarWrapper.createActionBars(this, null);
        plotSystem1.createPlotPart(this, "ExamplePlot1", actionBarComposite, PlotType.IMAGE, null);
        
		button.setText ("Tri-state");
		/* Make the button toggle between three states */
		button.addListener (SWT.Selection, e -> {
			if (button.getSelection()) {
				if (!button.getGrayed()) {
					button.setGrayed(true);
				}
			} else {
				if (button.getGrayed()) {
					button.setGrayed(false);
					button.setSelection (true);
				}
			}
		});
		
		SliceND slice = new SliceND(aggDat.getShape());
		
		/* Read the tri-state button (application code) */
		button.addListener (SWT.Selection, e -> {
			if (button.getGrayed()) {
//				System.out.println("Grayed");
			} else {
				if (button.getSelection()) {
//				
					int selection = model.getImageNumber();
					slice.setSlice(0, selection, selection+1, 1);
					IDataset j = null;
					try {
						j = aggDat.getSlice(slice);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					j.squeeze();
					IDataset image1 = j;
					IDataset output = DummyProcessingClass.DummyProcess(j, model, dm);
					plotSystem1.createPlot2D(output, null, null);
				} else {
				}
			}
		});
        
		model.addPropertyChangeListener(new PropertyChangeListener() {
			
			@SuppressWarnings("unused")
			public void widgetSelected(SelectionEvent e) {
				
				int selection = model.getImageNumber();
				
			    try {
			    	if (button.getSelection()){
			    		slice.setSlice(0, selection, selection+1, 1);
			    		IDataset i = aggDat.getSlice(slice);
			    		i.squeeze();
			    		IDataset image1 = i;
						IDataset output = DummyProcessingClass.DummyProcess(i, model, dm);
						plotSystem1.createPlot2D(output, null, null);
			    	}
				
			    } 
			    catch (Exception f) {
					
					f.printStackTrace();
				}
			}
			

			@Override
			public void propertyChange(PropertyChangeEvent evt) {

				int selection = model.getImageNumber();
				
			    try {
			    	if (button.getSelection()){
			    		slice.setSlice(0, selection, selection+1, 1);
			    		IDataset i = aggDat.getSlice(slice);
			    		i.squeeze();
			    		IDataset image1 = i;
						IDataset output = DummyProcessingClass.DummyProcess(i, model, dm);
						plotSystem1.createPlot2D(output, null, null);
						plotSystem1.repaint();
			    	}
				
			    } 
			    catch (Exception f) {
					// TODO Auto-generated catch block
					f.printStackTrace();
				}
			}
			
		});
	       
        button1.setText("Run");
        
        button1.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
              SliceIterationRunner.sliceIterationRunner1(model, dm);
              
            }

            public void widgetDefaultSelected(SelectionEvent event) {
              
            }
        });
         
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        setLayout(gridLayout);
         
         
        button2.setText("Reset Tracker");
        button2.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent event) {
	            int selection = model.getSliderPos();
	            System.out.println("Slider position in reset:  " + selection);
	            slice.setSlice(0, selection, selection+1, 1);
				IDataset i = null;
				try {
					i = aggDat.getSlice(slice);
				} catch (DatasetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				i.squeeze();
             	model.setInput(null);
             	IROI region = model.getROI();
             	IRectangularROI currentBox = region.getBounds();
             	int[] currentLen = currentBox.getIntLengths();
             	int[] currentPt = currentBox.getIntPoint();
             	int[][] currentLenPt = {currentLen, currentPt};
             	double[] currentTrackerPos = new double[] {(double) currentPt[1],(double)currentPt[0], (double) (currentPt[1] +currentLen[1]),(double) (currentPt[0]),(double) currentPt[1],
					(double) currentPt[0]+currentLen[0], (double) (currentPt[1]+currentLen[1]),(double) (currentPt[0]+currentLen[0])};
             	
             	model.setTrackerCoordinates(new double[] {currentTrackerPos[1], currentTrackerPos[0]});
             	model.setLenPt(currentLenPt);
             	
             	IDataset j = DummyProcessingClass.DummyProcess(i, model, dm);
             	
             	plotSystem1.createPlot2D(j, null, null);
             	dm.resetAll();
             	
            }

            public void widgetDefaultSelected(SelectionEvent event) {
              
            }
        });

        final GridData gd_firstField = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.grabExcessVerticalSpace = true;

        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.heightHint = 100;
        gd_firstField.horizontalSpan = 2;

//        plotSystem1.createPlotPart(this, "ExamplePlot1", actionBarComposite, PlotType.IMAGE, null);
        
        plotSystem1.getPlotComposite().setLayoutData(gd_firstField);
        
        
       // plotSystem1.createPlot2D(image1, null, null);
   
		}
    
   public Composite getComposite(){   	
	   return this;
   }
   
   public IPlottingSystem<Composite> getPlotSystem(){
	   return plotSystem1;
   }

   public IDataset getImage(){
	   return image1;
   }
   
}

