package sad.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sad.initializer.LoadConfigurations;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;

/**
 * 
 * @author qaiser.mehmood@insight-centre.org
 */

public class SADUtils {

	private static final Logger logger = LoggerFactory.getLogger("SADUtils");

	// set the initial value to endpoints either all or for alive
	private static String ALL_ENDPOINTS = "ALL";
	private static String LIVE_ENDPOINTS = "LIVE";

	// write to file
	static FileWriter writer = null;
	// read from file
	static BufferedReader br = null;
	
	
	static QueryExecution qryexec = null;
	static ResultSet res = null;
	static Query query = null;

	

	/*
	 * get current time of the system in GMT 
	 */
	public static String getCurrentTime() throws ParseException {

		Date date = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	
	public static String getExecStartTime() throws ParseException {

		return getCurrentTime();
	}

	public static String getExecEndTime() throws ParseException {

		return getCurrentTime();
	}

	/*
	 * calculate the total time interval for queries
	 */
	public static long getTotalTime(String startTime, String endTime)
			throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		Date date1 = formatter.parse(startTime);
		Date date2 = formatter.parse(endTime);

		return (date2.getTime() - date1.getTime());
	}

	/*
	 * create the directory and files to holds the data
	 * 
	 * @param dirctory
	 *            which directory
	 * @param fileName
	 *            name of the file to create
	 * @return
	 */
	public static FileWriter creteDirAndFile(String dirctory, String fileName) {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		File directory = null;
		directory = new File(System.getProperty("user.home") + File.separator
				+ dirctory);// for creating a directory in user home
		boolean dirFlag = false;
		try {
			if (!directory.exists()) {
				dirFlag = directory.mkdir();
			} else {
				;
			}
			File file = new File(directory.getPath() + File.separator
					+ dateFormat.format(cal.getTime()) + "-"
					+ URLEncoder.encode(fileName, "UTF-8"));
			file.createNewFile();
			fileName = file.getPath();
			writer = new FileWriter(fileName);

		} catch (SecurityException Se) {
			logger.error("Error while creating directory in Java: {}",
					Se.getMessage());
		} catch (UnsupportedEncodingException ex) {
			logger.error("{}", ex.getMessage());
		} catch (IOException ioex) {
			logger.error("{}", ioex.getMessage());
		}

		return writer;
	}

	/*
	 * log the number of endpoints either (all or alive) in user's home in
	 * EndPoints directory
	 * 
	 * @param endpoints
	 *            given endpoints
	 * @param endpCheck
	 *            either all or alive
	 */
	public static void logEndpoints(Set<String> endpoints, String endpCheck) {

		HttpURLConnection http;
		try {

			List<String> endpointsList = new ArrayList<String>();
			endpointsList.addAll(endpoints);
			Collections.sort(endpointsList);

			if (endpCheck.equals(ALL_ENDPOINTS)) {
				writer = creteDirAndFile("Endpoints", ALL_ENDPOINTS);

				for (String endp : endpointsList) {
					writer.write(endp);
					writer.write("\n");
				}
			} else if (endpCheck.equals(LIVE_ENDPOINTS)) {

				writer = creteDirAndFile("Endpoints", LIVE_ENDPOINTS);

				for (String endp : endpointsList) {
					http = HTTP_Response(endp);
					if (http.getResponseCode() == 200) {
						writer.write(endp);
						writer.write("\n");
					} else {
						;
					}
				}
			} else {

				writer = creteDirAndFile("Endpoints", "TEMP");
				for (String endp : endpointsList) {
					writer.write(endp);
					writer.write("\n");
				}
			}
			writer.flush();
			writer.close();

		} catch (NullPointerException nullex) {
			logger.error(" endpoints list threw {}", nullex.getMessage());
		} catch (UnknownHostException une) {
			logger.error("{}", une.getMessage());
		} catch (IOException ioex) {
			logger.error("{}", ioex);
		}

	}

