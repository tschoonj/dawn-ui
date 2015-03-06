package org.dawnsci.plotting.histogram.ui;

import org.dawnsci.common.widgets.spinner.FloatSpinner;
import org.dawnsci.plotting.histogram.ImageHistogramProvider;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dawnsci.plotting.api.tool.AbstractToolPage;
import org.eclipse.dawnsci.plotting.api.tool.IToolPage;
import org.eclipse.dawnsci.plotting.api.trace.IPaletteTrace;
import org.eclipse.dawnsci.plotting.api.trace.ITraceListener;
import org.eclipse.dawnsci.plotting.api.trace.TraceEvent;
import org.eclipse.dawnsci.plotting.api.trace.TraceWillPlotEvent;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.IPageSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistogramToolPage2 extends AbstractToolPage implements IToolPage {

	private static final Logger logger = LoggerFactory.getLogger(HistogramToolPage2.class);

	private FormToolkit toolkit;
	private ScrolledForm form;
	private CCombo colourMapCombo;

	private HistogramViewer histogramWidget;

	private ITraceListener traceListener = new TraceListener();

	@Override
	public ToolPageRole getToolPageRole() {
		return ToolPageRole.ROLE_2D;
	}

	@Override
	public void createControl(Composite parent) {
		logger.debug("HistogramToolPage: createControl");
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.reflow(true); // create view with no scrollbars reflowing at this point
		form.getBody().setLayout(GridLayoutFactory.fillDefaults().create());

		createImageSettings(form.getBody());

		createHistogramControl(form.getBody());
	}

	private void createMinMaxSettings(Composite comp){
		Composite composite = new Composite(comp, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(4).create());

		Label minLabel = new Label(composite, SWT.NONE);
		minLabel.setText("Min:");
		minLabel.setLayoutData(new GridData (SWT.BEGINNING, SWT.CENTER, false, false));
		FloatSpinner minText = new FloatSpinner(composite, SWT.BORDER);
		minText.setLayoutData(new GridData (SWT.BEGINNING, SWT.CENTER, false, false));
		Label maxLabel = new Label(composite, SWT.NONE);
		maxLabel.setLayoutData(new GridData (SWT.END, SWT.CENTER, true, false));
		maxLabel.setText("Max:");
		FloatSpinner maxText = new FloatSpinner(composite, SWT.BORDER);
		maxText.setLayoutData(new GridData (SWT.BEGINNING, SWT.CENTER, false, false));

		minText.setFormat(12, 4);
		maxText.setFormat(12, 4);
		minText.setIncrement(1.0);
		maxText.setIncrement(1.0);
	}
	/*
	 * Create the image settings, i.e. colour scheme section
	 */
	private void createImageSettings(Composite comp) {
		Section section = toolkit.createSection(comp, Section.DESCRIPTION
				| Section.TITLE_BAR);
		section.setLayout(GridLayoutFactory.fillDefaults().create());
		section.setLayoutData(GridDataFactory.fillDefaults().grab(true, false)
				.create());

		section.setText("Image Settings");
		section.setDescription("Colour scheme:");

		Composite colourComposite = toolkit.createComposite(section);
		colourComposite.setLayout(GridLayoutFactory.fillDefaults()
				.numColumns(2).create());
		colourComposite.setLayoutData(GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		// Label label = toolkit.createLabel(colourComposite, "Colour Scheme:");
		colourMapCombo = new CCombo(colourComposite, SWT.FLAT | SWT.DROP_DOWN
				| SWT.READ_ONLY | SWT.BORDER);
		colourMapCombo.setLayoutData(GridDataFactory.fillDefaults()
				.align(SWT.FILL, SWT.CENTER).grab(true, false).create());
		toolkit.adapt(colourMapCombo);
		toolkit.paintBordersFor(colourComposite);
		section.setClient(colourComposite);

		initializeColourMap();
	}


	/*
	 * Initialize colour map combo with available colour maps
	 */
	private void initializeColourMap() {
		// TODO: 1. populate the colourMapCombo control from extension point as per HistogramToolPage
		// TODO: 2. initialize the default selection to the specified colour map in preferences as per HistogramToolPage
		// TODO: 3. connect up a listener to react to changes in the colour map. Initially the colour map
		//			should be set on the image via the PaletteTrace. Don't worry about updating histogram yet.

	}

	/*
	 * Create the histogram section
	 */
	private void createHistogramControl(Composite comp) {
		Section section = toolkit.createSection(comp, Section.DESCRIPTION
				| Section.TITLE_BAR);
		section.setLayout(GridLayoutFactory.fillDefaults().create());
		section.setLayoutData(GridDataFactory.fillDefaults().grab(true, true)
				.create());

		section.setText("Histogram Plot");
		section.setDescription("Histogram information for active plot view");

		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(GridLayoutFactory.fillDefaults().create());
		sectionClient.setLayoutData(GridDataFactory.fillDefaults()
				.grab(true, true).create());

		final IPageSite site = getSite();
		IActionBars actionBars = (site != null) ? site.getActionBars() : null;

		createMinMaxSettings(form.getBody());

		try {
			histogramWidget = new HistogramViewer(sectionClient, getTitle(),
					null, actionBars);
		} catch (Exception e) {
			logger.error("Cannot locate any plotting systems!", e);
		}

		createSectionToolbar(section);

		GridData create = GridDataFactory.fillDefaults().hint(0, 200)
				.grab(true, true).create();
		histogramWidget.getControl().setLayoutData(create);

		histogramWidget.setContentProvider(new ImageHistogramProvider());

		toolkit.adapt(histogramWidget.getComposite());
		section.setClient(sectionClient);
	}

	/**
	 * Create toolbar
	 *
	 * @param section
	 * @param toolkit
	 */
	private void createSectionToolbar(Section control) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		ToolBar toolbar = toolBarManager.createControl(control);
		final Cursor handCursor = new Cursor(Display.getCurrent(),
				SWT.CURSOR_HAND);
		toolbar.setCursor(handCursor);
		// Cursor needs to be explicitly disposed
		toolbar.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				handCursor.dispose();
			}
		});

		Action reset = new Action("Reset histogram", IAction.AS_PUSH_BUTTON) {
			public void run() {

				final IContributionItem action = getPlottingSystem()
						.getActionBars().getToolBarManager()
						.find("org.dawb.workbench.plotting.histo");
				if (action != null && action.isVisible()
						&& action instanceof ActionContributionItem) {
					ActionContributionItem iaction = (ActionContributionItem) action;
					iaction.getAction().setChecked(
							!iaction.getAction().isChecked());
					iaction.getAction().run();
				}
			}
		};
		toolBarManager.add(reset);
		toolBarManager.update(true);

		control.setTextClient(toolbar);
	}
	@Override
	public void activate() {
		super.activate();

		Assert.isTrue(getPlottingSystem() != null, "Plotting system must not be null");

		logger.debug("HistogramToolPage: activate. Plotting System " + getPlottingSystem().hashCode());
		getPlottingSystem().addTraceListener(traceListener);

		IPaletteTrace paletteTrace = getPaletteTrace();
		if (paletteTrace != null){
			logger.debug("HistogramToolPage: activate - palette trace " + paletteTrace.hashCode());
			histogramWidget.setInput(paletteTrace);
		} else {
			logger.debug("HistogramToolPage: activate - palette trace is null.");
		}
	}

	@Override
	public void deactivate() {
		super.deactivate();

		logger.debug("HistogramToolPage: deactivate. Plotting System " + getPlottingSystem().hashCode());
		// comment out because at this point histogramWidget is disposed - is this right, verify
		// that this is expected/desired behaviour
		//histogramWidget.setInput(null);

		// remove our trace listener
		getPlottingSystem().removeTraceListener(traceListener);
	}

	@Override
	public boolean isAlwaysSeparateView() {
		return true;
	}

	@Override
	public Control getControl() {
		return form;
	}

	@Override
	public void setFocus() {
		form.setFocus();
	}

	private final class TraceListener implements ITraceListener{
		@Override
		public void traceWillPlot(TraceWillPlotEvent evt) {
			logger.debug("HistogramToolPage: traceWillPlotEvent");

			//use this event to modify the trace before we plot it
			// i.e. set colour palette etc
			// note the trace may include lots of null settings e.g. min/max

			// When we get this notification, the data is not ready in the trace
			// e.g. min gets set but not max/
			// we would have to turn off listeners or something
			IPaletteTrace it = (IPaletteTrace)evt.getSource();
			histogramWidget.setInput(it);
		}

		@Override
		public void tracesAdded(TraceEvent evt) {
			logger.debug("HistogramToolPage: tracesAdded");

		}

		@Override
		public void traceCreated(TraceEvent evt) {
			logger.debug("HistogramToolPage: traceCreated");

		}

		@Override
		public void traceUpdated(TraceEvent evt) {
			logger.debug("HistogramToolPage: traceUpdated");
			IPaletteTrace it = (IPaletteTrace)evt.getSource();
			histogramWidget.setInput(it);
		}

		@Override
		public void traceAdded(TraceEvent evt) {
			logger.debug("HistogramToolPage: traceAdded");
			IPaletteTrace it = (IPaletteTrace)evt.getSource();
			histogramWidget.setInput(it);
		}

		@Override
		public void traceRemoved(TraceEvent evt) {
			logger.debug("HistogramToolPage: traceRemoved");

		}

		@Override
		public void tracesUpdated(TraceEvent evt) {
			logger.debug("HistogramToolPage: tracesUpdated");

		}

		@Override
		public void tracesRemoved(TraceEvent evet) {
			logger.debug("HistogramToolPage: tracesRemoved");

		}


	};

}
