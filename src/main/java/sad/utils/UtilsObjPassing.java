package sad.utils;

import java.io.FileWriter;

import com.hp.hpl.jena.rdf.model.Model;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class UtilsObjPassing {


	private Model mdl;

	// server parameters
	private String endpoint = "";
	private String server = "";
	private String serverStatusCode = "", httpResponse = "";

	// query related variables
	private long totalTime = 0;
	private String executionStartTime = "", executionEndTime = "";
	private String qryName = "";
	private String qryNumber = "";
	private String actualQry = "";
	private String graphName = "";
	private String baseUri = "";
	private String dataSet = "";
	private String datasetInQryConstruct = "";
	private String queryToEvaluate = "";
	int qryAnswer = 0;
	int sols = 0;

	public UtilsObjPassing(Model mdl, String endpoint, String graphName,
			String queryToEvaluate, String datasetInQryConstruct,
			String qryName, String qryNumber, String executionStartTime,
			String executionEndTime, long totalTime, int sols,
			String serverStatusCode, String httpResponse) {

		this.mdl = mdl;
		this.endpoint = endpoint;
		this.graphName = graphName;
		this.datasetInQryConstruct = datasetInQryConstruct;
		this.qryName = qryName;
		this.qryNumber = qryNumber;
		this.executionStartTime = executionStartTime;
		this.executionEndTime = executionEndTime;
		this.totalTime = totalTime;
		this.queryToEvaluate = queryToEvaluate;
		this.sols = sols;
		this.serverStatusCode = serverStatusCode;
		this.httpResponse = httpResponse;
		

	}


	public Model getMdl() {
		return mdl;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getServer() {
		return server;
	}

	public String getServerStatusCode() {
		return serverStatusCode;
	}

	public String getHttpResponse() {
		return httpResponse;
	}

	public long getTotalTime() {
		return totalTime;
	}

	public String getExecutionStartTime() {
		return executionStartTime;
	}

	public String getExecutionEndTime() {
		return executionEndTime;
	}

	public String getQryName() {
		return qryName;
	}

	public String getQryNumber() {
		return qryNumber;
	}

	public String getActualQry() {
		return actualQry;
	}

	public String getGraphName() {
		return graphName;
	}

	public String getBaseUri() {
		return baseUri;
	}

	public String getDataSet() {
		return dataSet;
	}

	public String getDatasetInQryConstruct() {
		return datasetInQryConstruct;
	}

	public String getQueryToEvaluate() {
		return queryToEvaluate;
	}

	public int getQryAnswer() {
		return qryAnswer;
	}

	public int getSols() {
		return sols;
	}

}
