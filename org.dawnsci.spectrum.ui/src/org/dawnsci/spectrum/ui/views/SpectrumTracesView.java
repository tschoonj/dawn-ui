/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.dawnsci.spectrum.ui.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dawb.common.ui.menu.MenuAction;
import org.dawb.common.ui.util.EclipseUtils;
import org.dawnsci.spectrum.ui.Activator;
import org.dawnsci.spectrum.ui.file.IContain1DData;
import org.dawnsci.spectrum.ui.file.ISpectrumFile;
import org.dawnsci.spectrum.ui.file.ISpectrumFileListener;
import org.dawnsci.spectrum.ui.file.SpectrumFile;
import org.dawnsci.spectrum.ui.file.SpectrumFileEvent;
import org.dawnsci.spectrum.ui.file.SpectrumFileManager;
import org.dawnsci.spectrum.ui.file.SpectrumInMemory;
import org.dawnsci.spectrum.ui.processing.SaveProcess;
import org.dawnsci.spectrum.ui.processing.SaveTextProcess;
import org.dawnsci.spectrum.ui.utils.SpectrumUtils;
import org.dawnsci.spectrum.ui.wizard.SaveFileWizardPage;
import org.dawnsci.spectrum.ui.wizard.SpectrumWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.dawnsci.plotting.api.IPlottingSystem;
import org.eclipse.dawnsci.plotting.api.PlottingFactory;
import org.eclipse.dawnsci.plotting.api.axis.IAxis;
import org.eclipse.dawnsci.plotting.api.filter.AbstractPlottingFilter;
import org.eclipse.dawnsci.plotting.api.filter.IFilterDecorator;
import org.eclipse.dawnsci.plotting.api.histogram.IPaletteService;
import org.eclipse.dawnsci.plotting.api.trace.ColorOption;
import org.eclipse.dawnsci.plotting.api.trace.ILineTrace;
import org.eclipse.dawnsci.plotting.api.trace.ITrace;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.Maths;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* Main view of the Spectrum Perspective. Contains a table which shows the list of active files and 
* has actions for viewing and processing files in different ways.
* <p>
* Hold the SpectrumFileManager which takes care of loading and plotting files
*/
public class SpectrumTracesView extends ViewPart {
	
	// Jake start using this more please :)
	private Logger logger = LoggerFactory.getLogger(SpectrumTracesView.class);

	private SpectrumFileManager manager;
	private IPlottingSystem<Composite>     system;
	private CheckboxTableViewer viewer;
	private List<Color>         orderedColors;
	private ProcessMenuManager processMenuManager;

	private Action removeAction;
	private Action configDefaults;

	@Override
	public void createPartControl(Composite parent) {
		
		//Create table
		viewer = CheckboxTableViewer.newCheckList(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		viewerColumn.setLabelProvider(new ViewLabelProvider());
	 
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableColumnLayout.setColumnData(viewerColumn.getColumn(),
		        new ColumnWeightData(1));
		parent.setLayout(tableColumnLayout);
		
		ColumnViewerToolTipSupport.enableFor(viewer);
		
		//Get plotting system from PlotView, use it to create file manager
		IWorkbenchPage page = getSite().getPage();
		IViewPart view = page.findView("org.dawnsci.spectrum.ui.views.SpectrumPlot");
		system = (IPlottingSystem<Composite>)view.getAdapter(IPlottingSystem.class);
		manager = new SpectrumFileManager(system);
		
		viewer.setInput(manager);
		
		manager.addFileListener(new ISpectrumFileListener() {
			@Override
			public void fileLoaded(final SpectrumFileEvent event) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						ISpectrumFile file = updateSelection(event);
						file.setShowPlot(true);
					}
				});
			}

