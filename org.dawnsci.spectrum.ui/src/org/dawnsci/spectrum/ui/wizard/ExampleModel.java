package org.dawnsci.spectrum.ui.wizard;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.Methodology;
import org.dawnsci.spectrum.ui.wizard.AnalaysisMethodologies.FitPower;
import org.eclipse.dawnsci.analysis.api.roi.IROI;
import org.eclipse.dawnsci.analysis.api.roi.IRectangularROI;
import org.eclipse.dawnsci.analysis.dataset.roi.RectangularROI;
import org.eclipse.january.dataset.AggregateDataset;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ILazyDataset;

public class ExampleModel {
	
	private int imageNumber = 0;
	private IROI ROI;
	private int[][] lenpt;
	private IDataset currentImage;
	private ArrayList<ILazyDataset> arrayILD;
	private int sliderPos;
	private float iterationMarker =0;
	private AggregateDataset aggDat;
	private RectangularROI box;
	private IDataset input; 
	private int boundaryBox;
	private Methodology methodology;
	private double[] trackerCoordinates = {100,100,110,100,110,100,110,110};
	
	public IDataset getInput() {
		return input;
	}

	public void setInput(IDataset input) {
		this.input = input;
	}

	
	
	public double[] getTrackerCoordinates() {
		return trackerCoordinates;
	}

	public void setTrackerCoordinates(double[] trackerCoordinates) {
		this.trackerCoordinates = trackerCoordinates;
	}

	public Methodology getMethodology() {
		return methodology;
	}

	public void setMethodology(Methodology methodology) {
		this.methodology = methodology;
	}

	private FitPower fitPower;

	
	public int getBoundaryBox() {
		return boundaryBox;
	}

	public void setBoundaryBox(int boundaryBox) {
		this.boundaryBox = boundaryBox;
	}

	public FitPower getFitPower() {
		return fitPower;
	}

	public void setFitPower(FitPower fitPower1) {
		this.fitPower = fitPower1;
	}

	public RectangularROI getBox() {
		return box;
	}

	public void setBox(RectangularROI box) {
		firePropertyChange("box", this.box, this.box= box);
	}

	public AggregateDataset getAggDat() {
		return aggDat;
	}

	public void setAggDat(AggregateDataset aggDat) {
		this.aggDat = aggDat;
	}

	public float getIterationMarker() {
		return iterationMarker;
	}

	public void setIterationMarker(float iterationMarker) {
		firePropertyChange("iterationMarker", this.iterationMarker, this.iterationMarker= iterationMarker);
	}
	
	public int getSliderPos() {
		return sliderPos;
	}

	public void setSliderPos(int sliderPos) {
		this.sliderPos = sliderPos;
	}

	public ArrayList<ILazyDataset> getArrayILD() {
		return arrayILD;
	}

	public void setArrayILD(ArrayList<ILazyDataset> arrayILD) {
		this.arrayILD = arrayILD;
	}

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public IROI getROI(){
		return ROI;
	}

	public void setROI(IROI ROI) {
		firePropertyChange("ROI", this.ROI, this.ROI= ROI);
		IRectangularROI bounds = ROI.getBounds();
		int[] len = bounds.getIntLengths();
		int[] pt = bounds.getIntPoint();
		int[][] lenpt = new int[2][];
		lenpt[0]=len;
		lenpt[1]=pt;
		firePropertyChange("ROI", this.ROI, this.ROI= ROI);
		this.setLenPt(lenpt);
		firePropertyChange("lenpt", this.lenpt, this.lenpt= lenpt);
	}
	
	public int[][] getLenPt(){
		return lenpt;
	}
	
	public void setLenPt(int[][] lenpt){
		this.lenpt = lenpt;
	}

	public IDataset getCurrentImage(){
		return currentImage;
	}
	
	public void setCurrentImage(IDataset currentImage){
		firePropertyChange("currentImage", this.currentImage, this.currentImage= currentImage);
		}

	public int getImageNumber() {
		return imageNumber;
	}

	public void setImageNumber(int imageNumber) {
		firePropertyChange("imageNumber", this.imageNumber, this.imageNumber= imageNumber);
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
