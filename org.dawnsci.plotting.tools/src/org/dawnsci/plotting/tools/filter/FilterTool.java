package org.dawnsci.plotting.tools.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.dawnsci.plotting.tools.Activator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.dawnsci.plotting.api.filter.AbstractDelayedFilter;
import org.eclipse.dawnsci.plotting.api.filter.FilterConfiguration;
import org.eclipse.dawnsci.plotting.api.filter.IFilterDecorator;
import org.eclipse.dawnsci.plotting.api.filter.IPlottingFilter;
import org.eclipse.dawnsci.plotting.api.tool.AbstractToolPage;
import org.eclipse.dawnsci.plotting.api.trace.IImageTrace;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * A tool which reads the plot filter extension point 
 * and allows the user to experiment with different filters.
 * 
 * For instance this tool can be used to apply the PHA algorithm 
 * and the fano factor.
 * 
 * @author fcp94556
 *
 */
public class FilterTool extends AbstractToolPage {

	
	/* Map id to filter */
	private Map<String, IPlottingFilter> filters;
	
	/* Map id to ui */
	private Map<String, Composite>       components;
	
	/* Map label to id */
	private Map<String, String>          labels;
	
	/* Current active filter */
	private IPlottingFilter  currentFilter;
	
	/* UI */
	private Combo            filterChoice;
	private Composite        control;
	private IFilterDecorator deco;
	private StackLayout      slayout;

	@Override
	public ToolPageRole getToolPageRole() {
		return ToolPageRole.ROLE_2D;
	}
	
	@Override
	public void createControl(Composite parent) {
		
		super.createControl(parent);
		
		this.deco = PlottingFactory.createFilterDecorator(getPlottingSystem());
		
		// Create the UI for the filters
		this.control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(2, false));	
		
		final Label label = new Label(control, SWT.WRAP);
		label.setText("Please choose a filter then press apply");
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		
		final Label filterName = new Label(control, SWT.NONE);
		filterName.setText("Filter");
		
		this.filterChoice = new Combo(control, SWT.READ_ONLY);
		filterChoice.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Composite   configuration = new Composite(control, SWT.BORDER);
		this.slayout       = new StackLayout();
		configuration.setLayout(slayout);
		configuration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		filters    = new HashMap<String, IPlottingFilter>(7);
		components = new HashMap<String, Composite>(7);
		labels     = new TreeMap<String, String>();
		
		String noneId = getClass().getName()+".none";
		filters.put(noneId,    null);
		components.put(noneId, new Composite(configuration, SWT.NONE));
		
		try {
			IConfigurationElement[] ele = Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.dawnsci.plotting.api.plottingFilter");
			for (IConfigurationElement e : ele) {
				final String        id     = e.getAttribute("id");
				final String        slabel = e.getAttribute("label");
				
				IPlottingFilter     filter = (IPlottingFilter)e.createExecutableExtension("filter");
				if (filter instanceof AbstractDelayedFilter) ((AbstractDelayedFilter)filter).setFilterName(slabel);
				
				FilterConfiguration config = (FilterConfiguration)e.createExecutableExtension("ui");
				labels.put(slabel, id);
				filters.put(id, filter);
				
				config.init(getPlottingSystem(), filter);
				Composite conf = config.createControl(configuration);
				components.put(id, conf);
			}
		} catch (Exception ne) {
			logger.error("Cannot read extension points for filters!", ne);
		}
		
		
		filterChoice.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final String label = filterChoice.getItem(filterChoice.getSelectionIndex());				
				setUISelected(label);
			}
		});
		
		final List<String> ls = new ArrayList<String>(labels.size()+1);
		ls.addAll(labels.keySet());
		
		labels.put("<None>", noneId);
		ls.add(0, "<None>");
		filterChoice.setItems(ls.toArray(new String[ls.size()]));
		filterChoice.select(0);
		
		final Composite buttons = new Composite(control, SWT.BORDER);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 2, 1));
		
		final Button apply = new Button(buttons, SWT.PUSH);
		apply.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		apply.setImage(Activator.getImage("icons/apply.gif"));
		apply.setText("Apply");
		apply.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				resetFilter();
				applyFilter();
			}
		});
		
		final Button reset = new Button(buttons, SWT.PUSH);
		reset.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
		reset.setImage(Activator.getImage("icons/reset.gif"));
		reset.setText("Reset");
		reset.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				reset();
			}
		});

		createActions();
	}

	private void reset() {
		filterChoice.select(0);
		resetFilter();
		setUISelected("<None>");
		getPlottingSystem().repaint();
	}

	private void createActions() {
		final IToolBarManager man = getSite().getActionBars().getToolBarManager();
		final IAction reset = new Action("Reset filter", Activator.getImageDescriptor("icons/reset.gif")) {
			public void run() {
				reset();
			}
		};
		man.add(reset);
	}

	protected void resetFilter() {
		deco.reset();
		try {
			if (currentFilter!=null) deco.removeFilter(currentFilter);
		} catch (Exception ne) {
			logger.debug("Error removing filter, might be because tool was deactivated.");
		}
	}

	private void setUISelected(String label) {
		
		final String id    = labels.get(label);
		slayout.topControl = components.get(id);
		
		for (Control c : control.getChildren()) if (c instanceof Composite) ((Composite)c).layout();

		currentFilter = filters.get(id);
	}
	
	private void applyFilter() {
		
		if (currentFilter!=null) {
			deco.addFilter(currentFilter);
		} else {
			reset();
			return;
		}
		
		IImageTrace image = getImageTrace();
		if (image!=null) image.setData(image.getData(), image.getAxes(), false);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setFocus() {
		control.setFocus();
	}

	public void activate() {
		super.activate();
		if (deco!=null && currentFilter!=null) {
			deco.addFilter(currentFilter);
		}
	}

	public void deactivate() {
        super.deactivate();
        resetFilter();
		setUISelected("<None>");
	}
}
