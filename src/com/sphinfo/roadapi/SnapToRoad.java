package com.sphinfo.roadapi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sphinfo.roadapi.util.ErrorMsgUtil;
import com.sphinfo.roadapi.util.GeoJSONUtil;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystParameter;
import com.supermap.analyst.networkanalyst.TransportationAnalystResult;
import com.supermap.data.CoordSysTransMethod;
import com.supermap.data.CoordSysTransParameter;
import com.supermap.data.CoordSysTranslator;
import com.supermap.data.GeoCoordSys;
import com.supermap.data.GeoCoordSysType;
import com.supermap.data.GeoLineM;
import com.supermap.data.GeoSpatialRefType;
import com.supermap.data.Point2D;
import com.supermap.data.Point2Ds;
import com.supermap.data.PointM;
import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;

public class SnapToRoad {

	private static final Logger logger = Logger.getLogger(SnapToRoad.class
			.getName().toString());
	private static ErrorMsgUtil errorMsgUtil = new ErrorMsgUtil();

	public static JSONObject execute(String method, JSONObject reqJsonObj,		
			TransportationAnalyst transportationAnalyst, String coordSys)
			throws IOException {
		// TODO Auto-generated method stub
		if (method.equalsIgnoreCase("POST")) {
			try {
				
				JSONArray nodesJsonArray = (JSONArray) reqJsonObj.get("nodes");
				Point2Ds nodePts = new Point2Ds();

				for (int i = 0; i < nodesJsonArray.length(); i++) {
					JSONObject nodeJsonObj = (JSONObject) nodesJsonArray.get(i);
					Point2D nodePt = new Point2D();
					nodePt.setX(nodeJsonObj.getDouble("x"));
					nodePt.setY(nodeJsonObj.getDouble("y"));

					nodePts.add(nodePt);
				}
				// node 수 제한
				if (nodePts.getCount() > 200) {
					String errorType = "LimitNodes";
					return errorMsgUtil.createErrorJSON(errorType);

				}
				TransportationAnalystParameter transportationParam = new TransportationAnalystParameter();

				transportationParam.setPoints(nodePts);
				transportationParam.setRoutesReturn(true);
				TransportationAnalystResult transportationResult = transportationAnalyst
						.findPath(transportationParam, true);

				if (transportationResult.getRoutes().length > 0) {

					GeoLineM geoLineM = transportationResult.getRoutes()[0];
					Double length = 0.0;
					if (coordSys.equalsIgnoreCase("wgs1984")) {
						GeoCoordSys geoCoordSys = new GeoCoordSys(
								GeoCoordSysType.GCS_WGS_1984,
								GeoSpatialRefType.SPATIALREF_EARTH_LONGITUDE_LATITUDE);
						// WGS 1984 Geo Coord System
						PrjCoordSys srcPrjCoordSys = new PrjCoordSys();
						srcPrjCoordSys.setGeoCoordSys(geoCoordSys);
						srcPrjCoordSys
								.setType(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE);
						// Web Mercator Project Coord System
						PrjCoordSys tgPrjCoordSys = new PrjCoordSys(
								PrjCoordSysType.PCS_SPHERE_MERCATOR);

						CoordSysTranslator.convert(geoLineM, srcPrjCoordSys,
								tgPrjCoordSys, new CoordSysTransParameter(),
								CoordSysTransMethod.MTH_GEOCENTRIC_TRANSLATION);
						length = geoLineM.getLength();
					} else
						length = geoLineM.getLength();

					logger.info("length: " + length);

					PointM[] pts = geoLineM.getPart(0).toArray();
					GeoJSONUtil geoJsonUtil = new GeoJSONUtil();
					JSONObject feature = geoJsonUtil
							.createLineStringFeatureJSON(pts,
									geoLineM.getLength());

					return feature;

				} else {
					return errorMsgUtil.createErrorJSON("NoResult");
				}

			} catch (Exception e) {
				logger.debug("Error: " +e.toString());
				return errorMsgUtil.createErrorJSON("ETC", e.toString());
				

			}

		} 
		return null;

	}


}
