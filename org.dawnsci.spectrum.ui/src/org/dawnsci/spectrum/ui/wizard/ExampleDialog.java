package org.dawnsci.spectrum.ui.wizard;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.dawnsci.analysis.api.io.IDataHolder;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.ILazyDataset;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class ExampleDialog extends Dialog {
	
	final private String[] filepaths;
	
	public ExampleDialog(Shell parentShell, String[] datFilenames) {
		super(parentShell);
		this.filepaths = datFilenames;

	}

	@Override
	  protected Control createDialogArea(Composite parent) {
	    final Composite container = (Composite) super.createDialogArea(parent);
	    GridLayout gridLayout = new GridLayout(4, false);
	    container.setLayout(gridLayout);			

		ArrayList<ILazyDataset> arrayILD = new ArrayList<ILazyDataset>();
		
		for (String fpath : filepaths){
			try {
				IDataHolder dh1 =LoaderFactory.getData(fpath);
				String[] names = dh1.getNames();
				ILazyDataset ild =dh1.getLazyDataset("/entry/result/data"); 
				//DatasetUtils.
				arrayILD.add(ild);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		ILazyDataset[] shouldntneedthisarray = new ILazyDataset[arrayILD.size()];
		
		
		
		Iterator<ILazyDataset> itr =arrayILD.iterator();
		int i=0;
		while (itr.hasNext()){
			shouldntneedthisarray[i] = itr.next();
			i++;
		}
		
		final AggregateDataset aggDat = new AggregateDataset(false, shouldntneedthisarray);
		
		ExampleModel model = new ExampleModel();
		DataModel dm = new DataModel();
		
		//model.setArrayILD(arrayILD);
		model.setAggDat(aggDat);
		
		
		String title = filepaths[0];
		
		String test0 = null;
		String test1 = null;
///////////////////////////Window 1////////////////////////////////////////////////////
		try {
			DropDownMenuTestComposite dropDown = new DropDownMenuTestComposite(container, SWT.NONE, model);
			dropDown.setLayout(new GridLayout(2,true));
			dropDown.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			test0 = dropDown.getFitDirection();
			test1 = dropDown.getFitPower();
		    
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
	    		SWT.NONE, aggDat, test0, test1, model, dm);
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
			PaddingClass padField1 = new PaddingClass(container, SWT.NONE);
			padField1.setLayout(new GridLayout());
			padField1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    
///////////////////////////Window 6////////////////////////////////////////////////////
	    try {
			PlotSystem2Composite customComposite2 = new PlotSystem2Composite(container, SWT.NONE, 
					aggDat, model);
		    customComposite2.setLayout(new GridLayout());
		    customComposite2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    PlotSystem3Composite customComposite3 = new PlotSystem3Composite(container, SWT.NONE, 
		    		aggDat, model, dm);
///////////////////////////Window 7////////////////////////////////////////////////////
		    customComposite3.setLayout(new GridLayout());
		    customComposite3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		    
	    } catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
///////////////////////////Window 8////////////////////////////////////////////////////
	    
	    try {
			OutputMovie outputMovie = new OutputMovie(container, SWT.NONE, dm);
			outputMovie.setLayout(new GridLayout());
			outputMovie.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}
