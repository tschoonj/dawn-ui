package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.dawb.common.ui.widgets.ActionBarWrapper;
import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.FitPower;
import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.Methodology;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlotSystem1Composite extends Composite {

	private final static Logger logger = LoggerFactory.getLogger(PlotSystem1Composite.class);

    private IPlottingSystem<Composite> plotSystem1;
    private IDataset image1;
    private Button button; 
    private Button button1;
    private Button button2;
    private Combo comboDropDown0;
	private Combo comboDropDown1;
    private Text boundaryBoxText;
    private ArrayList<ExampleModel> models;
    private SuperModel sm;
    private GeometricParametersModel gm;
    private ArrayList<DataModel> dms;
    private DataModel dm;
	
    private ExampleModel model;
    
    public PlotSystem1Composite(Composite parent, int style
    		,ArrayList<ExampleModel> models, ArrayList<DataModel> dms,SuperModel sm, GeometricParametersModel gm) {
    	
        super(parent, style);
        //composite = new Composite(parent, SWT.NONE);

        new Label(this, SWT.NONE).setText("Operation Window");
        
        this.gm = gm;
        this.models = models;
        this.sm =sm;
        this.model = models.get(sm.getSelection());
        this.dm =dms.get(sm.getSelection());
        
        
        try {
        	
			plotSystem1 = PlottingFactory.createPlottingSystem();
		} catch (Exception e2) {
			logger.error("Can't make plotting system", e2);
		}
        
        
        
        this.createContents(model, gm); 
//        System.out.println("Test line");
        
    }
     
    public void createContents(ExampleModel model
    		, GeometricParametersModel gm) {
        
        
        Group methodSetting = new Group(this, SWT.NULL);
        GridLayout methodSettingLayout = new GridLayout(3, true);
//		methodSettingLayout.numColumns = 3;
	    GridData methodSettingData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	    methodSettingData .minimumWidth = 50;
	    methodSetting.setLayout(methodSettingLayout);
	    methodSetting.setLayoutData(methodSettingData);
	    
	    
	    new Label(methodSetting, SWT.LEFT).setText("Methodology");
	    new Label(methodSetting, SWT.LEFT).setText("Fit Power");
	    new Label(methodSetting, SWT.LEFT).setText("Boundary Box");
	    //Combo comboSimple = new Combo(this, SWT.SIMPLE | SWT.BORDER);
	    
	    comboDropDown0 = new Combo(methodSetting, SWT.DROP_DOWN | SWT.BORDER | SWT.LEFT);
	   	comboDropDown1 = new Combo(methodSetting, SWT.DROP_DOWN | SWT.BORDER | SWT.RIGHT);
	    boundaryBoxText = new Text(methodSetting, SWT.SINGLE);
	    
	    
	    
	    for(Methodology  t: AnalaysisMethodologies.Methodology.values()){
	    	comboDropDown0.add(AnalaysisMethodologies.toString(t));
	    }
	    
	    for(FitPower  i: AnalaysisMethodologies.FitPower.values()){
	    	comboDropDown1.add(String.valueOf(AnalaysisMethodologies.toInt(i)));
	    }
	    
	    comboDropDown0.addSelectionListener(new SelectionListener() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	          int selection = comboDropDown0.getSelectionIndex();
	          model.setMethodology(Methodology.values()[selection]);
	        }
	    	@Override
	        public void widgetDefaultSelected(SelectionEvent e) {
	          
	        }

	      });
	    
	    comboDropDown1.addSelectionListener(new SelectionListener() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	          int selection1 = comboDropDown1.getSelectionIndex();
	          model.setFitPower(FitPower.values()[selection1]);
	        }
	    	@Override
	        public void widgetDefaultSelected(SelectionEvent e) {
	          
	        }

	      });
	    
	    boundaryBoxText.addModifyListener(new ModifyListener(){

			@Override
			public void modifyText(ModifyEvent e) {
				model.setBoundaryBox(Integer.parseInt(boundaryBoxText.getText()));
			}
	    	
	    });
	    
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
		
		SliceND slice = new SliceND(model.getDatImages().getShape());
		
		/* Read the tri-state button (application code) */
		button.addListener (SWT.Selection, e -> {
			if (button.getGrayed()) {
//				System.out.println("Grayed");
			} else {
//				if (button.getSelection()) {
//				
					int selection = model.getImageNumber();
					slice.setSlice(0, selection, selection+1, 1);
					IDataset j = null;
					try {
						j = model.getDatImages().getSlice(slice);
					} catch (Exception e1) {
						
						e1.printStackTrace();
					}
					j.squeeze();
					IDataset image1 = j;
					IDataset output = DummyProcessingClass.DummyProcess(j, model,dm, gm);
					plotSystem1.createPlot2D(output, null, null);
//				} else {
//				}
			}
		});
        
		model.addPropertyChangeListener(new PropertyChangeListener() {
			
			@SuppressWarnings("unused")
			public void widgetSelected(SelectionEvent e) {
				
				int selection = model.getImageNumber();
				
			    try {
			    	if (button.getSelection()){
			    		slice.setSlice(0, selection, selection+1, 1);
			    		IDataset i = model.getDatImages().getSlice(slice);
			    		i.squeeze();
			    		IDataset image1 = i;
						IDataset output = DummyProcessingClass.DummyProcess(i, model, dm,gm);
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
			    		IDataset i = model.getDatImages().getSlice(slice);
			    		i.squeeze();
			    		IDataset image1 = i;
						IDataset output = DummyProcessingClass.DummyProcess(i, model, dm, gm);
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
        
        button2.setText("Reset Tracker");
//        button2.addSelectionListener(new SelectionListener() {
//
//            public void widgetSelected(SelectionEvent event) {
//	            int selection = model.getSliderPos();
//	            System.out.println("Slider position in reset:  " + selection);
//	            SliceND slice = new SliceND(model.getDatImages().getShape());
//	            slice.setSlice(0, selection, selection+1, 1);
//				IDataset i = null;
//				try {
//					i = model.getDatImages().getSlice(slice);
//				} catch (DatasetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				i.squeeze();
//             	model.setInput(null);
//             	IROI region = model.getROI();
//             	IRectangularROI currentBox = region.getBounds();
//             	int[] currentLen = currentBox.getIntLengths();
//             	int[] currentPt = currentBox.getIntPoint();
//             	int[][] currentLenPt = {currentLen, currentPt};
//             	double[] currentTrackerPos = new double[] {(double) currentPt[1],(double)currentPt[0], (double) (currentPt[1] +currentLen[1]),(double) (currentPt[0]),(double) currentPt[1],
//					(double) currentPt[0]+currentLen[0], (double) (currentPt[1]+currentLen[1]),(double) (currentPt[0]+currentLen[0])};
//             	
//             	int[] ab =getMethodology();
//				model.setMethodology((Methodology.values()[ab[0]]));
//				model.setFitPower(FitPower.values()[ab[1]]);
//				model.setBoundaryBox(ab[2]);
//				
//             	
//             	model.setTrackerCoordinates(new double[] {currentTrackerPos[1], currentTrackerPos[0]});
//             	model.setLenPt(currentLenPt);
//             	
//             	IDataset j = DummyProcessingClass.DummyProcess(i, model,dm, gm);
//             	
//             	plotSystem1.createPlot2D(j, null, null);
//             	dm.resetAll();
//             	
//            }
//
//            public void widgetDefaultSelected(SelectionEvent event) {
//              
//            }
//        });

        final GridData gd_firstField = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.grabExcessVerticalSpace = true;

        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.grabExcessVerticalSpace = true;
        gd_firstField.heightHint = 100;
        gd_firstField.horizontalSpan = 2;

        plotSystem1.getPlotComposite().setLayoutData(gd_firstField);
        
        
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
   
   public int[] getMethodology(){
	   
	   int[] returns = new int[3]; 
	   
	   Display.getDefault().syncExec(new Runnable() {
		      public void run() {
		    	  returns[0] = comboDropDown0.getSelectionIndex();
				   returns[1] = comboDropDown1.getSelectionIndex();
				   returns[2] = Integer.parseInt(boundaryBoxText.getText());
		      }
		    });
		   
	   
	   return returns;
   }
   
   public Button getRunButton(){
	   return button1;
   }
   
	public Button getButton2() {
		return button2;
	}

   
class operationJob extends Job {

	private IDataset input;
	

	public operationJob() {
		super("updating image...");
	}

	public void setData(IDataset input) {
		this.input = input;
	}


	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
			plotSystem1.clear();
			plotSystem1.updatePlot2D(input, null, monitor);
    		plotSystem1.repaint(true);
			}
    	
		});	
	
		return Status.OK_STATUS;
	}
   }
}
