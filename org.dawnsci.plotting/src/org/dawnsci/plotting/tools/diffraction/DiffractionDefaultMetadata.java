package org.dawnsci.plotting.tools.diffraction;

import javax.vecmath.Vector3d;


import org.dawnsci.plotting.Activator;
import org.dawnsci.plotting.preference.DiffractionToolConstants;
import org.dawnsci.plotting.preference.detector.DiffractionDetectorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.diffraction.DetectorProperties;
import uk.ac.diamond.scisoft.analysis.diffraction.DiffractionCrystalEnvironment;
import uk.ac.diamond.scisoft.analysis.io.DiffractionMetadata;
import uk.ac.diamond.scisoft.analysis.io.IDiffractionMetadata;

public class DiffractionDefaultMetadata {
	
	private static Logger logger = LoggerFactory.getLogger(DiffractionDefaultMetadata.class);
	
	/**
	 * Static method to produce a Detector properties object populated with persisted values
	 * from the preferences store
	 * 
	 * @param shape
	 *            shape from the AbstractDataset the detector properties are created for
	 *            Used to produce the initial detector origin
	 *            
	 */
	public static DetectorProperties getPersistedDetectorProperties(int[] shape) {
		
		int heightInPixels = shape[0];
		int widthInPixels = shape[1];
		
		// Get the default values from the preference store
		double pixelSizeX  = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.PIXEL_SIZE_X);
		double pixelSizeY  = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.PIXEL_SIZE_Y);
		double distance = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DISTANCE);
		
		//Guess pixel Size from the shape of the image
		double[] pixelXY = DiffractionDetectorHelper.getXYPixelSizeMM(shape);
		
		if (pixelXY != null) {
			pixelSizeX = pixelXY[0];
			pixelSizeY = pixelXY[1];
		}
		
		// Create the detector origin vector based on the above
		double[] detectorOrigin = { (widthInPixels - widthInPixels/2d) * pixelSizeX, (heightInPixels - heightInPixels/2d) * pixelSizeY, distance };
		
		// The rotation of the detector relative to the reference frame - assume no rotation
		double detectorRotationX = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_ROTATION_X);
		double detectorRotationY = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_ROTATION_Y);
		double detectorRotationZ = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_ROTATION_Z);
		
		DetectorProperties detprop =new DetectorProperties(new Vector3d(detectorOrigin), heightInPixels, widthInPixels, 
				pixelSizeX, pixelSizeY, detectorRotationX, detectorRotationY, detectorRotationZ);
		
		double x = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.BEAM_CENTRE_X);
		double y = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.BEAM_CENTRE_Y);
		
		double yaw = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_YAW);
		double pitch = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_PITCH);
		double roll = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.DETECTOR_ROLL);
		
		detprop.setBeamCentreCoords(new double[] {x,y});
		
		detprop.setNormalAnglesInDegrees(yaw, pitch, roll);
		
		detprop.setBeamCentreDistance(distance);
		
		return detprop;
	}

	/**
	 * Static method to produce a DiffractionCrystalEnvironment properties object populated with persisted values
	 * from the preferences store
	 */
	public static DiffractionCrystalEnvironment getPersistedDiffractionCrystalEnvironment() {
		double lambda = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.LAMBDA);
		double startOmega = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.START_OMEGA);
		double rangeOmega = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.RANGE_OMEGA);
		double exposureTime = Activator.getDefault().getPreferenceStore().getDouble(DiffractionToolConstants.EXPOSURE_TIME);
		
		return new DiffractionCrystalEnvironment(lambda, startOmega, rangeOmega, exposureTime);
	}
	
	/**
	 * Static method to produce a Detector properties object populated with default values
	 * from the preferences store
	 * 
	 * @param shape
	 *            shape from the AbstractDataset the detector properties are created for
	 *            Used to produce the initial detector origin
	 *            
	 */
	public static DetectorProperties getDefaultDetectorProperties(int[] shape) {
		
		int heightInPixels = shape[0];
		int widthInPixels = shape[1];
		
		// Get the default values from the preference store
		double pixelSizeX  = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.PIXEL_SIZE_X);
		double pixelSizeY  = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.PIXEL_SIZE_Y);
		double distance = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DISTANCE);
		
		// Create the detector origin vector based on the above
		double[] detectorOrigin = { (widthInPixels - widthInPixels/2d) * pixelSizeX, (heightInPixels - heightInPixels/2d) * pixelSizeY, distance };
		
		// The rotation of the detector relative to the reference frame - assume no rotation
		double detectorRotationX = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_ROTATION_X);
		double detectorRotationY = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_ROTATION_Y);
		double detectorRotationZ = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_ROTATION_Z);

		DetectorProperties detprop =new DetectorProperties(new Vector3d(detectorOrigin), heightInPixels, widthInPixels, 
				pixelSizeX, pixelSizeY, detectorRotationX, detectorRotationY, detectorRotationZ);
		
		
		double x = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.BEAM_CENTRE_X);
		double y = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.BEAM_CENTRE_Y);
		
		double yaw = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_YAW);
		double pitch = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_PITCH);
		double roll = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.DETECTOR_ROLL);
		
		detprop.setBeamCentreCoords(new double[] {x,y});
		
		detprop.setNormalAnglesInDegrees(yaw, pitch, roll);
		
		return detprop;
	}
	
	/**
	 * Static method to produce a DiffractionCrystalEnvironment properties object populated with default values
	 * from the preferences store
	 */
	public static DiffractionCrystalEnvironment getDefaultDiffractionCrystalEnvironment() {
		double lambda = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.LAMBDA);
		double startOmega = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.START_OMEGA);
		double rangeOmega = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.RANGE_OMEGA);
		double exposureTime = Activator.getDefault().getPreferenceStore().getDefaultDouble(DiffractionToolConstants.EXPOSURE_TIME);
		
		return new DiffractionCrystalEnvironment(lambda, startOmega, rangeOmega, exposureTime);
	}
	
	/**
	 * Static method to set the default DiffractionCrystalEnvironment values in the 
	 * from the preferences store
	 */
	public static void setPersistedDiffractionCrystalEnvironmentValues(DiffractionCrystalEnvironment dce){
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.LAMBDA, dce.getWavelength());
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.START_OMEGA, dce.getPhiStart());
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.RANGE_OMEGA, dce.getPhiRange());
	}
	
	/**
	 * Static method to set the default DetectorProperties values in the 
	 * from the preferences store
	 */
	public static void setPersistedDetectorPropertieValues(DetectorProperties detprop) {
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.PIXEL_SIZE_X, detprop.getVPxSize());
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.PIXEL_SIZE_Y, detprop.getHPxSize());
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.DISTANCE, detprop.getBeamCentreDistance());
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.BEAM_CENTRE_X, detprop.getBeamCentreCoords()[0]);
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.BEAM_CENTRE_Y, detprop.getBeamCentreCoords()[1]);
		double[] normalAngles = detprop.getNormalAnglesInDegrees();
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.DETECTOR_YAW,normalAngles[0]);
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.DETECTOR_PITCH,normalAngles[1]);
		Activator.getDefault().getPreferenceStore().setValue(DiffractionToolConstants.DETECTOR_ROLL,normalAngles[2]);
		
		
	}
	
	/**
	 * Static method to obtain a DiffractionMetaDataAdapter populated with default values
	 * from the preferences store to act as a starting point for images without metadata
	 */
	public static IDiffractionMetadata getDiffractionMetadata(String filePath, int[] shape) {
		
		final DetectorProperties detprop = getPersistedDetectorProperties(shape);
		final DiffractionCrystalEnvironment diffenv = getPersistedDiffractionCrystalEnvironment();
		
		logger.debug("Meta read from preferences");
		
		return new DiffractionMetadata(filePath, detprop, diffenv);
		
	}
	
	public static IDiffractionMetadata getDiffractionMetadata(int[] shape) {
		
		final DetectorProperties detprop = getPersistedDetectorProperties(shape);
		final DiffractionCrystalEnvironment diffenv = getPersistedDiffractionCrystalEnvironment();
		
		logger.debug("Meta read from preferences");
		
		return new DiffractionMetadata(null, detprop, diffenv);
		
	}
}
