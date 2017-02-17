package org.dawnsci.surfacescatter.ui;

import org.dawnsci.surfacescatter.SuperModel;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class PresenterInitialSetup extends Dialog {


	private Combo correctionsDropDown;
	private Button goButton;
	private Button imageFolderSelection;
	private Button datFolderSelection;
	private Shell parentShell; 
    private String[] filepaths;
    private int correctionSelection;
    private String imageFolderPath = null;
    private String datFolderPath = null;
    private SurfaceScatterPresenter ssp; 
	
	protected PresenterInitialSetup(Shell parentShell, 
								    String[] filepaths) {
		
		super(parentShell);

		this.parentShell = parentShell;
//		this.filepaths = filepaths;
		

		setShellStyle(getShellStyle() | SWT.RESIZE);
		
	}

	@Override
	protected Control createDialogArea(Composite parent) {
	
		
//		SuperModel sm = new SuperModel();
		
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		
///////////////////////////.dat Folder selector///////////////////////////////////////////////////
		
//		datFolderSelection = new Button(container, SWT.PUSH | SWT.FILL);
//		
//		datFolderSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		datFolderSelection.setText("Select .dat File Folder");
//		
//		datFolderSelection.addSelectionListener(new SelectionListener() {
//		
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//			
//				FileDialog fd = new FileDialog(getParentShell(), SWT.OPEN); 
//				
//				String path = "p";
//				
//				if (fd.open() != null) {
//					path = fd.getFilterPath();
//				}
//				
//				datFolderPath = path;
//				
//				}
//				
//				@Override
//				public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//			
//			}
//		});
//
//
/////////////////////////////////////////////////////////////////////////////////
//		
//		
//		
/////////////////////////////Image Folder selector///////////////////////////////////////////////////
//				
//		imageFolderSelection = new Button(container, SWT.PUSH | SWT.FILL);
//		
//		imageFolderSelection.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		imageFolderSelection.setText("Select Images Folder");
//		
//		imageFolderSelection.addSelectionListener(new SelectionListener() {
//			
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				
//				FileDialog fd = new FileDialog(getParentShell(), SWT.OPEN); 
//				
//				String path = "p";
//				
//				if (fd.open() != null) {
//					path = fd.getFilterPath();
//				}
//				
//				imageFolderPath = path;
//				
//			}
//			
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		
		
///////////////////////////////////////////////////////////////////////////////
		
		
		goButton = new Button(container, SWT.PUSH | SWT.FILL);
		
		goButton.setText("GO!");
		goButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		goButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {

				
				ssp = new SurfaceScatterPresenter();

				
				ssp.setImageFolderPath(imageFolderPath);
				
				SurfaceScatterViewStart ssvs = new SurfaceScatterViewStart(parentShell, 
						   null, 
						   ssp.getNumberOfImages(), 
						   ssp.getImage(0),
						   ssp,
						   null);
//						   datFolderPath);
				
				ssp.setSsvs(ssvs);
				
				ssvs.open();
				
				
		}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		return container;
		
	}

	
}
