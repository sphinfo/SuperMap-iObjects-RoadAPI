package com.sphinfo.roadapi;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.sphinfo.roadapi.util.ApiKeyCheckUtil;
import com.sphinfo.roadapi.util.ErrorMsgUtil;
import com.sphinfo.roadapi.util.ReadWriteUtil;
import com.supermap.analyst.networkanalyst.TransportationAnalyst;
import com.supermap.analyst.networkanalyst.TransportationAnalystSetting;
import com.supermap.analyst.networkanalyst.WeightFieldInfo;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasets;
import com.supermap.data.Datasource;
import com.supermap.data.Workspace;
import com.supermap.data.WorkspaceConnectionInfo;
import com.supermap.data.WorkspaceType;

@SuppressWarnings("serial")
public class RoadAPIServlet extends HttpServlet {

	/**
	 * 
	 */
	private static Workspace m_workspace;
	private static Datasource m_datasource;
	private static Datasets m_datasets;
	private static Dataset m_dataset;
	private static TransportationAnalyst m_transportationAnalyst;
	private static String m_coordSys;
	private static final Logger logger = Logger.getLogger(RoadAPIServlet.class
			.getName().toString());
	private ErrorMsgUtil errorMsgUtil = new ErrorMsgUtil();
	private ApiKeyCheckUtil apiKeyCheckUtil = new ApiKeyCheckUtil();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String errorType = "GetRequest";
		ReadWriteUtil.writeJsonStringResponse(
				errorMsgUtil.createErrorJSON(errorType), resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			// query value
			logger.debug("Get paremeter string: " + req.getQueryString());
			InputStream resqInStream = req.getInputStream();
			String resqStr = URLDecoder
					.decode(ReadWriteUtil.convertStreamToString(resqInStream));
			JSONObject resqJsonObj = new JSONObject(resqStr);
		
			if (apiKeyCheckUtil.isApiKeyChecked(req, resqJsonObj)) {
				//SnapToRoad snapToRoadService = new SnapToRoad();
				JSONObject resultJsonObj = SnapToRoad.execute("POST",
						resqJsonObj, m_transportationAnalyst, m_coordSys);
				ReadWriteUtil.writeJsonStringResponse(resultJsonObj, resp);
			} else {
				String errorType = "ETC";
				ReadWriteUtil.writeJsonStringResponse(errorMsgUtil
						.createErrorJSON(errorType, "Not vaild the API Key"),
						resp);
			}
		} catch (Exception e) {
			String errorType = "ETC";
			ReadWriteUtil.writeJsonStringResponse(
					errorMsgUtil.createErrorJSON(errorType), resp);
		}
		// super.doPost(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		try {
			// properties loading
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("road_api.properties");
			Properties prop = new Properties();
			prop.load(is);
			// file path
			String filePath = prop.getProperty("filepath");

			m_workspace = new Workspace();
			WorkspaceConnectionInfo conInfo = new WorkspaceConnectionInfo(
					filePath + prop.getProperty("smwufilename"));
			conInfo.setType(WorkspaceType.SMWU);
			m_workspace.open(conInfo);

			// Datasource
			m_datasource = m_workspace.getDatasources().get(0);
			m_coordSys = prop.getProperty("coordsys");
			m_dataset = (DatasetVector) m_datasource.getDatasets().get(
					m_coordSys.equalsIgnoreCase("wgs1984") ? "Korea_Road_84"
							: "Korea_Road_wm");

			DatasetVector networkDataset = (DatasetVector) m_dataset;

			// Create an instance of the TransportationAnalystSetting object and
			// set
			// its properties
			TransportationAnalystSetting transportationAnalystSetting = new TransportationAnalystSetting();
			transportationAnalystSetting.setNetworkDataset(networkDataset);
			transportationAnalystSetting.setEdgeIDField("SmID");
			transportationAnalystSetting.setNodeIDField("SmNodeID");
			transportationAnalystSetting.setFNodeIDField("SmFNode");
			transportationAnalystSetting.setTNodeIDField("SmTNode");

			WeightFieldInfo weightInfo = new WeightFieldInfo();
			weightInfo.setFTWeightField("smLength");
			weightInfo.setTFWeightField("smLength");

			transportationAnalystSetting.getWeightFieldInfos().add(weightInfo);

			// Create the TransportationAnalyst object and set its properties
			m_transportationAnalyst = new TransportationAnalyst();
			m_transportationAnalyst
					.setAnalystSetting(transportationAnalystSetting);

			// Load the network model
			m_transportationAnalyst.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String errorType = "ETC";
			errorMsgUtil.createErrorJSON(errorType, e.toString());
			logger.debug(e.toString());
		}

	}

}
