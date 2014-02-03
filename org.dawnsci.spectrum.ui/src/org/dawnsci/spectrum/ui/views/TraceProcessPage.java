package org.dawnsci.spectrum.ui.views;


import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.dawb.common.ui.menu.MenuAction;
import org.dawnsci.algorithm.ui.views.runner.AbstractAlgorithmProcessPage;
import org.dawnsci.algorithm.ui.views.runner.IAlgorithmProcessContext;
import org.dawnsci.plotting.api.IPlottingSystem;
import org.dawnsci.plotting.api.PlottingFactory;
import org.dawnsci.plotting.api.axis.IAxis;
import org.dawnsci.plotting.api.filter.AbstractPlottingFilter;
import org.dawnsci.plotting.api.filter.IFilterDecorator;
import org.dawnsci.plotting.api.histogram.IPaletteService;
import org.dawnsci.plotting.api.trace.ColorOption;
import org.dawnsci.plotting.api.trace.ILineTrace;
import org.dawnsci.plotting.api.trace.ITrace;
import org.dawnsci.spectrum.ui.Activator;
import org.dawnsci.spectrum.ui.file.IContain1DData;
import org.dawnsci.spectrum.ui.file.ISpectrumFile;
import org.dawnsci.spectrum.ui.file.ISpectrumFileListener;
import org.dawnsci.spectrum.ui.file.SpectrumFileManager;
import org.dawnsci.spectrum.ui.file.SpectrumFileOpenedEvent;
import org.dawnsci.spectrum.ui.utils.SpectrumUtils;
import org.dawnsci.spectrum.ui.wizard.SpectrumWizard;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ResourceTransfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Maths;

/**
* Main view of the Spectrum Perspective. Contains a table which shows the list of active files and 
* has actions for viewing and processing files in different ways.
* <p>
* Hold the SpectrumFileManager which takes care of loading and plotting files
*/
public class TraceProcessPage extends AbstractAlgorithmProcessPage {
	
	// Jake start using this more please :)
	private Logger logger = LoggerFactory.getLogger(TraceProcessPage.class);

	private SpectrumFileManager manager;
	private IPlottingSystem     system;
	private CheckboxTableViewer viewer;
	private List<Color>         orderedColors;

	private Action removeAction;
	private Action configDefaults;

