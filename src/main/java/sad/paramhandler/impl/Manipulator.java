package sad.paramhandler.impl;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.jena.riot.RiotException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.data.modeling.and.creator.MetadataPlusResultsCreator;
import sad.endpoints.provider.EndPointsProvider;
import sad.handler.ParamHandler;
import sad.initializer.LoadConfigurations;
import sad.utils.SADUtils;
import sad.utils.UtilsObjPassing;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class Manipulator implements ParamHandler {

	public static final Logger logger = LoggerFactory.getLogger("Manipulator");

	// set the intial value to endpoints either all or for alive
	private String ALL_ENDPOINTS = "ALL";
	private String LIVE_ENDPOINTS = "LIVE";

	private int total200status = 0;
	private int total404status = 0;
	private int total400status = 0;
	private int total500status = 0;
	private int total502status = 0;
	private int total503status = 0;
	private int totalUnknownHostStatus = 0;
	private int totalTimeoutStatus = 0;
	private int totalConnRefuseStatus = 0;
	private int notRespondingToSelect = 0;
	private int RespondingToSelect = 0;
	private int someOtherReasons = 0;

	private int serverTot = 0;

	// Httpheader values variables
	private String server;
	private String statusResponse, serverStatusCode, httpResponse;
	private List<String> hhtRespLst = new ArrayList<String>();
	boolean isSupport;
	int totalSparql1_1 = 0;
	int totalSparql1_0 = 0;
	int endPointNumber = 0;
	int totalEndpoints = 0;

	// interfaces implemented by the client classes
	LoadConfigurations config;
	EndPointsProvider endpProvider;



	// query related variables
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
	private static final long FIRST_RESULT_TIMEOUT = 60 * 1000;
	private static final long EXECUTION_TIMEOUT = 15 * 60 * 1000;

	private static long totalTime;
	private String executionStartTime, executionEndTime, latency;

	// model
	Model mdl = ModelFactory.createDefaultModel();

	// query measurements
	Map<String, List<Double>> qryMaxMinTimeMap = new HashMap<String, List<Double>>();
	List<Double> totalTimeList = new ArrayList<Double>();

	Map<String, List<Double>> qryMaxMinLatencyMap = new HashMap<String, List<Double>>();
	List<Double> totalLatencyList = new ArrayList<Double>();
	List<String> noTripleAndLatency = new ArrayList<String>();

	Map<String, List<Double>> qryMaxMinTriplesMap = new HashMap<String, List<Double>>();
	List<Double> totalTripleList = new ArrayList<Double>();

	int totalNumberOfTriples = 0;
	int totalExecutionTime = 0;

	/*
	 * Constructor
	 * 
	 * @param the
	 *            configurations such as queries
	 * @param a
	 *            source that fetch endpoints from different sources/stream and
	 *            etc.
	 */

	public Manipulator(LoadConfigurations config, EndPointsProvider endpProvider) {

		this.config = config;
		this.endpProvider = endpProvider;
	}

	
	/**
	 * query over the live endpoints retrieved from different sources (static or
	 * stream)
	 */
	public void queryEndpointsStream() {

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> qryNum = new ArrayList<String>();
		try {
			List<String> alive = new ArrayList<String>();//pickAliveEndpoints(endpProvider.provider());
			alive.addAll(endpProvider.provider());
			Collections.sort(alive);
			logger.info("total {} alive endpoints found", alive.size());
			SADUtils.logEndpoints(new HashSet<String>(alive), "temp");
			JSONArray genreArray = (JSONArray) config.loadConfiguration().get(
					"queriesWithDescription");

			Iterator itr = genreArray.iterator();
			while (itr.hasNext()) {

				JSONObject ob = (JSONObject) itr.next();
				qryNum.add(ob.get("qryLabel").toString());
				qryNum.add(ob.get("qryNumber").toString());

				map.put(ob.get("qry").toString(), qryNum); // put id as key and
															// query as value

				synchronized (this) {
					execute(map, alive);
				}

				map.clear();
				qryNum.clear();// clear the list

			}
		} catch (IOException ioexp) {
			logger.error("failed to load qureis");
		} catch (ConfigurationException ex) {
			logger.error("configuration exception");
		} catch (ParseException e) {
			e.printStackTrace();
		} catch(NullPointerException nullP){
			logger.error("null ppinter exceptoion");
		}catch(Exception e){
			e.printStackTrace();
		}finally {

			printLastLoggings();
			printEndpointsDesc();
		}

	}
	/**
	 * query over endpoints logged in file
	 */
	public void queryLoggedEndpoints(String file) {

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> qryNum = new ArrayList<String>();
		Set<String> fileEndpoints = SADUtils.readEndpointsFromFile(file);
		List<String> alive = new ArrayList<String>();

		alive.addAll((fileEndpoints));
		Collections.sort(alive);
		try {
			JSONArray genreArray = (JSONArray) config.loadConfiguration().get(
					"queriesWithDescription");

			Iterator itr = genreArray.iterator();
			while (itr.hasNext()) {

				JSONObject ob = (JSONObject) itr.next();
				qryNum.add(ob.get("qryLabel").toString());
				qryNum.add(ob.get("qryNumber").toString());

				map.put(ob.get("qry").toString(), qryNum); // put id as key and
															// query as value

				synchronized (this) {
					execute(map, alive);
				}

				map.clear();
				qryNum.clear();// clear the list

			}
		} catch (IOException ioexp) {
			logger.error("failed to load qureis");
		} catch (ConfigurationException ex) {
			logger.error("configuration exception");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			printLastLoggings();

		}

	}

	private void setEndpointAndDataset(String dataset, String endpoint) {

		logger.info("setting static dataset <#dataset>");
		dataSet = dataset;
		datasetInQryConstruct = SADUtils.concatEndpointWithDataset(endpoint,
				dataSet);
		baseUri = SADUtils.replaceEndpointHash(endpoint);
	}

	private void createGraphName(String grName) {

		graphName = grName; //** temp commented out //SADUtils.makeFullUri(grName.trim());

	}

	public void latency() {

		try{
		latency = SADUtils.getExecStartTime();
		logger.info("query #{}. latency start time {} ", qryNumber, latency);
		}catch(ParseException pe){
			logger.info("{}",pe);
		}
	}

	public void startTime(String param) {

		try{
		executionStartTime = SADUtils.getExecStartTime();
		logger.info("query #{}. {} start time {} ", qryNumber, param,
				executionStartTime);
		
		}catch(ParseException pe){
		logger.info("{}",pe);
		}
	}

	public void endTime(String param) {

		try{
		executionEndTime = SADUtils.getExecEndTime();
		logger.info("query #{}. {} end time {} ", qryNumber, param,
				executionEndTime);

		}catch(ParseException pe){
		logger.info("{}",pe);
		}
	}

	public void totalTime(String param) throws ParseException {

		try{
		totalTime = SADUtils.getTotalTime(executionStartTime, executionEndTime);

		if (totalTime < 0) {
			logger.info(
					"query #{}. total query execution time is less than or equal to 0 millisecond",
					qryNumber);
		} else {
			logger.info("query #{}. {} total time in milliseconds {} ",
					qryNumber, param, totalTime);
		}

		}catch(ParseException pe){
		logger.info("{}",pe);
		}
	}

	/*
	 * this function takes the queries and list of endpoints and makes query
	 * execution
	 * 
	 * @param queries
	 * 
	 * @param endpoints
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 * @throws ParseException
	 */

	public void execute(Map<String, List<String>> queries,
			List<String> endpoints) throws UnsupportedEncodingException,
			ParseException {

		String endpoint = "";
		Query query = null;
		QueryExecution qe = null;

		for (Map.Entry<String, List<String>> qry : queries.entrySet()) {

			actualQry = qry.getKey();
			List<String> attributes = new ArrayList<String>();
			attributes = qry.getValue();
			for (int i = 0; i < attributes.size(); i++) {
				switch (i) {

				case 0:
					qryName = attributes.get(0);
					break;
				case 1:
					qryNumber = attributes.get(1);
					break;

				default:
					logger.error("index out of bound for list");

				}

			}
		}

		// initialize all the variables to avoid any cached data
		endPointNumber = 0;totalEndpoints = 0;totalSparql1_1 = 0;totalSparql1_0 = 0;totalNumberOfTriples = 0;
		totalExecutionTime = 0;
		qryMaxMinTimeMap.clear();totalTimeList.clear();qryMaxMinTriplesMap.clear();
		totalTripleList.clear();totalLatencyList.clear();qryMaxMinLatencyMap.clear();
		
		endpoints.remove("");// remove the element if equal to empty string in endpoints list
		
		for (String endp : endpoints) {
			endpoint = pickAliveEndpoints(endp);
			if(endpoint!=""){
			try {
				qryAnswer = 0;sols = 0;totalTime = 0;
				executionStartTime = null;
				executionEndTime = null;
				
				endPointNumber++;
				totalEndpoints++;

				setEndpointAndDataset("dataset", endpoint);
				
				isSupport = isSupportSparql1_1(endpoint);
				
				if (isSupport) {
					logger.info("sparql1.1 is supported by '{}' ", endpoint);
					totalSparql1_1++;
				} else {
					logger.warn("sparql1.1 is not supported by '{}' ", endpoint);
					totalSparql1_0++;
				}

				logger.info("status code {} and http response is {} ",
						serverStatusCode, httpResponse);

				logger.info("query #{}. for targeted endpoint '{}' is: '{}' ",
						qryNumber, endpoint, SADUtils.format(actualQry));
				try {
					Thread.sleep(1000);
					logger.info("one second delay before query execution");
				} catch (InterruptedException e1) {
					logger.error("Interrupted", e1);
				}

				queryToEvaluate = actualQry
						.replace("datasetUri", datasetInQryConstruct)
						// replace the <const> in query with the
						// endpoint+datasetURI
						.replace("endpointUrl", endpoint)
						.replace("baseUri", baseUri);

				startTime("execution");// start time
				// HttpQuery ex= new HttpQuery(endpoint);

				query = QueryFactory.create(queryToEvaluate);
				qe = QueryExecutionFactory.sparqlService(endpoint, query);

			/*	logger.info(
						"query #{}. first_result_timeout is {} and execution_timeout is {}",
						qryNumber, FIRST_RESULT_TIMEOUT, EXECUTION_TIMEOUT);*/
				qe.setTimeout(FIRST_RESULT_TIMEOUT, EXECUTION_TIMEOUT);

				if (query.isConstructType()) {

					latency();// start time
					Iterator<Triple> triples = qe.execConstructTriples();

					while (triples.hasNext()) {
						triples.next();
						//writetoFileTemp(triples.next().toString());
						sols++;
						if (sols == 1) {

							endTime("latency");
						}

					}

					totalTime("latency");
					// latency=totalTime;
					totalLatencyList.add((double) totalTime);
					qryMaxMinLatencyMap.put(qryNumber, totalLatencyList);
					qryAnswer = 1;

					excuteConstruct(qe, endpoint, sols, qryName,
							queryToEvaluate);
				}

				}
		
			catch (QueryExceptionHTTP httpe) {
				sols = 0;
				qryAnswer = 0;
				logger.error(
						"query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				if (!isSupport) {
					logger.error(
							"query #{}. only valid for endpoint support sparql1.1",
							qryNumber);

					if (httpe.getCause() != null) {
						String readTime = httpe.getCause().getMessage();
						if (readTime.equals("Read timed out")) {
							logger.error("query #{}. issued {} ", qryNumber,
									httpe.getCause().getMessage());
							// timeOut = true;
							endTime("execution");// end timefor
							totalTime("execution");// calculated time

							totalTimeList.add((double) totalTime);
							qryMaxMinTimeMap.put(qryNumber, totalTimeList);

						} else {
							logger.error("query #{}. {}", qryNumber, httpe
									.getCause().getMessage());
							endTime("execution");// end time
							totalTime("execution");// calculated time
							totalTimeList.add((double) totalTime);
							qryMaxMinTimeMap.put(qryNumber, totalTimeList);
						}

					} else {
						endTime("execution");// end time
						totalTime("execution");// calculated time
						totalTimeList.add((double) totalTime);
						qryMaxMinTimeMap.put(qryNumber, totalTimeList);
					}

				} else {
					logger.error("query #{}. failed for '{}' ", qryNumber,
							endpoint);

					if (httpe.getCause() != null) {
						String readTime = httpe.getCause().getMessage();
						if (readTime.equals("Read timed out")) {
							logger.error("query #{}. issued ({}) ", qryNumber,
									httpe.getCause().getMessage());
							// timeOut = true;
							endTime("execution");// end time
							totalTime("execution");// calculated time
							totalTimeList.add((double) totalTime);
							qryMaxMinTimeMap.put(qryNumber, totalTimeList);
						} else {
							logger.error("query #{}. {}", qryNumber, httpe
									.getCause().getMessage());
							endTime("execution");// end time
							totalTime("execution");// calculated time
							totalTimeList.add((double) totalTime);
							qryMaxMinTimeMap.put(qryNumber, totalTimeList);
						}
					} else {
						endTime("execution");// end time
						totalTime("execution");// calculated time
						totalTimeList.add((double) totalTime);
						qryMaxMinTimeMap.put(qryNumber, totalTimeList);
					}
				}

			} catch (RiotException rioexp) {
				sols = 0;
				qryAnswer = 0;
				logger.warn("query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				endTime("execution");// end time
				totalTime("execution");// calculated time
				totalTimeList.add((double) totalTime);
				qryMaxMinTimeMap.put(qryNumber, totalTimeList);

				logger.error("Unexpected charater {}", rioexp.getMessage());

			} catch (ClassCastException clcastex) {
				sols = 0;
				qryAnswer = 0;
				logger.error(
						"query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				logger.error("class cast exception {}", clcastex.getCause());
				endTime("execution");// end time
				totalTime("execution");// calculated time
				totalTimeList.add((double) totalTime);
				qryMaxMinTimeMap.put(qryNumber, totalTimeList);

			} catch (NullPointerException nullexp) {
				sols = 0;
				qryAnswer = 0;
				logger.error(
						"query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				logger.warn(
						"query #{}. no triple found and the exception is {}",
						qryNumber, nullexp.getMessage());
				endTime("execution");// end time
				totalTime("execution");// calculated time
				totalTimeList.add((double) totalTime);
				qryMaxMinTimeMap.put(qryNumber, totalTimeList);

			} catch (ParseException e) {
				sols = 0;
				qryAnswer = 0;
				logger.error(
						"query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				logger.error("{}", e.getMessage());
				endTime("execution");// end time
				totalTime("execution");// calculated time
				totalTimeList.add((double) totalTime);
				qryMaxMinTimeMap.put(qryNumber, totalTimeList);

			} catch (Exception e) {
				sols = 0;
				qryAnswer = 0;
				logger.error(
						"query #{}. latency total time in milliseconds {}",
						qryNumber, "NTRNL");
				endTime("execution");// end time
				totalTime("execution");// calculated time
				totalTimeList.add((double) totalTime);
				qryMaxMinTimeMap.put(qryNumber, totalTimeList);
				logger.error("query #{}. failed because of {} for '{}' ",
						qryNumber, e.getMessage(), endpoint);
			} finally {
				totalTripleList.add((double) sols);
				qryMaxMinTriplesMap.put(qryNumber, totalTripleList);
				qe.close();
				if (isSupport) {
					if (qryAnswer == 1) {
						logger.info(
								"query #{}. supports sparql1.1 and returned answer {}",
								qryNumber, qryAnswer);
					} else {
						logger.info(
								"query #{}. supports sparql1.1 but failed to return answer",
								qryNumber);
					}

				} else {
					if (qryAnswer == 1) {
						logger.info(
								"query #{}. doesn't support sparql1.1 and returned answer {}",
								qryNumber, qryAnswer);
					} else {
						logger.info(
								"query #{}. doesn't support sparql1.1 also failed to return answer",
								qryNumber);
					}

				}

				new MetadataPlusResultsCreator(new UtilsObjPassing(mdl, endpoint.trim(),
						graphName.trim(), queryToEvaluate,
						datasetInQryConstruct, qryName.trim(), qryNumber,
						executionStartTime, executionEndTime, totalTime, sols,
						serverStatusCode, httpResponse));
				logger.info("query #{}. total number of solutions {}",
						qryNumber, sols);
				logger.info("query #{}. closing time is {} ", qryNumber,
						SADUtils.getCurrentTime());
				logger.info("query #{}. execution finished", qryNumber);

			}
			mdl.removeAll();

			}else{continue;}	
		} // end of for loop 

		logger.info(
				"...mathematical measurements for query {} after querying all the targeted endpoints",
				qryNumber);
		SADUtils.calculteMean(qryMaxMinTimeMap, "time");
		SADUtils.calculateMedian(qryMaxMinTimeMap, "time");
		SADUtils.calculateStandardDeviation(qryMaxMinTimeMap, "time");

		SADUtils.calculateQryMax(qryMaxMinTimeMap, "time");
		SADUtils.calculateQryMin(qryMaxMinTimeMap, "time");
		SADUtils.calulateTotal(qryMaxMinTimeMap, "time");

		// claculate latency for first triple retrieval

		SADUtils.calculteMean(qryMaxMinLatencyMap, "latency");
		SADUtils.calculateMedian(qryMaxMinLatencyMap, "latency");
		SADUtils.calculateStandardDeviation(qryMaxMinLatencyMap, "latency");

		SADUtils.calculateQryMax(qryMaxMinLatencyMap, "latency");
		SADUtils.calculateQryMin(qryMaxMinLatencyMap, "latency");
		SADUtils.calulateTotal(qryMaxMinLatencyMap, "latency");

		// calculate the triple measurements

		SADUtils.calculteMean(qryMaxMinTriplesMap, "triple");
		SADUtils.calculateMedian(qryMaxMinTriplesMap, "triple");
		SADUtils.calculateStandardDeviation(qryMaxMinTriplesMap, "triple");

		SADUtils.calculateQryMax(qryMaxMinTriplesMap, "triple");
		SADUtils.calculateQryMin(qryMaxMinTriplesMap, "triple");
		SADUtils.calulateTotal(qryMaxMinTriplesMap, "triple");

	}

	
	private void writetoFileTemp(String value){
		try {
			 
			//String content = "This is the content to write into file";
 
			File file = new File("/home/helpdesk/Desktop/Test2.txt");
 
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(value);
			bw.write("\n");
			bw.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void excuteConstruct(QueryExecution qe, String endpoint,
			int numbOfTriples, String qryName, String queryToEvaluate) {

		try {
			mdl = qe.execConstruct();
			endTime("execution");// end time
			totalTime("execution");// calculated time
			totalTimeList.add((double) totalTime);
			qryMaxMinTimeMap.put(qryNumber, totalTimeList);
		} catch (NullPointerException e) {
			logger.error("null exception {}", e);
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	public void printEndpointsDesc() {
		logger.info("...total servers are {} with following descriptions",
				serverTot);
		for (String desc : hhtRespLst) {
			logger.info("{}", desc);
		}
	}

	public String pickAliveEndpoints(String endp)  {

		HttpURLConnection http;
		Map<String, List<String>> header ;
		String aliveEndpoint = "";

		try {

				header = new HashMap<String, List<String>>();
				try {

					http = SADUtils.HTTP_Response(endp);
					hhtRespLst.add(http.getHeaderField("Server"));
					serverTot++;
					header = http.getHeaderFields();
					int statusCode = http.getResponseCode();

					boolean isAlive = isAlive(endp); 
					
					if (statusCode == 200) {

						total200status++;

						if (isAlive) {

							aliveEndpoint=endp;
							RespondingToSelect++;

							for (Map.Entry<String, List<String>> entry : header
									.entrySet()) {

								String key = entry.getKey();
								List<String> value = entry.getValue();
								for (String val : value) {
									
									if (key == null) {
										statusResponse =val;
									} else if (key.equals("Server")) {
										server = val;
									} else {
										serverStatusCode = Integer
												.toString(statusCode);
									}

								}

								httpResponse = "StatusCode=[" + statusResponse
										+ "] & Server=[" + server + "]";

							}

						} else {
							notRespondingToSelect++;
						}
					} else if (statusCode == 400 || statusCode == 404
							|| statusCode == 500 || statusCode == 502
							|| statusCode == 503) {

						logger.error("down and status code: {} is for '{}' ",
								statusCode, endp);

						switch (statusCode) {
						case 400:
							total400status++;
							break;
						case 404:
							total404status++;
							break;
						case 500:
							total500status++;
							break;
						case 502:
							total502status++;
							break;
						case 503:
							total503status++;
							break;
						}
					} else {
						someOtherReasons++;
						logger.error(
								"connection refused for somee other reasons '{}'",
								endp);
					}

				} catch (UnknownHostException e) {

					totalUnknownHostStatus++;

					logger.error("Unknown Host '{}' ", endp);

				} catch (SocketTimeoutException e) {

					totalTimeoutStatus++;

					logger.error("Timeout for '{}' ", endp);

				} catch (IOException e) {

					totalConnRefuseStatus++;

					logger.error("connection refused for '{}'", endp);

				} catch (ConfigurationException e) {
					logger.error("{}",e.getStackTrace().toString());;
				} 

		} catch (NullPointerException nullexp) {
			logger.error("{}", nullexp.getMessage());
		}

		return aliveEndpoint;
	}

	private boolean isAlive(String endpoint) throws IOException, IOException,
			ConfigurationException {

		QueryExecution qryexec = null;
		ResultSet res = null;

		try {
			String aliveQry = config.loadConfiguration().get("endpointAlive")
					.toString();
			qryexec = execQueries(endpoint, aliveQry);
			res = qryexec.execSelect();

		} catch (QueryParseException prsexcpt) {
			logger.error("'{}' failed due to {}", qryexec, prsexcpt);
		} catch (Exception e) {
			logger.error("Request:'{}' failed", qryexec);
		} finally {
			qryexec.close();
		}

		if (res != null) {
			return true;
		} else {
			logger.error("not responding to '{}' ", config.loadConfiguration()
					.get("endpointAlive").toString());
			return false;
		}
	}

	private boolean isSupportSparql1_1(String endpoint) {

		QueryExecution qe = null;
		ResultSet res = null;
		try {
			String support1_1 = config.loadConfiguration()
					.get("isSupportSparql1.1").toString();

			qe = execQueries(endpoint, support1_1);
			qe.setTimeout(FIRST_RESULT_TIMEOUT, EXECUTION_TIMEOUT);
			res = qe.execSelect();
		} catch (QueryParseException prsexcpt) {
			logger.error("{} failed due to {}", qe, prsexcpt);
		} catch (Exception e) {
			;
		} finally {
			qe.close();
		}

		if (res != null) {
			return true;
		} else {
			return false;
		}

	}

	private QueryExecution execQueries(String endpoint, String qry) {

		Query query = QueryFactory.create(qry);
		return QueryExecutionFactory.sparqlService(endpoint, query);

	}
	

	/*
	 * print logging after each query completion over all endpoints
	 */
	public void printLastLoggings() {

		logger.info("...total number of endpoints with 200 status code are {}",
				total200status);
		logger.info("...total number of endpoints with 400 status code are {}",
				total400status);
		logger.info("...total number of endpoints with 404 status code are {}",
				total404status);
		logger.info("...total number of endpoints with 500 status code are {}",
				total500status);
		logger.info("...total number of endpoints with 502 status code are {}",
				total502status);
		logger.info("...total number of endpoints with 503 status code are {}",
				total503status);
		logger.info(
				"...total number of endpoints with Unknow host status are {}",
				totalUnknownHostStatus);
		logger.info("...total number of endpoints with Timeout status are {}",
				totalTimeoutStatus);
		logger.info(
				"...total number of endpoints with Connection refused status are {}",
				totalConnRefuseStatus);
		logger.info(
				"...total number of endpoints with not repsonding for some reasons {}",
				someOtherReasons);
		logger.info(
				"...total number of endpoints with no response to select query {}",
				notRespondingToSelect);
		logger.info(
				"...total number of endpoints with response to select query {}",
				RespondingToSelect);
		logger.info("...total number of endpoints support sparql1.1 {}",
				totalSparql1_1);
		logger.info("...total number of endpoints support sparql1.0 {}",
				totalSparql1_0);

	}


	/**
	 * log all the all endpoints retrieved from different sources
	 */
	public void logAllEndpoints() {

		try {
			SADUtils.logEndpoints(endpProvider.provider(), ALL_ENDPOINTS);
		} catch (ParseException e) {
			logger.error("{}", e.getMessage());
		} catch (NullPointerException nullexp) {
			logger.error("{}", nullexp.getMessage());

		}
	}

	/**
	 * log all the alive all endpoints retrieved from different sources
	 */
	public void logAliveEndpoints() {
		try {
			SADUtils.logEndpoints(endpProvider.provider(), LIVE_ENDPOINTS);
		} catch (ParseException e) {
			logger.error("{}", e.getMessage());
		} catch (NullPointerException nullexp) {
			logger.error("{}", nullexp.getMessage());

		}

	}

	/**
	 * log all the all alive and responding to (select query) endpoints
	 * retrieved from different sources
	 */
	public void logAliveAndRespondingEndpoints() {

		try {
			SADUtils.logEndpoints(endpProvider.provider(), config);
		} catch (ParseException e) {
			logger.error("{}", e.getMessage());
		} catch (NullPointerException nullexp) {
			logger.error("{}", nullexp.getMessage());

		}

	}

}
