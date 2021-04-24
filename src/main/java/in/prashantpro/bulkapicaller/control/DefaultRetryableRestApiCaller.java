package in.prashantpro.bulkapicaller.control;

import in.prashantpro.bulkapicaller.boundary.ApiResponse;
import in.prashantpro.bulkapicaller.boundary.RetryPolicy;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRetryableRestApiCaller implements RetryableRestApiCaller {

  private static final Logger log = LoggerFactory.getLogger(DefaultRetryableRestApiCaller.class);

  @Override
  public ApiResponse invoke(String url, RetryPolicy retryPolicy) {
    return null;
  }

  @Override
  public ApiResponse invoke(
      String url, Map<String, String> requestHeaders, RetryPolicy retryPolicy) {
    return null;
  }

  @Override
  public <T> ApiResponse invoke(String url, String requestPayload, RetryPolicy retryPolicy) {
    return null;
  }

  @Override
  public <T> ApiResponse invoke(
      String url,
      String requestPayload,
      Map<String, String> requestHeaders,
      RetryPolicy retryPolicy) {

    HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).build();
    Builder requestBuilder = HttpRequest.newBuilder(URI.create(url));
    requestBuilder.setHeader("Content-Type", "application/json");
    requestHeaders.forEach(requestBuilder::setHeader);

    final HttpRequest request =
        requestBuilder
            .timeout(Duration.ofSeconds(10))
            .POST(BodyPublishers.ofString(requestPayload))
            .build();
    log.debug("Request body {}", requestPayload);
    try {
      HttpResponse<String> httpResponse = client.send(request, BodyHandlers.ofString());
      ApiResponse apiResponse = new ApiResponse(httpResponse.statusCode(), httpResponse.body());
      apiResponse.setResponseHeaders(httpResponse.headers().map());
      log.debug("Response Status {} body {}", httpResponse.statusCode(), httpResponse.body());

      return apiResponse;

    } catch (IOException | InterruptedException ex) {
      log.error("Error executing request ", ex);
    }

    return null;
  }
}
