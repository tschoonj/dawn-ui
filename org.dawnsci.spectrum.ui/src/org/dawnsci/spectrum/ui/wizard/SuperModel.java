package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class SuperModel {

	private String[] filepaths;
	private int selection;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);


	public String[] getFilepaths() {
		return filepaths;
	}

	public void setFilepaths(String[] filepaths) {
		this.filepaths = filepaths;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
		firePropertyChange("selection", this.selection, this.selection= selection);
	}
	
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName,
				listener);
	}
	
	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue,
				newValue);
	}
	
}
