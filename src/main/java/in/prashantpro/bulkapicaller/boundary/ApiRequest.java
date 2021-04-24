package in.prashantpro.bulkapicaller.boundary;

public class ApiRequest {

  private final String url;
  private final HttpRequestMethod method;
  private final String requestBody;
  private String id;
  private Object associatedData;

  public ApiRequest(HttpRequestMethod method, String url, String requestBody) {
    this.method = method;
    this.url = url;
    this.requestBody = requestBody;
  }

  public String getUrl() {
    return url;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Object getAssociatedData() {
    return associatedData;
  }

  public void setAssociatedData(Object associatedData) {
    this.associatedData = associatedData;
  }

  public enum HttpRequestMethod {
    GET,
    POST,
    DELETE
  }
}
