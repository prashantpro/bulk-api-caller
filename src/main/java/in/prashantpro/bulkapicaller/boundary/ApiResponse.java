package in.prashantpro.bulkapicaller.boundary;

import java.util.List;
import java.util.Map;

public class ApiResponse<T> {

  private int statusCode;
  private Map<String, List<String>> responseHeaders;
  private String responseBodyString;
  private T responseBody;

  public ApiResponse(int statusCode, String responseBodyString) {
    this.statusCode = statusCode;
    this.responseBodyString = responseBodyString;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public Map<String, List<String>> getResponseHeaders() {
    return responseHeaders;
  }

  public void setResponseHeaders(Map<String, List<String>> responseHeaders) {
    this.responseHeaders = responseHeaders;
  }

  public String getResponseBodyString() {
    return responseBodyString;
  }

  public void setResponseBodyString(String responseBody) {
    this.responseBodyString = responseBodyString;
  }

  public T getResponseBody() {
    return responseBody;
  }

  public void setResponseBody(T responseBody) {
    this.responseBody = responseBody;
  }

  @Override
  public String toString() {
    return "ApiResponse{"
        + "statusCode="
        + statusCode
        + ", responseHeaders="
        + responseHeaders
        + ", responseBodyString='"
        + responseBodyString
        + '\''
        + ", responseBody='"
        + responseBody
        + '\''
        + '}';
  }
}
