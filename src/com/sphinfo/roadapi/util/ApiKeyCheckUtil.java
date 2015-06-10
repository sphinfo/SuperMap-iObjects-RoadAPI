package com.sphinfo.roadapi.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiKeyCheckUtil {
	private static final Logger logger = Logger.getLogger(ApiKeyCheckUtil.class
			.getName().toString());
	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	public boolean isApiKeyChecked(HttpServletRequest req,
			JSONObject resqJsonObj) throws InvalidKeyException,
			SignatureException, NoSuchAlgorithmException, IOException,
			JSONException {

		InputStream is = this.getClass().getClassLoader()
				.getResourceAsStream("road_api.properties");
		Properties prop = new Properties();
		prop.load(is);
		
		//Map parameters = req.getParameterMap();
		

		boolean isChecked = false;
		String hmacKey = "";
		try {
		
			String clientID = req.getParameterValues("client")[0];//resqJsonObj.get("client").toString();
			String apiKey = req.getParameterValues("key")[0];//resqJsonObj.get("key").toString();

			if (clientID.equalsIgnoreCase("public")) {
				hmacKey = prop.getProperty("publicapikey");// calculateRFC2104HMAC("",clientID.toLowerCase());
			}
			// API Key Check
			else {
				logger.info("cors request origin: "
						+ req.getAttribute("cors.request.origin"));
				String corsReqOriginDomain = req
						.getAttribute("cors.request.origin").toString();
				hmacKey = calculateRFC2104HMAC(corsReqOriginDomain,
						clientID.toLowerCase());
			}

			if (hmacKey.equals(apiKey))
				isChecked = true;
			else
				isChecked = false;

			return isChecked;

		} catch (Exception e) {
			logger.debug(e.toString());
			return isChecked;
		}
	}

	public static String calculateRFC2104HMAC(String data, String key)
			throws SignatureException, NoSuchAlgorithmException,
			InvalidKeyException {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(),
				HMAC_SHA1_ALGORITHM);
		Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
		mac.init(signingKey);
		return toHexString(mac.doFinal(data.getBytes()));
	}

	private static String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();

		for (byte b : bytes) {
			formatter.format("%02x", b);
		}

		return formatter.toString();
	}

}
