package org.dawnsci.common.widgets.gda.function.internal;

import org.dawnsci.common.widgets.gda.function.FunctionTreeViewer;
import org.dawnsci.common.widgets.gda.function.internal.model.ParameterModel;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;

public abstract class ValueColumnEditingSupport extends EditingSupport
		implements IGetSetValueOnParameterModel, ITextEditingSupport {

	private TextCellEditor tce;
	private FunctionTreeViewer viewer;

	public ValueColumnEditingSupport(FunctionTreeViewer viewer) {
		super(viewer.getTreeViewer());
		this.viewer = viewer;
		tce = new TextCellEditor(viewer.getTreeViewer().getTree());
	}

	protected boolean canEdit(Object element) {
		if (element instanceof ParameterModel) {
			return true;
		} else {
			return false;
		}
	}

	protected CellEditor getCellEditor(Object element) {
		return tce;
	}

	protected Object getValue(Object element) {
		if (element instanceof ParameterModel) {
			ParameterModel param = (ParameterModel) element;
			String errorValue = getErrorValue(param);
			if (errorValue == null)
				return String.valueOf(getValue(param));
			else
				return errorValue;
		}
		return element.toString();
	}

	protected void setValue(Object element, Object value) {
		if (element instanceof ParameterModel && value instanceof String) {
			ParameterModel parameterModel = (ParameterModel) element;
			String string = (String) value;
			setValue(parameterModel, string);
			viewer.refresh(parameterModel.getParameter());
		}

	}

	/**
	 * @return the tce
	 */
	public TextCellEditor getTextCellEditor() {
		return tce;
	}
}