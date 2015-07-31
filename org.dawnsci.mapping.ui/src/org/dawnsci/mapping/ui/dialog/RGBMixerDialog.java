package org.dawnsci.mapping.ui.dialog;

import java.util.List;

import org.eclipse.dawnsci.analysis.api.dataset.IDataset;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlotType;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class RGBMixerDialog extends Dialog {

	private List<IDataset> data;
	private IPlottingSystem system;

	public RGBMixerDialog(Shell parentShell, List<IDataset> data) {
		super(parentShell);
		this.data = data;
		try {
			system = PlottingFactory.createPlottingSystem();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Control createContents(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, true));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite leftPane = new Composite(container, SWT.NONE);
		leftPane.setLayout(new GridLayout(2, false));
		leftPane.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//generate combos
		String[] dataNames = new String[data.size() + 1];
		dataNames[0] = "None";
		for (int i = 0; i < data.size(); i ++) {
			dataNames[i + 1] = data.get(i).getName();
		}
		Label redLabel = new Label(leftPane, SWT.RIGHT);
		redLabel.setText("Red:");
		final Combo redCombo = new Combo(leftPane, SWT.NONE);
		redCombo.setItems(dataNames);
		redCombo.select(0);
		redCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				int idx = redCombo.getSelectionIndex() - 1 ;
				if (idx < 0) {
					system.clear();
				} else {
					system.updatePlot2D(data.get(idx), null, null);
				}
			}
		});

		Label greenLabel = new Label(leftPane, SWT.RIGHT);
		greenLabel.setText("Green:");
		Combo greenCombo = new Combo(leftPane, SWT.NONE);
		greenCombo.setItems(dataNames);
		greenCombo.select(0);
		greenCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				
			}
		});

		Label blueLabel = new Label(leftPane, SWT.RIGHT);
		blueLabel.setText("Blue:");
		Combo blueCombo = new Combo(leftPane, SWT.NONE);
		blueCombo.setItems(dataNames);
		blueCombo.select(0);
		blueCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				
			}
		});

		Composite plotContainer = new Composite(container, SWT.NONE);
		plotContainer.setLayout(new GridLayout(1, false));
		plotContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		system.createPlotPart(plotContainer, "RGB Plot", null, PlotType.IMAGE, null);
		system.getPlotComposite().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Button closeButton = new Button(container, SWT.NONE);
		closeButton.setText("Close");
		closeButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false, 2, 1));
		closeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent evt) {
				RGBMixerDialog.this.close();
			}
		});

		return container;
	}

	// overriding this methods allows you to set the
	// title of the custom dialog
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("RGB Mixer");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}
}
