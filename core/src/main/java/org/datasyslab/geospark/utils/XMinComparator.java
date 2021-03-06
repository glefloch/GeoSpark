/**
 * FILE: XMinComparator.java
 * PATH: org.datasyslab.geospark.utils.XMinComparator.java
 * Copyright (c) 2015-2017 GeoSpark Development Team
 * All rights reserved.
 */
package org.datasyslab.geospark.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.vividsolutions.jts.geom.Geometry;

// TODO: Auto-generated Javadoc
/**
 * The Class XMinComparator.
 */
public class XMinComparator implements Comparator<Object>, Serializable {

	 /* (non-Javadoc)
 	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
 	 */
 	public int compare(Object spatialObject1, Object spatialObject2) {
 		
 		if(((Geometry) spatialObject1).getEnvelopeInternal().getMinX()>((Geometry) spatialObject2).getEnvelopeInternal().getMinX())
 		{
 			return 1;
 		}
 		else if (((Geometry) spatialObject1).getEnvelopeInternal().getMinX()<((Geometry) spatialObject2).getEnvelopeInternal().getMinX())
 		{
 			return -1;
 		}
 		else
 		{
 			return 0;
 		}
 	}
}