			@Override
			public void fileRemoved(SpectrumFileEvent event) {
				updateSelection(event);
			}
		});
		
		processMenuManager = new ProcessMenuManager(viewer, manager, system);

		getSite().setSelectionProvider(viewer);
		
		//Set up drag-drop
		DropTargetAdapter dropListener = new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				Object dropData = event.data;
				List<String> paths = new ArrayList<String>();
				if (dropData instanceof TreeSelection) {
					TreeSelection selectedNode = (TreeSelection) dropData;
					Object obj[] = selectedNode.toArray();
					for (int i = 0; i < obj.length; i++) {
						if (obj[i] instanceof IFile) {
							IFile file = (IFile) obj[i];
							paths.add(file.getRawLocation().toOSString());
						}
					}
				} else if (dropData instanceof String[]) {
					for (String path : (String[])dropData){
						paths.add(path);
					}
				}
				manager.addFiles(paths);
			}
		};
		
		DropTarget dt = new DropTarget(viewer.getControl(), DND.DROP_MOVE | DND.DROP_DEFAULT | DND.DROP_COPY);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance(),
				FileTransfer.getInstance(), ResourceTransfer.getInstance(),
				LocalSelectionTransfer.getTransfer() });
		dt.addDropListener(dropListener);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "org.dawnsci.spectrum.viewer");
		makeActions();
		hookContextMenu();
		//hookDoubleClickAction();
		contributeToActionBars();

		//hook up delete key to remove from list
		viewer.getTable().addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					removeAction.run();
				}
			}
		});

		//Highlight trace on selection
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				List<ISpectrumFile> list = SpectrumUtils.getSpectrumFilesList((IStructuredSelection)event.getSelection());
				for (ISpectrumFile file : manager.getFiles()) {
					if (list.contains(file)) {
						file.setSelected(true);
					} else {
						file.setSelected(false);
					}
				}
			}
		});
		
		viewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				Object ob = event.getElement();

				if (ob instanceof ISpectrumFile) {
					if (event.getChecked()) {
						((ISpectrumFile) ob).setShowPlot(true);
					} else {
						((ISpectrumFile) ob).setShowPlot(false);
					}
				}
			}
		});

		//set axis as tight
		List<IAxis> axes = system.getAxes();
		for (IAxis axis : axes) axis.setAxisAutoscaleTight(true);
		system.setColorOption(ColorOption.BY_NAME);

		logger.debug("Controls created");
	}

	private ISpectrumFile updateSelection(SpectrumFileEvent event) {
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(event.getFile()), true);
		ISpectrumFile file = event.getFile();
		viewer.setChecked(file, true);
		return file;
	}

	@Override
	public String getTitle() {
		return "Traces";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class clazz) {
		if (clazz == SpectrumFileManager.class) return manager;
		return super.getAdapter(clazz);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				SpectrumTracesView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(removeAction);

		menuManager.add(new Separator());

		Action orderColors = new Action("Jet Color Plotted Traces", Activator.getImageDescriptor("icons/color.png")) {
			@Override
			public void run() {
				if (orderedColors == null) {
					final IPaletteService pservice = (IPaletteService) PlatformUI.getWorkbench()
							.getService(IPaletteService.class);
					PaletteData paletteData = pservice.getDirectPaletteData("Jet (Blue-Cyan-Green-Yellow-Red)");
					RGB[] rgbs = paletteData.getRGBs();
					orderedColors = new ArrayList<Color>(256);
					Display display = Display.getDefault();
					for (int i = 0; i < 256; i++) {
						orderedColors.add(new Color(display, rgbs[i]));
					}
				}

				Collection<ITrace> traces = system.getTraces(ILineTrace.class);
				double count = 0;
				for (ITrace trace : traces)
					if (trace.isUserTrace())
						count++;

				double val = 255 / (count - 1);
				int i = 0;
				for (ITrace trace : traces) {
					if (trace.isUserTrace()) {
						((ILineTrace) trace).setTraceColor(orderedColors.get((int) val * i));
						i++;
					}

				}
			}
		};
		menuManager.add(orderColors);
		menuManager.add(new Separator());
		menuManager.add(configDefaults);
	}

	private void fillContextMenu(IMenuManager menuManager) {
		
		processMenuManager.fillProcessMenu(menuManager);

		menuManager.add(new Separator());
		
		if (((IStructuredSelection)viewer.getSelection()).size() == 1) {

			List<ISpectrumFile> file = SpectrumUtils.getSpectrumFilesList((IStructuredSelection)viewer.getSelection());

			if (!file.isEmpty() && file.get(0) instanceof SpectrumInMemory) {
				menuManager.add(new Action("Save HDF5...") {
					public void run() {
						SpectrumWizard sw = new SpectrumWizard();
						ISelection selection = viewer.getSelection();
						List<IContain1DData> list = SpectrumUtils.get1DDataList((IStructuredSelection)selection);
						WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(),sw);
						sw.addPage(new SaveFileWizardPage(new SaveProcess()));
						sw.setData(list);
						wd.open();
					}
				});
				menuManager.add(new Action("Save text...") {
					public void run() {
						SpectrumWizard sw = new SpectrumWizard();
						ISelection selection = viewer.getSelection();
						List<IContain1DData> list = SpectrumUtils.get1DDataList((IStructuredSelection)selection);
						WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(),sw);
						sw.addPage(new SaveFileWizardPage(new SaveTextProcess()));
						sw.setData(list);
						wd.open();
					}
				});
				menuManager.add(new Separator());
			}
		}
		menuManager.add(removeAction);

		// Other plug-ins can contribute there actions here
		
		menuManager.add(new Separator());
		menuManager.add(new Action("Check Selected", Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/ticked.png")) {
			@Override
			public void run() {
				setSelectionChecked(true);
			}

		});
		
		menuManager.add(new Action("Uncheck Selected", Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/unticked.gif")) {
			@Override
			public void run() {
				setSelectionChecked(false);
			}

		});
		
		if (((IStructuredSelection)viewer.getSelection()).size() == 1) {

			menuManager.add(new Separator());

			menuManager.add(new Action("Open in Data Browsing Perspective",Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/application_view_gallery.png")) {
				@Override
				public void run() {
					try {
						ISelection selection = viewer.getSelection();
						List<ISpectrumFile> list = SpectrumUtils.getSpectrumFilesList((IStructuredSelection)selection);
						if (list.isEmpty()) return;
						if (!(list.get(0) instanceof SpectrumFile)) {
							showMessage("Could not open perspective, operation not supported for this data!");
							return;
						}

						PlatformUI.getWorkbench().showPerspective("org.edna.workbench.application.perspective.DataPerspective",PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						EclipseUtils.openExternalEditor(list.get(0).getLongName());

					} catch (WorkbenchException e) {
						showMessage("Could not open perspective, operation not supported for this data! : " + e.getMessage());
						e.printStackTrace();
					} 
				}
			});
		}
		
		menuManager.add(new Separator());
		menuManager.add(configDefaults);
		menuManager.add(new Separator());
		menuManager.add( new Action("Copy Python Code to Clipboard",Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/clipboard.png")) {
			@Override
			public void run() {
				String code = SpectrumUtils.getPythonLoadString(manager);
				Clipboard cb = new Clipboard(Display.getCurrent());
				TextTransfer textTransfer = TextTransfer.getInstance();
		        cb.setContents(new Object[] { code },
		            new Transfer[] { textTransfer });
			}
		});
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		
		createXYFiltersActions(manager);
		
		manager.add(removeAction);
	}

	private void setSelectionChecked(boolean checked) {
		IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
		List<ISpectrumFile> list = SpectrumUtils.getSpectrumFilesList(selection);
		
		for (ISpectrumFile file : list) {
			file.setShowPlot(checked);
			viewer.setChecked(file, checked);
		}
	}

	private void makeActions() {		
		
		removeAction = new Action("Remove From List",Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/delete.gif")) {
			public void run() {
				ISelection selection = viewer.getSelection();
				List<?> obj = ((IStructuredSelection)selection).toList();
				for (Object ob : obj) manager.removeFile(((ISpectrumFile)ob).getLongName());
				//Todo change selection
				
				if (manager.isEmpty()) system.clear();
				int i = viewer.getTable().getItemCount();
				
				if (i > 0) {
					Object ob = viewer.getTable().getItem(i-1).getData();
					viewer.setSelection(new StructuredSelection(ob),true);
				}
				
			}
		};
		
		removeAction.setToolTipText("Remove selected files from list");
		
		configDefaults = new Action("Configure Default Dataset Names...",Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/book-brown-setting.png")) {
			@Override
			public void run() {
				PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "org.dawnsci.spectrum.ui.preferences.page", null, null);
				if (pref != null) pref.open();
			}
		};
	}	

	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Sample View",
			message);
	}
	
	@Override
	public void dispose() {	
		system.dispose();
		manager.dispose();
		if (orderedColors != null) for (Color color : orderedColors) color.dispose();
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
	public void createXYFiltersActions(IToolBarManager manager) {

		final IFilterDecorator dec = PlottingFactory.createFilterDecorator(system);
		final AbstractPlottingFilter stack = new AbstractPlottingFilter() {

			@Override
			public int getRank() {
				return 1;
			}

			protected IDataset[] filter(IDataset x, IDataset y) {
				Collection<ITrace>  traces = system.getTraces(ILineTrace.class);
				IDataset newY = Maths.add(DatasetUtils.norm(DatasetUtils.convertToDataset(y)),(traces.size()*0.2));
				newY.setName(y.getName());

				return new IDataset[]{x, newY};
			}
		};

		final AbstractPlottingFilter norm = new AbstractPlottingFilter() {

			@Override
			public int getRank() {
				return 1;
			}

			protected IDataset[] filter(IDataset x, IDataset y) {
				IDataset newY = DatasetUtils.norm(DatasetUtils.convertToDataset(y));
				return new IDataset[]{x, newY};
			}
		};
		
		final AbstractPlottingFilter offset = new AbstractPlottingFilter() {

			@Override
			public int getRank() {
				return 1;
			}

			protected IDataset[] filter(IDataset x, IDataset y) {
				Collection<ITrace>  traces = system.getTraces(ILineTrace.class);
				IDataset newY = Maths.add(DatasetUtils.norm(DatasetUtils.convertToDataset(y)),(traces.size()*1));
				newY.setName(y.getName());

				return new IDataset[]{x, newY};
			}
		};
		
		
		IAction none = new Action("None", IAction.AS_RADIO_BUTTON) {
			public void run(){
				if (!isChecked()) return;
				dec.clear();
				replot();
			}

		};
		
		IAction normalize = new Action("Min/Max", IAction.AS_RADIO_BUTTON) {
			public void run(){
				if (!isChecked()) return;
				dec.clear();
				dec.addFilter(norm);
				replot();

			}
		};
		
		IAction stackAc = new Action("Stack", IAction.AS_RADIO_BUTTON) {
			public void run(){
				if (!isChecked()) return;
				dec.clear();
				dec.addFilter(stack);
				replot();
			}
		};
		
		IAction offAc = new Action("Offset", IAction.AS_RADIO_BUTTON) {
			public void run(){
				if (!isChecked()) return;
				dec.clear();
				dec.addFilter(offset);
				replot();
			}
		};
		
		MenuAction m = new MenuAction("Display");
		none.setChecked(true);
		m.add(none);
		m.add(normalize);
		m.add(stackAc);
		m.add(offAc);
		
		manager.add(m);
	}
	
	private void replot(){
		
		final Collection<ITrace> traces = system.getTraces(ILineTrace.class);
		for (ITrace trace: traces) system.removeTrace(trace);
		
		Collection<ISpectrumFile> files = manager.getFiles();
		for (ISpectrumFile file : files) file.plotAll();
		//final Collection<ITrace> traces = system.getTraces(ILineTrace.class);
//		for (ITrace trace: traces) system.removeTrace(trace);
//		for (ITrace trace: traces) system.addTrace(trace);
		if (system.isRescale())
			system.autoscaleAxes();
	}
	
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return manager.getFiles().toArray();
		}
	}
	
	class ViewLabelProvider extends ColumnLabelProvider implements ITableLabelProvider {
		
		Image fileImage;
		Image memoryImage;
		
		public ViewLabelProvider() {
			fileImage = Activator.getImage("icons/Multiple.png");
			memoryImage = Activator.getImage("icons/MultipleDisk.png");
		}

		
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public String getText(Object obj) {
			if (obj instanceof ISpectrumFile) {
				return ((ISpectrumFile)obj).getName();
			}
			
			return "";
		}
		@Override
		public Image getImage(Object obj) {
			
			if (!(obj instanceof ISpectrumFile)) {
				return null;
			}
			
			ISpectrumFile file = (ISpectrumFile)obj;
			
	
			if (file.canBeSaved()) return memoryImage;
			else return fileImage;
		}
		@Override
		public String getToolTipText(Object obj) {
			if (obj instanceof ISpectrumFile) {
				return ((ISpectrumFile)obj).getLongName();
			}
			
			return "";
		}
	
		@Override
		public void dispose() {
			super.dispose();
			
			memoryImage.dispose();
			fileImage.dispose();
		}
	}
}
