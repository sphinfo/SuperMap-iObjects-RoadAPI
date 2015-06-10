package com.sphinfo.roadapi.util;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.data.GeoLineM;
import com.supermap.data.PointM;

public class GeoJSONUtil {
	
	private static final Logger logger = Logger.getLogger(GeoJSONUtil.class
			.getName().toString());
	
	public JSONObject createPointFeatureJSON(double lon, double lat){
		JSONObject feature = new JSONObject();		
	    try {
	    	feature.put("type", "Feature");
			 //geometry
	        JSONObject point = new JSONObject();
	        point.put("type", "Point");
	        
	        JSONArray coord = new JSONArray("["+lon+","+lat+"]");
	        point.put("coordinates", coord);
	        feature.put("geometry", point);
	 
	       
	    } catch (JSONException e) {
	    	logger.debug("can't save json object: "+e.toString());
	    	return null;
	    }
	    // output the result
	    logger.debug("feature="+feature.toString());
		return feature;
	}
	
	public JSONObject createLineStringFeatureJSON(PointM[] pointMs, double distance){
		
		JSONObject feature = new JSONObject();		
	    try {
	    	feature.put("type", "Feature");
	    	//distance
	    	JSONObject distObj = new JSONObject();
	    	distObj.put("distance", distance);
	    	
	    	feature.put("properties", distObj);
			 //geometry
	        JSONObject lineString = new JSONObject();
	        lineString.put("type", "LineString");
	        JSONArray coords = new JSONArray();
	        
	        for(int i = 0 ; i < pointMs.length  ; i++){	        		        	
	        	JSONArray coord = new JSONArray("["+pointMs[i].getX()+","+pointMs[i].getY()+"]");
	        	coords.put(coord);
	        }
	        
	        lineString.put("coordinates", coords);
	        feature.put("geometry", lineString);
	 
	       
	    } catch (JSONException e) {
	    	logger.debug("can't save json object: "+e.toString());
	    	return null;
	    }
	    // output the result
	    logger.debug("fature="+feature.toString());
		return feature;
		
	}
	
	public JSONObject createFeatureCollectionJSON(JSONArray featureList){
		JSONObject featureCollection = new JSONObject();
	    try {
	        featureCollection.put("type", "FeatureCollection");
	        
            featureCollection.put("features", featureList);
	 
	       
	    } catch (JSONException e) {
	    	logger.debug("can't save json object: "+e.toString());
	    	return null;
	    }
	    // output the result
	    logger.debug("featureCollection="+featureCollection.toString());
		return featureCollection;
	}

}
