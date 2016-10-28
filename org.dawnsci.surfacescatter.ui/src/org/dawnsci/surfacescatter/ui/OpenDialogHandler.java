package org.dawnsci.surfacescatter.ui;

import java.util.ArrayList;
import java.util.Iterator;

import org.dawnsci.spectrum.ui.file.IContain1DData;
import org.dawnsci.surfacescatter.ExampleDialog;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class OpenDialogHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection)selection;
			
			ArrayList<String> selectedFile = new ArrayList<String>();
			
			
			
			Iterator it = ss.iterator();
			
			while (it.hasNext()) {
				Object ob = it.next();
				if (ob instanceof IContain1DData) {
					selectedFile.add(((IContain1DData)ob).getLongName());
				}
			}
			
			
			
			Object[] datFilenames1 = selectedFile.toArray();
			String[] datFilenames = new String[datFilenames1.length];
			
			for (int i =0; i< datFilenames1.length; i++){
				datFilenames[i]=(String) datFilenames1[i];
			}
			
			
			
			if (!selectedFile.isEmpty()) {
//				String string = selectedFile.get(0);
//				MessageBox box = new MessageBox(Display.getCurrent().getActiveShell());
//				box.setMessage(string);
//				box.open();
				
				ExampleDialog ed = new ExampleDialog(Display.getCurrent().getActiveShell(), datFilenames);
//				
				ed.open();
//				ed.createDialogArea(Display.getCurrent().getActiveShell().getParent());
			}

		}

		return null;
	}

}
