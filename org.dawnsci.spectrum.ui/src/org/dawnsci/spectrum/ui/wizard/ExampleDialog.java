package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.region.IROIListener;
import org.eclipse.dawnsci.plotting.api.region.ROIEvent;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.Dataset;
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class ExampleDialog extends Dialog {
	
	final private String[] filepaths;
	private PlotSystemComposite customComposite;
	private PlotSystem2Composite customComposite2;
	private PlotSystem1Composite customComposite1;
	private PlotSystem3Composite customComposite3;
	private IDataset j;
	private OutputCurves outputCurves;
	private int imageNo;
	private OutputMovie outputMovie;
	private AggregateDataset aggDat;
	private ArrayList<ExampleModel> models;
	private ArrayList<GeometricParametersModel> gms;
	private ArrayList<DataModel> dms;
	private SuperModel sm;
	
	
	
	
	
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
	    ExampleModel model = new ExampleModel();
		DataModel dm = new DataModel();
		GeometricParametersModel gm = new GeometricParametersModel();
	    
		ArrayList<ILazyDataset> arrayILD = new ArrayList<ILazyDataset>();
		
		model.setFilepaths(filepaths);
		sm.setFilepaths(filepaths);
		
		for (String fpath : filepaths){
			try {
				IDataHolder dh1 =LoaderFactory.getData(fpath);
				ILazyDataset ild =dh1.getLazyDataset(gm.getImageName()); 
				//DatasetUtils.
				arrayILD.add(ild);
				models.add(new ExampleModel());
				gms.add(new GeometricParametersModel());
				dms.add(new DataModel());
				
				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		gm.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				for (String fpath : filepaths){
					try {
						IDataHolder dh1 =LoaderFactory.getData(fpath);
						ILazyDataset ild =dh1.getLazyDataset(gm.getImageName()); 
						//DatasetUtils.
						arrayILD.add(ild);
					} 
					
					catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		

		
		ILazyDataset[] shouldntneedthisarray = new ILazyDataset[arrayILD.size()];
		
		Iterator<ILazyDataset> itr =arrayILD.iterator();
		int i=0;

		while (itr.hasNext()){
			shouldntneedthisarray[i] = itr.next();
			i++;
		}
		
		aggDat = new AggregateDataset(false, shouldntneedthisarray);
		
		
		//model.setArrayILD(arrayILD);
		model.setAggDat(aggDat);

		
		
		ArrayList<ILazyDataset> arrayILDx = new ArrayList<ILazyDataset>();
		
		for (String fpath : filepaths){
			try {
				IDataHolder dh1 =LoaderFactory.getData(fpath);
				ILazyDataset ild =dh1.getLazyDataset(gm.getxName()); 
				//DatasetUtils.
				arrayILDx.add(ild);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		gm.addPropertyChangeListener(new PropertyChangeListener(){

			public void propertyChange(PropertyChangeEvent evt) {
				for (String fpath : filepaths){
					try {
						IDataHolder dh1 =LoaderFactory.getData(fpath);
				
						ILazyDataset ild =dh1.getLazyDataset(gm.getxName()); 
						//DatasetUtils.
						arrayILDx.add(ild);
					} 
					catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
			}
		});
		
		ILazyDataset[] shouldntneedthisarray1 = new ILazyDataset[arrayILDx.size()];
		
		Iterator<ILazyDataset> itr1 =arrayILDx.iterator();
		int j=0;

		while (itr1.hasNext()){
			shouldntneedthisarray1[j] = itr1.next();
			j++;
		}
		
		final AggregateDataset aggDatx = new AggregateDataset(false, shouldntneedthisarray1);
		
		
		//model.setArrayILD(arrayILD);
		model.setAggDatx(aggDatx);
		
///////////////////////////Window 1////////////////////////////////////////////////////
		try {
			
			DatDisplayer datDisplayer = new DatDisplayer(container, SWT.NONE, sm);
			datDisplayer.setLayout(new GridLayout());
			datDisplayer.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
///////////////////////////Window 2////////////////////////////////////////////////////
	    customComposite = new PlotSystemComposite(container, SWT.NONE, model);
	    customComposite.setLayout(new GridLayout());
	    customComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
///////////////////////////Window 3////////////////////////////////////////////////////	    
	    PlotSystem1Composite customComposite1 = new PlotSystem1Composite(container, 
	    		SWT.NONE, aggDat, model, dm, gm);
	    customComposite1.setLayout(new GridLayout());
	    customComposite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    
///////////////////////////Window 4////////////////////////////////////////////////////
	    try {
			outputCurves = new OutputCurves(container, SWT.NONE, dm);
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
		IDataset k = PlotSystem2DataSetter.PlotSystem2DataSetter1(model);
		customComposite2.setData(k);
		model.setCurrentImage(k);
	    customComposite2.setLayout(new GridLayout());
	    customComposite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
///////////////////////////Window 7////////////////////////////////////////////////////
	    
	    
		try {
			customComposite3 = new PlotSystem3Composite(container, SWT.NONE, 
					aggDat, model, dm);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	    customComposite3.setLayout(new GridLayout());
	    customComposite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    
///////////////////////////Window 8////////////////////////////////////////////////////
	    
	    try {
			outputMovie = new OutputMovie(container, SWT.NONE, dm);
			outputMovie.setLayout(new GridLayout());
			outputMovie.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
///////////////////////////////////////////////////////////////////////////////////	    
///////////////////////Update Methods/////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////	    
	    model.addPropertyChangeListener(new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				IDataset j = PlotSystem2DataSetter.PlotSystem2DataSetter1(model);
				customComposite2.setImage(j);
				customComposite3.updateAll(model, dm, j);
			}
	    	
	    });
	    
////////////////////////////////////////////////////////////////////////////////
	    customComposite1.getRunButton().addSelectionListener(new SelectionListener(){
	    	
	    	
			@Override
			public void widgetSelected(SelectionEvent e) {

				operationJob1 oJ = new operationJob1();
				oJ.setDm(dm);
				oJ.setModel(model);
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
				model.setOutputNo(imageNo);
				System.out.println("ImageNo: " + model.getOutputNo());
			}
			
		});
////////////////////////////////////////////////////////////////////////////////
	    
	    customComposite1.getButton2().addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				outputCurves.resetCurve();
				
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
				outputMovie.getPlotSystem().updatePlot2D(dm.getOutputDatArray().get(ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(), dm.getxList())), null,null);
				System.out.println("DatArray size:  " + dm.getOutputDatArray().size());
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
				
				imageNo = ClosestNoFinder.closestNoPos(outputCurves.getRegionNo().getROI().getPointX(), dm.getxList());
				
				if (outputMovie.getOutputControl().getSelection() == true){
				
					model.setOutputNo(imageNo);
					System.out.println("ImageNo: " + model.getOutputNo());
					
					operationJob2 oJ2 = new operationJob2();
					
					oJ2.setDm(dm);
					oJ2.setModel(model);
				
					oJ2.setImageNo(imageNo);
					
					model.setOutputNo(imageNo);
					System.out.println("ImageNo: " + model.getOutputNo());
					
					oJ2.setDm(dm);
					oJ2.setModel(model);
					oJ2.setOutputMovie(outputMovie);
					oJ2.setPlotSystem(outputMovie.getPlotSystem());
					
					oJ2.schedule();
				}
				if (customComposite.getOutputControl().getSelection() == true){
					
					operationJob3 oJ3 = new operationJob3();
					
					oJ3.setDm(dm);
					oJ3.setModel(model);
				
					oJ3.setImageNo(imageNo);
					oJ3.setPlotSystemComposite(customComposite);
					
					oJ3.schedule();
					
					
				}
				}
	    });
		
	    
//////////////////////////////////////////////////////////////////////////////////	    
	    
	    sm.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				int b = sm.getSelection();
				customComposite = new PlotSystemComposite(parent, SWT.NONE, models.get(b)); 
				
			}
		});
	    
	    
	    
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
			
				SliceND slice = new SliceND(aggDat.getShape());
				slice.setSlice(0, imageNo, imageNo+1, 1);
				
				
				try {
					
					System.out.println("model.getAggDat().getsize:  " + model.getAggDat().getSize());
					System.out.println("slice[0]:  " + slice.getShape()[0]);
					
					
					Dataset d = model.getAggDat().getSlice(slice);
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
		
			
			
//			if (plotSystemComposite.getOutputControl().getSelection() == true){
//
//				SliceND slice = new SliceND(aggDat.getShape());
//				slice.setSlice(0, imageNo, imageNo+1, 1);
//			
//			
//				
//				Display.getDefault().syncExec(new Runnable() {
//
//						@Override
//						public void run() {
//							try {
//								plotSystemComposite.getPlotSystem().
//								updatePlot2D(model.getAggDat().getSlice(slice), null, null);
//							} catch (DatasetException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						}
//					});
//			}
			
			return Status.OK_STATUS;

		}
	
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	
	class operationJob1 extends Job {

		private IDataset input;@Inject 
		UISynchronize sync;
		DataModel dm;
		ExampleModel model;
		IPlottingSystem<Composite> plotSystem;
		GeometricParametersModel gm;

		public operationJob1() {
			super("updating image...");
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
		
		public void setGeoModel(GeometricParametersModel gm) {
			this.gm = gm;
		}	
			
		public void setPlotSystem(IPlottingSystem<Composite> plotSystem) {
			this.plotSystem = plotSystem;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			

			int k =0;
			for ( k = model.getSliderPos(); k<model.getAggDat().getShape()[0]; k++){
				
				
				
				IDataset j = null;
				SliceND slice = new SliceND(model.getAggDat().getShape());
				slice.setSlice(0, k, k+1, 1);
				
				SliceND slicex = new SliceND(model.getAggDatx().getShape());
				slicex.setSlice(0, k, k+1, 1);
				
				
				
			
				
				
				
				
				try {
					//slice.setSlice(0, 0, 1, 1);
					dm.addxList((model.getAggDatx().getSlice(slicex)).getDouble(0));
				} catch (DatasetException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					j = model.getAggDat().getSlice(slice);
					} 
					catch (Exception e1) {
					}
					
				j.squeeze();
				
				final IDataset output1 = DummyProcessingClass.DummyProcess(j, model, dm, gm);

			plotSystem.clear();
			plotSystem.updatePlot2D(output1, null,monitor);
    		plotSystem.repaint(true);
    		System.out.println("~~~~~~~~~~~~~~In oj############");
    		try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			}
			
		return Status.OK_STATUS;

	   }
	}
}



