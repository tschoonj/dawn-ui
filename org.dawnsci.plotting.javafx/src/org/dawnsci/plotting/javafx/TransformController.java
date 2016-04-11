package org.dawnsci.plotting.javafx;

import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class TransformController 
{
	
	private Scale scale;
	private Translate translate;
	private Rotate rotate;
	
	
	public TransformController()
	{
		scale = new Scale();
		translate = new Translate();
		rotate = new Rotate();
	}
	
	
	
	

	public Scale getScale() {
		return scale;
	}

	public Translate getTranslate() {
		return translate;
	}

	public Rotate getRotate() {
		return rotate;
	}
	
	
	
	
	
}
