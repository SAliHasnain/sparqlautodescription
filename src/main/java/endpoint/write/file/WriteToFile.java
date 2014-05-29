package endpoint.write.file;

import sad.endpoints.provider.EndPointsProvider;

public interface WriteToFile extends EndPointsProvider {
	public void setSourceFile(String sourceFileName);
	public void createHeaderInCSVFile(String fileName);
	public void writeToCSVFile(String fileName,String endPoint, String status, String timeStamp);
}
