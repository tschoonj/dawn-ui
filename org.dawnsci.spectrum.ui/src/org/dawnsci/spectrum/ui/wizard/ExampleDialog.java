package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.inject.Inject;

import org.dawnsci.spectrum.ui.wizard.SliceIterationRunner.operationJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
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
import org.eclipse.swt.widgets.Slider;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class ExampleDialog extends Dialog {
	
	final private String[] filepaths;
	private PlotSystem2Composite customComposite2;
	private PlotSystem1Composite customComposite1;
	private IDataset j;
	
	public ExampleDialog(Shell parentShell, String[] datFilenames) {
		super(parentShell);
		this.filepaths = datFilenames;

	}
	
	

	@Override
	  protected Control createDialogArea(Composite parent) {
	    final Composite container = (Composite) super.createDialogArea(parent);
	    GridLayout gridLayout = new GridLayout(4, true);
	    container.setLayout(gridLayout);			

	    ExampleModel model = new ExampleModel();
		DataModel dm = new DataModel();
		GeometricParametersModel gm = new GeometricParametersModel();
	    
		ArrayList<ILazyDataset> arrayILD = new ArrayList<ILazyDataset>();
		
		model.setFilepaths(filepaths);
		
		for (String fpath : filepaths){
			try {
				IDataHolder dh1 =LoaderFactory.getData(fpath);
				String[] names = dh1.getNames();
				ILazyDataset ild =dh1.getLazyDataset(gm.getImageName()); 
				//DatasetUtils.
				arrayILD.add(ild);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
//		gm.addPropertyChangeListener(new PropertyChangeListener(){
//
//			public void propertyChange(PropertyChangeEvent evt) {
//				for (String fpath : filepaths){
//					try {
//						IDataHolder dh1 =LoaderFactory.getData(fpath);
//						String[] names = dh1.getNames();
//						ILazyDataset ild =dh1.getLazyDataset(gm.getImageName()); 
//						//DatasetUtils.
//						arrayILD.add(ild);
//					} 
//					catch (Exception e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//		});
		
		ILazyDataset[] shouldntneedthisarray = new ILazyDataset[arrayILD.size()];
		
		Iterator<ILazyDataset> itr =arrayILD.iterator();
		int i=0;

		while (itr.hasNext()){
			shouldntneedthisarray[i] = itr.next();
			i++;
		}
		
		final AggregateDataset aggDat = new AggregateDataset(false, shouldntneedthisarray);
		
		
		//model.setArrayILD(arrayILD);
		model.setAggDat(aggDat);
		
		
		
		String title = filepaths[0];
		
		
		ArrayList<ILazyDataset> arrayILDx = new ArrayList<ILazyDataset>();
		
		for (String fpath : filepaths){
			try {
				IDataHolder dh1 =LoaderFactory.getData(fpath);
				String[] names = dh1.getNames();
				ILazyDataset ild =dh1.getLazyDataset("/entry/result/l"); 
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
						String[] names = dh1.getNames();
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
		
		
///////////////////////////Window 1////////////////////////////////////////////////////
		try {
			
			FileViewer2 fileview = new FileViewer2(container);
			fileview.setLayout(new GridLayout());
			fileview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			
		    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
///////////////////////////Window 2////////////////////////////////////////////////////
	    PlotSystemComposite customComposite = new PlotSystemComposite(container, SWT.NONE, aggDat, title, model);
	    customComposite.setLayout(new GridLayout());
	    customComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
///////////////////////////Window 3////////////////////////////////////////////////////	    
	    PlotSystem1Composite customComposite1 = new PlotSystem1Composite(container, 
	    		SWT.NONE, aggDat, model, dm, gm);
	    customComposite1.setLayout(new GridLayout());
	    customComposite1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    
///////////////////////////Window 4////////////////////////////////////////////////////
	    try {
			OutputCurves outputCurves = new OutputCurves(container, SWT.NONE, dm);
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
	    
	    
	    
	    
	    
	    //	    try {
//			PaddingClass padField1 = new PaddingClass(container, SWT.NONE);
//			padField1.setLayout(new GridLayout());
//			padField1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	    
	    
///////////////////////////Window 6////////////////////////////////////////////////////
	    
	    
		try {
			customComposite2 = new PlotSystem2Composite(container, SWT.NONE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		customComposite2.setData(PlotSystem2DataSetter.PlotSystem2DataSetter1(model));
		
	    customComposite2.setLayout(new GridLayout());
	    customComposite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    PlotSystem3Composite customComposite3 =null;;
		try {
			customComposite3 = new PlotSystem3Composite(container, SWT.NONE, 
					aggDat, model, dm);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
///////////////////////////Window 7////////////////////////////////////////////////////
	    customComposite3.setLayout(new GridLayout());
	    customComposite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	    
	    
		
		
///////////////////////////Window 8////////////////////////////////////////////////////
	    
	    try {
			OutputMovie outputMovie = new OutputMovie(container, SWT.NONE, dm);
			outputMovie.setLayout(new GridLayout());
			outputMovie.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    model.addPropertyChangeListener(new PropertyChangeListener(){

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				customComposite2.setImage(PlotSystem2DataSetter.PlotSystem2DataSetter1(model));
				
			}
	    	
	    });
	    
	    
	    customComposite1.getRunButton().addSelectionListener(new SelectionListener(){
	    	
	    	
			@Override
			public void widgetSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				for (int k = model.getSliderPos(); k<model.getAggDat().getShape()[0]; k++){
//    				dm.addxList((double) k);
//    				
//    				SliceND slice = new SliceND(model.getAggDat().getShape());
//    				slice.setSlice(0, k, k+1, 1);
//    				try {
//    					j = model.getAggDat().getSlice(slice);
//    					} 
//    					catch (Exception e1) {
//    					}
//    					
//    				j.squeeze();
//    				
//    				IDataset output1 = DummyProcessingClass.DummyProcess(j, model, dm, gm);
//    				
//
//					Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//					// Update UI here
//						customComposite1.getPlotSystem().clear();
////		    			plotSystem.createPlot2D(output1, null, null);
//						customComposite1.getPlotSystem().updatePlot2D(output1, null,null);
//						System.out.println("j:  " + j.getName());
//						
//						customComposite1.getPlotSystem().repaint(true);
//						try {
//		    				Thread.sleep(1000);
//		    			} catch (InterruptedException e1) {
//		    				// TODO Auto-generated catch block
//		    				e1.printStackTrace();
//		        		System.out.println("~~~~~~~~~~~~~~Trying this now.############");
//		            	}
//					}
//					});
////    			operationJob oJ = new operationJob();
////    			oJ.setPlot(customComposite1.getPlotSystem());
////				oJ.setData(output1);	
////				oJ.schedule();	
//        		
//			}
//			
				operationJob oJ = new operationJob();
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
	
	class operationJob extends Job {

		private IDataset input;@Inject 
		UISynchronize sync;
		DataModel dm;
		ExampleModel model;
		IPlottingSystem<Composite> plotSystem;
		GeometricParametersModel gm;

		public operationJob() {
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
			
			for (int k = model.getSliderPos(); k<model.getAggDat().getShape()[0]; k++){
				dm.addxList((double) k);
				IDataset j = null;
				SliceND slice = new SliceND(model.getAggDat().getShape());
				slice.setSlice(0, k, k+1, 1);
				try {
					j = model.getAggDat().getSlice(slice);
					} 
					catch (Exception e1) {
					}
					
				j.squeeze();
				
				final IDataset output1 = DummyProcessingClass.DummyProcess(j, model, dm, gm);

			plotSystem.clear();
//			plotSystem.createPlot2D(output1, null, null);
			plotSystem.updatePlot2D(output1, null,monitor);
    		plotSystem.repaint(true);
    		System.out.println("~~~~~~~~~~~~~~In oj############");
    		try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			}
//			Display.getDefault().syncExec(new Runnable() {
//				public void run() {				
//				
//					
//				
//			}
//			});
			}
			
		return Status.OK_STATUS;

	   }
	}
}


