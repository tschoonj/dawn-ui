/*
 * Copyright (c) 2012 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 
package org.dawnsci.plotting.services.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.january.dataset.DatasetFactory;
import org.eclipse.january.dataset.DoubleDataset;
import org.eclipse.january.dataset.IDataset;

public class DatasetTitleUtils {

	public static String getTitle(final IDataset xIn, 
			                      final List<IDataset> ysIn, 
			                      final boolean isFileName) {
		return getTitle(xIn,ysIn,isFileName,null);
	}

	@SuppressWarnings("unchecked")
	public static String getTitle(final IDataset xIn, 
			                      final List<? extends IDataset> ysIn, 
			                      final boolean isFileName,
			                      final String  rootName) {
		
		final IDataset       x;
		final List<IDataset> ys;
		if (ysIn==null) {
			ys = new ArrayList<IDataset>(1);
			ys.add(xIn);
			x = DatasetFactory.createRange(DoubleDataset.class, ys.get(0).getSize());
			x.setName("Index of "+xIn.getName());
		} else {
			x  = xIn;
			ys = (List<IDataset>) ysIn;
		}
		
		final StringBuilder buf = new StringBuilder();
		buf.append("Plot of ");
		final Set<String> used = new HashSet<String>(7);
		int i=0;
		int dataSetSize=ys.size();
		for (IDataset dataSet : ys) {
			String name = getName(dataSet,rootName);
			
			if (isFileName && name!=null) {
			    // Strip off file name
				final Matcher matcher = Pattern.compile("(.*) \\(.*\\)").matcher(name);
				if (matcher.matches()) name = matcher.group(1);
			}
			
			if (used.contains(name)) continue;			
			if(i==0) buf.append(name);
			if (ys.size()<2) break;
			if(i==1 && 1==dataSetSize-1) buf.append(","+name);
			if(i==dataSetSize-1 && dataSetSize-1!=1) buf.append("..."+name);
			i++;
		}
		buf.append(" against ");
		buf.append(x.getName());
		return buf.toString();
	}

	/**
	 * 
	 * @param x
	 * @param rootName
	 * @return
	 */
	public static String getName(IDataset x, String rootName) {
		if (x==null) return null;
		try {
		    return rootName!=null
		           ? x.getName().substring(rootName.length())
		           : x.getName();
		} catch (StringIndexOutOfBoundsException ne) {
			return x.getName();
		}
	}

}
