package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import java.util.List;

import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.FitPower;
import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.Methodology;
import org.dawnsci.spectrum.ui.wizard.OutputMovie.MovieJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.region.IROIListener;
import org.eclipse.dawnsci.plotting.api.region.ROIEvent;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.dawnsci.plotting.api.trace.ILineTrace;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.events.EventException;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class ExampleDialog extends Dialog {
	
	final private String[] filepaths;
	private PlotSystemComposite customComposite;
	private PlotSystem2Composite customComposite2;
	private PlotSystem1Composite customComposite1;
	private PlotSystem3Composite customComposite3;
	private MultipleOutputCurves outputCurves;
	private int imageNo;
	private OutputMovie outputMovie;
	private AggregateDataset aggDat;
	private ArrayList<ExampleModel> models;
	private ArrayList<DataModel> dms;
	private ArrayList<GeometricParametersModel> gms;
	private SuperModel sm;
	private DatDisplayer datDisplayer;
	
	
	
	
	public ExampleDialog(Shell parentShell, String[] datFilenames) {
		super(parentShell);
		this.filepaths = datFilenames;

	}
	
	

	@Override
	  protected Control createDialogArea(Composite parent) {
	    final Composite container = (Composite) super.createDialogArea(parent);
	    GridLayout gridLayout = new GridLayout(4, true);
	    container.setLayout(gridLayout);			
	    sm = new SuperModel();
	
		DataModel dm = new DataModel();
		GeometricParametersModel gm = new GeometricParametersModel();
	    
		ArrayList<ILazyDataset> arrayILD = new ArrayList<ILazyDataset>();
		gms = new ArrayList<GeometricParametersModel>();
		dms = new ArrayList<DataModel>();
		models = new ArrayList<ExampleModel>();
		sm.setFilepaths(filepaths);
		
		for (int id = 0; id<filepaths.length; id++){ 
			try {
				IDataHolder dh1 =LoaderFactory.getData(filepaths[id]);
				ILazyDataset ild =dh1.getLazyDataset(gm.getImageName());
				models.add(new ExampleModel());
				dms.add(new DataModel());
				models.get(id).setDatImages(ild);
				models.get(id).setFilepath(filepaths[id]);
				//java.util.List<ILazyDataset> list1 = dh1.getList();
				gms.add(new GeometricParametersModel());
				ILazyDataset ildx =dh1.getLazyDataset(gm.getxName()); 
				models.get(id).setDatX(ildx);
				
			
			} 
			
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		gm.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				for (int id = 0; id<filepaths.length; id++){ 
					try {
						IDataHolder dh1 =LoaderFactory.getData(filepaths[id]);
						ILazyDataset ild =dh1.getLazyDataset(gm.getImageName()); 
						models.get(id).setDatImages(ild);
					} 
					
					catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		

		

///////////////////////////Window 1////////////////////////////////////////////////////
		try {
			
			datDisplayer = new DatDisplayer(container, SWT.NONE, sm);
			datDisplayer.setLayout(new GridLayout());
			datDisplayer.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
///////////////////////////Window 2////////////////////////////////////////////////////
		
	    customComposite = new PlotSystemComposite(container, SWT.NONE, models, sm,
	    		PlotSystemCompositeDataSetter.imageSetter(models.get(sm.getSelection()), 0));
					
	    customComposite.setLayout(new GridLayout());
	    customComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    

///////////////////////////Window 3////////////////////////////////////////////////////	    
	    customComposite1 = new PlotSystem1Composite(container, 
	    		SWT.NONE, models, dms, sm, gm);
	    customComposite1.setLayout(new GridLayout());
	    customComposite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    
///////////////////////////Window 4////////////////////////////////////////////////////
	    try {
			outputCurves = new MultipleOutputCurves(container, SWT.NONE, models, dms, sm);
			outputCurves.setLayout(new GridLayout());
			outputCurves.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
///////////////////////////Window 5////////////////////////////////////////////////////

	    try {
			SXRDGeometricParameterClass paramField = new SXRDGeometricParameterClass(container, SWT.NONE, gm);
			paramField.setLayout(new GridLayout());
			paramField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
///////////////////////////Window 6////////////////////////////////////////////////////
	    
	    
		try {
			customComposite2 = new PlotSystem2Composite(container, SWT.NONE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		IDataset k = PlotSystem2DataSetter.PlotSystem2DataSetter1(models.get(sm.getSelection()));
		customComposite2.setData(k);
		models.get(sm.getSelection()).setCurrentImage(k);
	    customComposite2.setLayout(new GridLayout());
	    customComposite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
///////////////////////////Window 7////////////////////////////////////////////////////
	    
	    
		try {
			customComposite3 = new PlotSystem3Composite(container, SWT.NONE, 
					aggDat,models.get(sm.getSelection()), dm);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    customComposite3.setLayout(new GridLayout());
	    customComposite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    
///////////////////////////Window 8////////////////////////////////////////////////////
	    
	    try {
			outputMovie = new OutputMovie(container, SWT.NONE, dms.get(sm.getSelection()));
			outputMovie.setLayout(new GridLayout());
			outputMovie.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
///////////////////////////////////////////////////////////////////////////////////	    
///////////////////////Update Methods/////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////
	    
	    
	    
	    
	    
	    
	    
//////////////////////////////////////////////////////////////////////////////////	    
	    for (ExampleModel m : models){
	    
	    	m.addPropertyChangeListener(new PropertyChangeListener(){

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					IDataset j = PlotSystem2DataSetter.PlotSystem2DataSetter1(m);
					customComposite2.setImage(j);
					int it = (int) m.getIterationMarker();
					customComposite3.updateAll(m, dms.get(it), j);
				}
		    	
		    });
	    }
////////////////////////////////////////////////////////////////////////////////
	    customComposite1.getRunButton().addSelectionListener(new SelectionListener(){
	    	
	    	
			@Override
			public void widgetSelected(SelectionEvent e) {

				operationJob1 oJ = new operationJob1();
				oJ.setOutputCurves(outputCurves);
				oJ.setDm(dms.get(sm.getSelection()));
				oJ.setModel(models.get(sm.getSelection()));
				oJ.setGeoModel(gm);;
				oJ.setPlotSystem(customComposite1.getPlotSystem());
				oJ.schedule();	
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
	   });
////////////////////////////////////////////////////////////////////////////////
	    outputCurves.getRegionNo().addROIListener(new IROIListener() {
			
			@Override
			public void roiSelected(ROIEvent evt) {
				roiStandard(evt);
				
			}
			
			@Override
			public void roiDragged(ROIEvent evt) {
				roiStandard(evt);
				
			}
			
			@Override
			public void roiChanged(ROIEvent evt) {
				roiStandard(evt);
			}
			
			public void roiStandard(ROIEvent evt){	
				imageNo = ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(), dm.getxList());
				models.get(sm.getSelection()).setOutputNo(imageNo);
				System.out.println("ImageNo: " + models.get(sm.getSelection()).getOutputNo());
			}
			
		});
////////////////////////////////////////////////////////////////////////////////
	    ////////THE RESET///////////////////
	    customComposite1.getButton2().addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
				outputCurves.resetCurve();
				
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

		        
	            int selection = models.get(sm.getSelection()).getSliderPos();
	            System.out.println("Slider position in reset:  " + selection);
	            SliceND slice = new SliceND(models.get(sm.getSelection()).getDatImages().getShape());
	            slice.setSlice(0, selection, selection+1, 1);
				IDataset i = null;
				try {
					i = models.get(sm.getSelection()).getDatImages().getSlice(slice);
				} catch (DatasetException e1) {
						// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				i.squeeze();
				models.get(sm.getSelection()).setInput(null);
            	IROI region = models.get(sm.getSelection()).getROI();
             	IRectangularROI currentBox = region.getBounds();
             	int[] currentLen = currentBox.getIntLengths();
             	int[] currentPt = currentBox.getIntPoint();
             	int[][] currentLenPt = {currentLen, currentPt};
		        double[] currentTrackerPos = new double[] {(double) currentPt[1],(double)currentPt[0], (double) (currentPt[1] +currentLen[1]),(double) (currentPt[0]),(double) currentPt[1],
				(double) currentPt[0]+currentLen[0], (double) (currentPt[1]+currentLen[1]),(double) (currentPt[0]+currentLen[0])};
		             	
		        int[] ab =customComposite1.getMethodology();
		        models.get(sm.getSelection()).setMethodology((Methodology.values()[ab[0]]));
		       	models.get(sm.getSelection()).setFitPower(FitPower.values()[ab[1]]);
		       	models.get(sm.getSelection()).setBoundaryBox(ab[2]);
						
		             	
             	models.get(sm.getSelection()).setTrackerCoordinates(new double[] {currentTrackerPos[1], currentTrackerPos[0]});
             	models.get(sm.getSelection()).setLenPt(currentLenPt);
		             	
             	IDataset j = DummyProcessingClass.DummyProcess(i, models.get(sm.getSelection()),dm, gm);
		             	
             	customComposite1.getPlotSystem().createPlot2D(j, null, null);
             	dms.get(sm.getSelection()).resetAll();
		        
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
////////////////////////////////////////////////////////////////////////////////
	    
	    outputMovie.getOutputControl().addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				outputMovie.getPlotSystem().updatePlot2D(dms.get(sm.getSelection()).getOutputDatArray().get(ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(),
						dms.get(sm.getSelection()).getxList())), null,null);
				System.out.println("DatArray size:  " + dms.get(sm.getSelection()).getOutputDatArray().size());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	    
//////////////////////////////////////////////////////////////////////////////////////

	    outputMovie.getPlayButton().addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				MovieJob movieJob = new MovieJob();
				movieJob.setData(dms.get(sm.getSelection()).getOutputDatArray());
				movieJob.setTime(Integer.parseInt(outputMovie.getTimeConstant().getText()));
				if(movieJob.getState() == Job.RUNNING) {
					movieJob.cancel();
				}
				movieJob.schedule();
					
			}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
	        });
	    
//////////////////////////////////////////////////////////////////////////////////////////////
	    outputCurves.getRegionNo().addROIListener(new IROIListener() {
			
			@Override
			public void roiSelected(ROIEvent evt) {
				// TODO Auto-generated method stub
				roiStandard1(evt);
			}
			
			@Override
			public void roiDragged(ROIEvent evt) {
				// TODO Auto-generated method stub
				roiStandard1(evt);
			}
			
			@Override
			public void roiChanged(ROIEvent evt) {
				roiStandard1(evt);
				
			}
			
			public void roiStandard1(ROIEvent evt){	
				
				imageNo = ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(), dms.get(sm.getSelection()).getxList());
				
				if (outputMovie.getOutputControl().getSelection() == true){
				
					models.get(sm.getSelection()).setOutputNo(imageNo);
					System.out.println("ImageNo: " + models.get(sm.getSelection()).getOutputNo());
					
					operationJob2 oJ2 = new operationJob2();
					
					oJ2.setDm(dms.get(sm.getSelection()));
					oJ2.setModel(models.get(sm.getSelection()));
				
					oJ2.setImageNo(imageNo);
					
					models.get(sm.getSelection()).setOutputNo(imageNo);
					System.out.println("ImageNo: " + models.get(sm.getSelection()).getOutputNo());
					
					oJ2.setDm(dms.get(sm.getSelection()));
					oJ2.setModel(models.get(sm.getSelection()));
					oJ2.setOutputMovie(outputMovie);
					oJ2.setPlotSystem(outputMovie.getPlotSystem());
					
					oJ2.schedule();
				}
				if (customComposite.getOutputControl().getSelection() == true){
					
					operationJob3 oJ3 = new operationJob3();
					
					oJ3.setDm(dms.get(sm.getSelection()));
					oJ3.setModel(models.get(sm.getSelection()));
				
					oJ3.setImageNo(imageNo);
					oJ3.setPlotSystemComposite(customComposite);
					
					oJ3.schedule();
					
					
				}
				}
	    });
		
	    
//////////////////////////////////////////////////////////////////////////////////
	    customComposite.getSlider().addSelectionListener(new SelectionListener() {
	    	
		public void widgetSelected(SelectionEvent e) {
			
			int selection = customComposite.getSlider().getSelection();
			models.get(sm.getSelection()).setSliderPos(selection);
			if (customComposite.getOutputControl().getSelection() == false){
				IDataset jk = PlotSystemCompositeDataSetter.imageSetter(models.get(sm.getSelection()), selection);
				customComposite.updateImage(jk);
			}	
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}			
			
	});
	    
	    
	    
///////////////////////////////////////////////////////////////////////////////////	    
	    
	    sm.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				System.out.println("I got fired off 2 ");
				customComposite.setModels(models.get(sm.getSelection()));
				int b = sm.getSelection();
				IDataset jl = PlotSystemCompositeDataSetter.imageSetter(models.get(sm.getSelection()), 0);
				customComposite.sliderReset(models.get(sm.getSelection()));
				customComposite.updateImage(jl);
				customComposite2.updateImage(PlotSystem2DataSetter.PlotSystem2DataSetter1(models.get(sm.getSelection())));
				
				
			}
		});
	    
///////////////////////////////////////////////////////////////////////////////////
	    
	    for( Button b: outputCurves.getDatSelector()){
	    	b.addSelectionListener(new SelectionListener() {
				
				@Override
				public void widgetSelected(SelectionEvent e) {
					outputCurves.getPlotSystem().clear();
					for(Button b :outputCurves.getDatSelector()){
						if (b.getSelection()){
							ILineTrace lte = outputCurves.getPlotSystem().createLineTrace(b.getText());
							lte = outputCurves.getPlotSystem().createLineTrace(b.getText());
							
							int p = (Arrays.asList(datDisplayer.getSelector().getItems())).indexOf(b.getText());
							
							if (dms.get(p).getyList() == null || dms.get(p).getxList() == null) {
								
								IDataset filler  = dms.get(0).backupDataset();
								lte.setData(filler, filler);
							} else {
								lte.setData(dms.get(p).xIDataset(),dms.get(p).yIDataset());
							}
							
							outputCurves.getPlotSystem().addTrace(lte);
						}
					}
				}
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
		}
	    
///////////////////////////////////////////////////////////////////////////////////
	    return container;
	}
	
	

	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("ExampleDialog");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}
	
	@Override
	  protected boolean isResizable() {
	    return true;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////
	class operationJob3 extends Job {
		
		private ExampleModel model;
		private IPlottingSystem<Composite> plotSystem;
		private PlotSystemComposite plotSystemComposite;
		private DataModel dm;
		private int imageNo;
		
		public operationJob3() {
			super("updating image...");
		}
		
		public void setPlotSystemComposite(PlotSystemComposite customComposite) {
			this.plotSystemComposite = customComposite;
		}
		
		public void setPlotSystem(IPlottingSystem<Composite> plotSystem) {
			this.plotSystem = plotSystem;
		}
		

		public void setDm(DataModel dm) {
			this.dm = dm;
		}
		
		public void setModel(ExampleModel model) {
			this.model = model;
		}
		
		public void setImageNo(int imageNo) {
			this.imageNo = imageNo;
		}
		
		
		
		@SuppressWarnings("unchecked")
		@Override
		protected IStatus run(IProgressMonitor monitor) {
	
			Display.getDefault().asyncExec(new Runnable() {
				
				@Override
				public void run() {
			
				SliceND slice = new SliceND(model.getDatImages().getShape());
				slice.setSlice(0, imageNo, imageNo+1, 1);
				
				
				try {
					
					
					System.out.println("slice[0]:  " + slice.getShape()[0]);
					
					
					Dataset d = (Dataset) model.getDatImages().getSlice(slice);
					d.squeeze();
					
					plotSystemComposite.getPlotSystem().
					updatePlot2D(d, null, null);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			});

			return Status.OK_STATUS;

		}
	
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	class operationJob2 extends Job {
	
		private ExampleModel model;
		private IPlottingSystem<Composite> plotSystem;
		private OutputMovie outputMovie;
		private DataModel dm;
		private PlotSystemComposite plotSystemComposite;
		private int imageNo;
	
		public void setPlotSystemComposite(PlotSystemComposite customComposite) {
			this.plotSystemComposite = customComposite;
		}
		
		public operationJob2() {
			super("updating image...");
		}
		
		public void setOutputMovie(OutputMovie outputMovie) {
			this.outputMovie = outputMovie;
		}
		
		public void setPlotSystem(IPlottingSystem<Composite> plotSystem) {
			this.plotSystem = plotSystem;
		}
		

		public void setDm(DataModel dm) {
			this.dm = dm;
		}
		
		public void setModel(ExampleModel model) {
			this.model = model;
		}
		
		
		public void setImageNo(int imageNo) {
			this.imageNo = imageNo;
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			Display.getDefault().asyncExec(new Runnable() {
				
			@Override
			public void run() {
	
				outputMovie.getPlotSystem().
				updatePlot2D(dm.getOutputDatArray().get(ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(), dm.getxList())), null, null);
			}
			});
			
			return Status.OK_STATUS;

		}
	
	}
	
////////////////////////////////////////////////////////////////////////////////////////////
	
	class operationJob1 extends Job {

		private IDataset input;
		@Inject 
		UISynchronize sync;
		DataModel dm;
		ExampleModel model;
		IPlottingSystem<Composite> plotSystem;
		MultipleOutputCurves outputCurves;
		GeometricParametersModel gm;
		SuperModel sm;

		public operationJob1() {
			super("updating image...");
		}

		public void setOutputCurves(MultipleOutputCurves outputCurves) {
			this.outputCurves = outputCurves;
		}
		
		
		public void setData(IDataset input) {
			this.input = input;
		}
		
		public void setDm(DataModel dm) {
			this.dm = dm;
		}
		
		public void setModel(ExampleModel model) {
			this.model = model;
		}
		
		public void setSuperModel(SuperModel sm) {
			this.sm= sm;
		}
		
		public void setGeoModel(GeometricParametersModel gm) {
			this.gm = gm;
		}	
			
		public void setPlotSystem(IPlottingSystem<Composite> plotSystem) {
			this.plotSystem = plotSystem;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			
			int[] ab =customComposite1.getMethodology();
			model.setMethodology((Methodology.values()[ab[0]]));
			model.setFitPower(FitPower.values()[ab[1]]);
			model.setBoundaryBox(ab[2]);
			
				outputCurves.resetCurve();
				int k =0;
				for ( k = model.getSliderPos(); k<model.getDatImages().getShape()[0]; k++){
					
					
					
					IDataset j = null;
					SliceND slice = new SliceND(model.getDatImages().getShape());
					slice.setSlice(0, k, k+1, 1);
					
					SliceND slicex = new SliceND(model.getDatX().getShape());
					slicex.setSlice(0, k, k+1, 1);
					
					
					try {
						//slice.setSlice(0, 0, 1, 1);
						dm.addxList((model.getDatX().getSlice(slicex)).getDouble(0));
					} catch (DatasetException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					try {
						j = model.getDatImages().getSlice(slice);
						} 
						catch (Exception e1) {
						}
						
					j.squeeze();
					
					
				
				
					
					
					customComposite.getBoxPosition();
					
					IDataset output1 = DummyProcessingClass.DummyProcess(j, model,dm, gm);
						
					

					Display.getDefault().syncExec(new Runnable() {
						
						@Override
							public void run() {
						plotSystem.clear();
						plotSystem.updatePlot2D(output1, null,monitor);
			    		plotSystem.repaint(true);
			    		outputCurves.updateCurve();
			    		System.out.println("~~~~~~~~~~~~~~In oj############");	
		    		
					}
				});
				}
				
			
	    		try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			
			
		return Status.OK_STATUS;

	   }
	}
	
	
/////////////////////////////Movie Job///////////////////////////////
	
////////////////////////////////////////////////////////////////
	   
	   
	class MovieJob extends Job {
	
	private List<IDataset> outputDatArray;
	private int time;
	
	public MovieJob() {
		super("Playing movie...");
	}
	
	public void setData(List outputDatArray) {
	this.outputDatArray = outputDatArray;
	}
	
	public void setTime(int time) {
	this.time = time;
	}
	
	
	
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		for( IDataset t: outputDatArray){
			java.util.Date date = new java.util.Date();
			System.out.println(date);
			System.out.println("start:" + date);
			System.out.println("sum: " + (Double) DatasetUtils.cast(DoubleDataset.class, t).sum());
			//outputMovie.clear();
			outputMovie.getPlotSystem().updatePlot2D(t, null, monitor);
			outputMovie.getPlotSystem().repaint(true);
			//outputMovie.repaint();
			try {
				TimeUnit.MILLISECONDS.sleep(time);
			} catch (InterruptedException e) {
			
				e.printStackTrace();
			}
			date = new java.util.Date();
			System.out.println("stop:" + date);
			}
	return Status.OK_STATUS;
	}
	}
}