	/*
	 * log the retrieved endpoints into a file
	 */
	public static void logEndpoints(Set<String> endpoints,
			LoadConfigurations config) {

		HttpURLConnection http;
		try {
			List<String> endpointsList = new ArrayList<String>();
			endpointsList.addAll(endpoints);
			Collections.sort(endpointsList);

			writer = creteDirAndFile("Endpoints", "ALIVE_RESPONDING_ENDPOINTS");

			for (String endp : endpointsList) {
				http = HTTP_Response(endp);
				if (http.getResponseCode() == 200) {
					boolean alive = isAlive(endp, config);
					if (alive) {
						writer.write(endp);
						writer.write("\n");
					}
				} else {
					;
				}
			}

			writer.flush();
			writer.close();

		} catch (NullPointerException nullex) {
			logger.error(" endpoints list threw {}", nullex.getMessage());
		} catch (IOException ioex) {
			logger.error("File not found");
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*
 * check the given endpoint is alive or not. Returns true if alive else false 
 * @param endpoint
 * @param config
 * @return boolean
 * @throws IOException
 * @throws IOException
 * @throws ConfigurationException
 */
	public static boolean isAlive(String endpoint, LoadConfigurations config)
			throws IOException, IOException, ConfigurationException {

		try {
			String aliveQry = config.loadConfiguration().get("endpointAlive")
					.toString();
			query = QueryFactory.create(aliveQry);

			qryexec = QueryExecutionFactory.sparqlService(endpoint, query);
			res = qryexec.execSelect();

		} catch (QueryParseException prsexcpt) {
			logger.error("'{}' failed due to {}", qryexec, prsexcpt);
		} catch (Exception e) {
			logger.error("Request:'{}' failed due to {}", qryexec,
					e.getMessage());
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

	/*
	 * 
	 * @param endPoint
	 *            to check the header fields
	 * @return http object
	 * @throws IOException
	 */
	public static HttpURLConnection HTTP_Response(String endPoint)
			throws IOException {

		URL obj = new URL(endPoint);
		HttpURLConnection http = (HttpURLConnection) obj.openConnection();
		http.setConnectTimeout(15 * 2000);
		http.setReadTimeout(15 * 2000);

		return http;
	}

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static Set<String> readEndpointsFromFile(String file) {
		Set<String> fileEndpointSet = new HashSet<String>();

		String line = "";
		try {
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null) {
				if (!line.equals(""))
					fileEndpointSet.add(line.replace("\"", ""));

			}
			logger.info("total endpoints from  {} are {}", file,
					fileEndpointSet.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fileEndpointSet;

	}

	public static String concatEndpointWithDataset(String endPoint,
			String dataSet) {

		String replacedString = "";
		try {
			replacedString = endPoint.concat("#").replace("##", "#")
					.concat(dataSet);
		} catch (NullPointerException nexp) {
			logger.error("Endpoint and Dataset values shouldn't be null");
		}
		return replacedString;
	}

	public static String makeFullUri(String Uri) {

		String fullUri = "";
		try {
			if (Uri != "") {
				fullUri = URLDecoder.decode("http://".concat(Uri), "UTF-8");
			}
		} catch (NullPointerException ne) {
			logger.error("{}", ne.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error("uri decoding error");
		}
		return fullUri;
	}

	public static String replaceEndpointHash(String endPoint) {

		String replacedString = "";
		try {
			replacedString = endPoint.concat("#").replace("##", "#");
		} catch (NullPointerException nexp) {
			logger.error("Endpoint value shouldn't be null");
		}
		return replacedString;
	}

	public static String format(String query) {
		query = query.replaceAll("\n", " ");
		query = query.replaceAll("\t", " ");

		return removeDoubleSpaces(query.trim());

	}

	public static String removeDoubleSpaces(String query) {
		int len = query.length();
		query = query.replaceAll("  ", " ");
		if (len == query.length())
			return query;
		else
			return removeDoubleSpaces(query);
	}

	public static void calculteMean(Map<String, List<Double>> qryMeanTime,
			String toEvaluate) {

		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (Map.Entry<String, List<Double>> minMaxTime : qryMeanTime
				.entrySet()) {
			String qryNumber = minMaxTime.getKey();
			List<Double> times = minMaxTime.getValue();
			Double[] array = times.toArray(new Double[times.size()]);

			for (int i = 0; i < array.length; i++) {
				stats.addValue(array[i]);
			}

			logger.info("query #{}. mean {} {} ", qryNumber, toEvaluate,
					stats.getMean());
		}
	}

	public static void calculateMedian(Map<String, List<Double>> qryMedianTime,
			String toEvaluate) {
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (Map.Entry<String, List<Double>> minMaxTime : qryMedianTime
				.entrySet()) {
			String qryNumber = minMaxTime.getKey();
			List<Double> times = minMaxTime.getValue();

			Double[] array = times.toArray(new Double[times.size()]);

			for (int i = 0; i < array.length; i++) {
				stats.addValue(array[i]);
			}

			logger.info("query #{}. median {} {} ", qryNumber, toEvaluate,
					stats.getPercentile(50));
		}
	}

	public static void calculateStandardDeviation(
			Map<String, List<Double>> qryStandardDeviation, String toEvaluate) {
		DescriptiveStatistics stats = new DescriptiveStatistics();

		for (Map.Entry<String, List<Double>> minMaxTime : qryStandardDeviation
				.entrySet()) {
			String qryNumber = minMaxTime.getKey();
			List<Double> times = minMaxTime.getValue();

			Double[] array = times.toArray(new Double[times.size()]);

			for (int i = 0; i < array.length; i++) {
				stats.addValue(array[i]);
			}

			logger.info("query #{}. standard deviation {}  {} ", qryNumber,
					toEvaluate, stats.getStandardDeviation());
		}
	}

	public static void calculateQryMax(Map<String, List<Double>> qryMax,
			String toEvaluate) {

		for (Map.Entry<String, List<Double>> max : qryMax.entrySet()) {
			String qryNumber = max.getKey();
			List<Double> times = max.getValue();
			logger.info("query #{}. maximum  {} {} ", qryNumber, toEvaluate,
					Collections.max(times));
		}

	}

	public static void calculateQryMin(Map<String, List<Double>> qryMin,
			String toEvaluate) {

		for (Map.Entry<String, List<Double>> min : qryMin.entrySet()) {
			String qryNumber = min.getKey();
			List<Double> times = min.getValue();

			logger.info("query #{}. minimum  {} {} ", qryNumber, toEvaluate,
					Collections.min(times));
		}

	}

	public static void calulateTotal(Map<String, List<Double>> qryTotal,
			String toEvaluate) {

		int total = 0;
		for (Map.Entry<String, List<Double>> tot : qryTotal.entrySet()) {
			String qryNumber = tot.getKey();
			List<Double> val = tot.getValue();
			for (Double calc : val)
				total += calc;
			logger.info("query #{}. total  {} {} ", qryNumber, toEvaluate,
					total);

		}
	}

	public static String queryLanguage(String query) {

		String lang = "";

		if (query.contains("COUNT") || query.contains("BIND")) {
			lang = "SPARQL1.1";
		} else {
			lang = "SPARQL1.1/1.0";

		}

		return lang;
	}

	/*
	 * method to create a header fields in CSV file
	 */
	public void createHeaderInCSVFile(String fileName) {
		try {
			writer = new FileWriter(fileName, false); // clear the file old data
														// before writing
			writer = new FileWriter(fileName, true);

			writer.append("ENDPOINT");
			writer.append(',');
			writer.append("STATUS");
			writer.append(',');
			writer.append("TIME");
			writer.append('\n');
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}
	}

	/*
	 * write to CSV file the data such as endpoint, status, measurement time and
	 * etc.
	 * 
	 * @param fileName
	 */
	public void writeToCSVFile(String fileName, String endPoint, String status,
			String timeStamp) {

		try {
			writer = new FileWriter(fileName, true);
			writer.append(endPoint);
			writer.append(',');
			writer.append(status);
			writer.append(',');

			writer.append(timeStamp);
			writer.append(',');

			writer.append("\n");
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}

	}

}