	public Composite createPartControl(Composite parent) {
		
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
		IWorkbenchPage page = getView().getSite().getPage();
		IViewPart view = page.findView("org.dawnsci.spectrum.ui.views.SpectrumPlot");
		system = (IPlottingSystem)view.getAdapter(IPlottingSystem.class);
		manager = new SpectrumFileManager(system);
		
		viewer.setInput(manager);
		
		manager.addFileListener( new ISpectrumFileListener() {

			@Override
			public void fileLoaded(final SpectrumFileOpenedEvent event) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						viewer.refresh();
						viewer.setSelection(new StructuredSelection(event.getFile()),true);
						viewer.setChecked(event.getFile(), true);
					}
				});
			}
		});

		getView().getSite().setSelectionProvider(viewer);
		
		//Set up drag-drop
		DropTargetAdapter dropListener = new DropTargetAdapter() {
			@Override
			public void drop(DropTargetEvent event) {
				Object dropData = event.data;
				if (dropData instanceof TreeSelection) {
					TreeSelection selectedNode = (TreeSelection) dropData;
					Object obj[] = selectedNode.toArray();
					for (int i = 0; i < obj.length; i++) {
						if (obj[i] instanceof IFile) {
							IFile file = (IFile) obj[i];
							manager.addFile(file.getRawLocation().toOSString());
						}
					}
				} else if (dropData instanceof String[]) {
					for (String path : (String[])dropData){
						manager.addFile(path);
					}
				}
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
				// TODO Auto-generated method stub
				Object ob = event.getElement();
				
				if (ob instanceof ISpectrumFile) {
					if (event.getChecked()) {
						((ISpectrumFile)ob).setShowPlot(true);
					} else {
						((ISpectrumFile)ob).setShowPlot(false);
					}
				}
			}
		});
		
		//set axis as tight
		List<IAxis> axes = system.getAxes();
		for (IAxis axis : axes) axis.setAxisAutoscaleTight(true);
		system.setColorOption(ColorOption.BY_NAME);
		
		// Currently we do not want the run actions visible
		final IToolBarManager man = getView().getViewSite().getActionBars().getToolBarManager();
		if (man.find(IAlgorithmProcessContext.RUN_ID_STUB)!=null) man.find(IAlgorithmProcessContext.RUN_ID_STUB).setVisible(false);
		if (man.find(IAlgorithmProcessContext.STOP_ID_STUB)!=null) man.find(IAlgorithmProcessContext.STOP_ID_STUB).setVisible(false);

		return viewer.getTable();
	}
	

	@Override
	public String getTitle() {
		return "Traces";
	}

	@Override
	public void run(final IAlgorithmProcessContext context) throws Exception {
		
		if (manager.isEmpty()) {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Empty File List", "There are currently no files to process.\n\nPlease double click to add files from the 'Trace Project' view.");
		    return;
		}
		
		// We save the manager to file. This then is read by the file
		// reader source and each file is processed.
		final File exportFile = File.createTempFile("SpectrumFiles", ".csv");
		manager.export(exportFile); // saves out file list with data sets so that pipeline can process.
		
		System.setProperty("import.file.path", exportFile.getAbsolutePath());
		
		final Job process = new Job(context.getTitle()) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					final IFile moml = SpectrumWorkflowCreator.createWorkflowFileIfRequired("trace", 
							                                                                "trace_workflow.moml", 
							                                                                context.getFullPath(), 
							                                                                monitor);
					context.execute(moml.getLocation().toOSString(), true, monitor);
					return Status.OK_STATUS;
				} catch (Exception e) {
					logger.error("Cannot process '"+context.getTitle()+"'");
					return Status.CANCEL_STATUS;
				}
			}
			
		};
		process.setPriority(Job.SHORT);
		process.schedule(2);
	}

	@Override
	public ISourceProvider[] getSourceProviders() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getAdapter(final Class clazz) {
		if (clazz == SpectrumFileManager.class) return manager;
		return null;
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				TraceProcessPage.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getView().getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getView().getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager menuManager) {
		menuManager.add(removeAction);
		
		menuManager.add(new Separator());
		
		Action orderColors = new Action("Jet Color Plotted Traces", Activator.getImageDescriptor("icons/color.png")) {
			public void run(){

				if (orderedColors == null) {
					final IPaletteService pservice = (IPaletteService)PlatformUI.getWorkbench().getService(IPaletteService.class);
					PaletteData paletteData = pservice.getDirectPaletteData("Jet (Blue-Cyan-Green-Yellow-Red)");
					RGB[] rgbs = paletteData.getRGBs();
					orderedColors = new ArrayList<Color>(256);
					Display display = Display.getDefault();
					for(int i = 0; i < 256 ; i++) {
						orderedColors.add(new Color(display, rgbs[i]));
					}
				}

				Collection<ITrace> traces = system.getTraces(ILineTrace.class);
				double count = 0;
				for (ITrace trace : traces) if (trace.isUserTrace()) count++;
				
				double val = 255/(count-1);
				int i = 0;
				for (ITrace trace : traces) {
					if (trace.isUserTrace()) {
						((ILineTrace)trace).setTraceColor(orderedColors.get((int)val*i));
						i++;
					}
					
				}
			}
		};
		
		menuManager.add(orderColors);
		menuManager.add(new Separator());
		menuManager.add(configDefaults);
	}

	private void fillContextMenu(IMenuManager manager) {
		
		MenuManager menuProcess = new MenuManager("Process",
				Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/function.png"),
				"org.dawnsci.spectrum.ui.views.processingmenu");
		
		menuProcess.add(new Action("Average") {
			public void run() {
				ISelection selection = viewer.getSelection();
				List<IContain1DData> list = SpectrumUtils.get1DDataList((IStructuredSelection)selection);
				ISpectrumFile file = SpectrumUtils.averageSpectrumFiles(list,system);
				
				if (file == null) {
					showMessage("Could not process dataset, operation not supported for this data!");
					return;
				}
				
				TraceProcessPage.this.manager.addFile(file);
			}
		});
		
		menuProcess.add(new Action("Wizard...") {
			public void run() {
				SpectrumWizard sw = new SpectrumWizard();
				ISelection selection = viewer.getSelection();
				List<IContain1DData> list = SpectrumUtils.get1DDataList((IStructuredSelection)selection);
				sw.add1DDatas(list);
				WizardDialog wd = new WizardDialog(Display.getDefault().getActiveShell(),sw);
				wd.open();
			}
		});
		
		if (((IStructuredSelection)viewer.getSelection()).size() == 2) {

			menuProcess.add(new Action("Subtract") {
				public void run() {
					ISelection selection = viewer.getSelection();
					List<IContain1DData> list = SpectrumUtils.get1DDataList((IStructuredSelection)selection);
					ISpectrumFile[] files = SpectrumUtils.subtractSpectrumFiles(list,system);

					if (files == null) {
						showMessage("Could not process dataset, operation not supported for this data!");
						return;
					}

					TraceProcessPage.this.manager.addFile(files[0]);
					TraceProcessPage.this.manager.addFile(files[1]);
				}
			});
		}
		manager.add(menuProcess);
		manager.add(new Separator());
		manager.add(removeAction);

		// Other plug-ins can contribute there actions here
		
		manager.add(new Separator());
		manager.add(new Action("Check Selected", Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/ticked.png")) {
			@Override
			public void run() {
				setSelectionChecked(true);
			}

		});
		
		manager.add(new Action("Uncheck Selected", Activator.imageDescriptorFromPlugin("org.dawnsci.spectrum.ui","icons/unticked.gif")) {
			@Override
			public void run() {
				setSelectionChecked(false);
			}

		});
		
		manager.add(new Separator());
		manager.add(configDefaults);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
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
				IDataset newY = Maths.add(DatasetUtils.norm((AbstractDataset)y),(traces.size()*0.2));
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
				IDataset newY = DatasetUtils.norm((AbstractDataset)y);
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
				IDataset newY = Maths.add(DatasetUtils.norm((AbstractDataset)y),(traces.size()*1));
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