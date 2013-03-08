/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 

package org.dawb.workbench.ui.editors.actions;

import org.dawb.common.services.IVariableManager;
import org.dawb.common.ui.plot.tool.IToolContainer;
import org.dawb.common.ui.plot.tool.IToolPage;
import org.dawb.common.ui.slicing.ISlicablePlottingPart;
import org.dawb.common.ui.util.EclipseUtils;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;


public class VariableManagerUtils {

	/**
	 * Trys to find an active IVariableManager 
	 * @return DataSetPlotView
	 */
	public static IVariableManager getActiveComponent() {
		
		IVariableManager sets =  null;
		
		try {
		    final IWorkbenchPart part = EclipseUtils.getActivePage().getActivePart();
		    if (part instanceof IVariableManager) return (IVariableManager)part;
		    
		    if (part instanceof IToolContainer) {
		    	IToolContainer  cont = (IToolContainer)part;
		    	final IToolPage page = cont.getActiveTool(); 
		    	if (page !=null && page instanceof IVariableManager) return (IVariableManager)page;
		    }
		    
		    if (part instanceof PageBookView) {
		    	PageBookView pview = (PageBookView)part;
		    	IPage        page  = pview.getCurrentPage();
		    	if (page !=null && page instanceof IVariableManager) return (IVariableManager)page;
		    }
		
		} catch (Exception ignored) {
			// Look at active editor instead
		}
		    
		IEditorPart editor = EclipseUtils.getActivePage().getActiveEditor();
		if (editor!=null) {
			if (editor instanceof ISlicablePlottingPart) {
				sets = ((ISlicablePlottingPart)editor).getDataSetComponent();
			}

		}
	
		return sets; // Might still be null
	}
}
