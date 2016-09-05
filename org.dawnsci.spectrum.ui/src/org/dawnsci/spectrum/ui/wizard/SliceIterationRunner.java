package org.dawnsci.spectrum.ui.wizard;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.january.DatasetException;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.january.dataset.SliceND;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class SliceIterationRunner {
	
	private IPlottingSystem<Composite> plotSystem;
	private IDataset output1;
	
	
	public IDataset sliceIterationRunner1 (ExampleModel model, 
			DataModel dm, IPlottingSystem<Composite> plotSystem){
		
		this.plotSystem = plotSystem;
		plotSystem.clear();
		SliceND slice = new SliceND(model.getAggDat().getShape());
		operationJob oJ = new operationJob();
//		operationJob oJ = new operationJob();
//		oJ.wakeUp();
		oJ.setDm(dm);
		oJ.setModel(model);
		oJ.setPlotSystem(plotSystem);
		oJ.schedule();
		
		
//		for (k = model.getSliderPos(); k<model.getAggDat().getShape()[0]; k++){
//			dm.addxList((double) k);
//			IDataset j = null;
//			slice.setSlice(0, k, k+1, 1);
//			try {
//				j = model.getAggDat().getSlice(slice);
//				} 
//				catch (Exception e1) {
//				}
//				
//			j.squeeze();
//			
//			final IDataset output = DummyProcessingClass.DummyProcess(j, model, dm);
//			this.output1= output;
//			
////			oJ.setData(output);
////			
////			if(oJ.getState() == Job.RUNNING) {
////				oJ.cancel();
////			}
////			oJ.setPriority(Job.INTERACTIVE);
////			
////			oJ.schedule();
//			Display.getDefault().syncExec(new Runnable() {
//
//				public void run() {
//				plotSystem.clear();
//				plotSystem.updatePlot2D(output, null,null);
//	    		plotSystem.repaint(true);
//	    		System.out.println("~~~~~~~~~~~~~~In oj############");
//	    		try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				}
//				
//			});
//			try {
//				oJ.join(10000, null);
//			} catch (OperationCanceledException | InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
//			try {
//				TimeUnit.MILLISECONDS.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			UISynchronize sync = new UISynchronize() {
//				
//				@Override
//				public void syncExec(Runnable runnable) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void asyncExec(Runnable runnable) {
//					// TODO Auto-generated method stub
//					plotSystem.clear();
//					plotSystem.updatePlot2D(output1, null, null);
//					plotSystem.repaint();
//				}
//			};
			
			
				  
				
//			Job job = new Job("My Job") {
//				  @Override
//				  protected IStatus run(IProgressMonitor monitor) {
//				    // do something long running
//				    //... 
//				            
//				    // If you want to update the UI
//				    
//				      @Override
//				      public void run() {
//				    	  Display.getDefault().asyncExec(new Runnable() {
//							  public void run() {
//								  	plotSystem.clear();
//									plotSystem.updatePlot2D(output1, null, null);
//									plotSystem.repaint();
//							  }
//							});
//						Display.getDefault().syncExec(new Runnable() {
//							  public void run() {
//								  	plotSystem.clear();
//									plotSystem.updatePlot2D(output1, null, null);
//									plotSystem.repaint();
//				        // do something in the user interface
//				        // e.g. set a text field
//				      }
//				    });
//				 }
//				    return Status.OK_STATUS;
//				  }
//				};
//			plotSystem.updatePlot2D(output, null, null);
//    		plotSystem.repaint(true);
		
		
		
		
	
		return null;
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
//    		try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
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
	
	
	

