package org.dawnsci.spectrum.ui.wizard;

public class AnalaysisMethodologies {

	public enum Methodology {
		TWOD, TWOD_TRACKING, X ,Y
	}

	public static String toString(Methodology methodology){
		
		switch(methodology){
			case X:
				return "X";
			case Y:
				return "Y";
			case TWOD:
				return "2D";
			case TWOD_TRACKING:
				return "2D Tracking";
		}
		return null;
	}
	
	public enum FitPower {
		ZERO, ONE, TWO, THREE ,FOUR
	}

	public static int toInt(FitPower num){
		
		switch(num){
			case ZERO:
				return 0;
			case ONE:
				return 1;
			case TWO:
				return 2;
			case THREE:
				return 3;
			case FOUR:
				return 4;
		}
		return (Integer) null;
	}
	
	
	
	
	
}
