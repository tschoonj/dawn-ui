package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.dawb.common.ui.widgets.ActionBarWrapper;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.dawnsci.plotting.api.region.IRegion;
import org.eclipse.dawnsci.plotting.api.region.IRegion.RegionType;
import org.eclipse.dawnsci.plotting.api.trace.ILineTrace;
import org.eclipse.january.DatasetException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;

public class MultipleOutputCurves extends Composite {

	private IPlottingSystem<Composite> plotSystem4;
	private IRegion imageNo;
	private ILineTrace lt;
	private ExampleModel model;
	private DataModel dm;
	private SuperModel sm;
	private ArrayList<Button> datSelector;

	public MultipleOutputCurves(Composite parent, int style, ArrayList<ExampleModel> models, ArrayList<DataModel> dms,
			SuperModel sm) {
		super(parent, style);

		new Label(this, SWT.NONE).setText("Output Curves");

		this.model = models.get(sm.getSelection());
		this.dm = dms.get(sm.getSelection());
		try {
			plotSystem4 = PlottingFactory.createPlottingSystem();
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		this.createContents(model, sm);

	}

	public void createContents(ExampleModel model, SuperModel sm) {

		Group datSelection = new Group(this, SWT.NULL);
		GridLayout datSelectionLayout = new GridLayout(4, true);
		// methodSettingLayout.numColumns = 3;
		GridData datSelectionData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		datSelectionData.minimumWidth = 50;
		datSelection.setLayout(datSelectionLayout);
		datSelection.setLayoutData(datSelectionData);

		datSelector = new ArrayList<Button>();

		for (int i = 0; i < sm.getFilepaths().length; i++) {
			datSelector.add(new Button(datSelection, SWT.CHECK));
			datSelector.get(i).setText(StringUtils.substringAfterLast(sm.getFilepaths()[i], "/"));
		}

		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		setLayout(gridLayout);

		ActionBarWrapper actionBarComposite = ActionBarWrapper.createActionBars(this, null);
		;

		final GridData gd_secondField = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_secondField.grabExcessVerticalSpace = true;
		gd_secondField.grabExcessVerticalSpace = true;
		gd_secondField.heightHint = 100;

		plotSystem4.createPlotPart(this, "ExamplePlot", actionBarComposite, PlotType.IMAGE, null);

		lt = plotSystem4.createLineTrace("Output Curve");
		if (dm.getyList() == null || dm.getxList() == null) {
			lt.setData(dm.backupDataset(), dm.backupDataset());
		} else {
			lt.setData(dm.yIDataset(), dm.xIDataset());
		}

		plotSystem4.addTrace(lt);
		try {
			imageNo = plotSystem4.createRegion("Image", RegionType.XAXIS_LINE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// dm.addPropertyChangeListener(new PropertyChangeListener() {
		//
		// @Override
		// public void propertyChange(PropertyChangeEvent evt) {
		// // TODO Auto-generated method stub
		// ILineTrace lt = plotSystem4.createLineTrace("Output Curve");
		// if (dm.getyList() == null || dm.getxList() == null ){
		// lt.setData(dm.backupDataset(), dm.backupDataset());
		// }
		// else{
		// lt.setData(dm.xIDataset(), dm.yIDataset());
		// }
		//
		// Display.getDefault().syncExec(new Runnable() {
		//
		// @Override
		// public void run() {
		// plotSystem4.clear();
		// plotSystem4.addTrace(lt);
		// plotSystem4.repaint();
		//
		// }
		//
		// });
		// }
		// });

		plotSystem4.getPlotComposite().setLayoutData(gd_secondField);

	}

	public Composite getComposite() {

		return this;
	}

	public IPlottingSystem<Composite> getPlotSystem() {
		return plotSystem4;
	}

	public IRegion getRegionNo() {
		return imageNo;
	}

	public void resetCurve() {
		// try{
		// lt.setData(null, null);
		// } catch (Exception e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		Display.getDefault().syncExec(new Runnable() {

			@Override
			public void run() {
				plotSystem4.clear();
				lt = plotSystem4.createLineTrace("Output Curve");
				lt.setData(dm.backupDataset(), dm.backupDataset());
				plotSystem4.addTrace(lt);
				plotSystem4.repaint();
			}
		});

	}

	public void updateCurve(DataModel dm1) {

		if (lt.getDataName() == null) {
			lt = plotSystem4.createLineTrace("Output Curve");
		}

		if (dm1.getyList() == null || dm1.getxList() == null) {
			lt.setData(dm1.backupDataset(), dm1.backupDataset());
		} else {
			lt.setData(dm1.xIDataset(), dm1.yIDataset());
		}
		
		System.out.println("IN MultipleOutput updateCuve");
		System.out.println("dm1 xIDataset: " + dm1.xIDataset().getSize());
		System.out.println("dm1 yIDataset: " + dm1.yIDataset().getSize());
		plotSystem4.clear();
		plotSystem4.addTrace(lt);
		plotSystem4.repaint();
		

	}

	public ArrayList<Button> getDatSelector() {
		return datSelector;
	}

}
