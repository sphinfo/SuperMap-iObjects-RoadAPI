package com.sphinfo.roadapi.util;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.sphinfo.roadapi.SnapToRoad;
import com.supermap.data.PointM;

public class ErrorMsgUtil {

	private static final Logger logger = Logger.getLogger(ErrorMsgUtil.class
			.getName().toString());

	public JSONObject createErrorJSON(String errorType) {
		JSONObject errorJsonObj = new JSONObject();
		try {

			if (errorType.equalsIgnoreCase("LimitNodes")) {
				errorJsonObj.put("message", "Node 수는 200으로 제한되어 있습니다.");
				errorJsonObj.put("status", "500");
			}
			else if(errorType.equalsIgnoreCase("GetRequest")){
				errorJsonObj.put("message", "GET 요청 방식은 지원하지 않습니다.");
				errorJsonObj.put("status", "500");
			}
			else if(errorType.equalsIgnoreCase("NoResult")){
				errorJsonObj.put("message", "결과가 없습니다.");
				errorJsonObj.put("status", "500");
			}
			else{
				errorJsonObj.put("message", "ETC Error");
				errorJsonObj.put("status", "500");
			}

		} catch (JSONException e) {
			logger.debug("ErrorMsgUtil Error: " + e.toString());
			return null;
		}

		return errorJsonObj;
	}

	public JSONObject createErrorJSON(String errorType, String errorStr) {
		// TODO Auto-generated method stub
		JSONObject errorJsonObj = new JSONObject();
		try {

			if (errorType.equalsIgnoreCase("LimitNodes")) {
				errorJsonObj.put("message", "Node 수는 200으로 제한되어 있습니다. " + errorStr);
				errorJsonObj.put("status", "500");
			}
			else if(errorType.equalsIgnoreCase("GetRequest")){
				errorJsonObj.put("message", "GET 요청 방식은 지원하지 않습니다. " + errorStr);
				errorJsonObj.put("status", "500");
			}
			else if(errorType.equalsIgnoreCase("NoResult")){
				errorJsonObj.put("message", "결과가 없습니다. " + errorStr);
				errorJsonObj.put("status", "500");
			}
			else{
				errorJsonObj.put("message", "ETC Error :" + errorStr);
				errorJsonObj.put("status", "500");
			}

		} catch (JSONException e) {
			logger.debug("ErrorMsgUtil Error: " + e.toString());
			return null;
		}

		return errorJsonObj;
	}
}
