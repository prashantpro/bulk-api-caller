package in.prashantpro.bulkapicaller.control;

import in.prashantpro.bulkapicaller.boundary.ApiResponse;
import java.util.Map;

public interface RestApiCaller {

  ApiResponse invoke(String url);

  ApiResponse invoke(String url, Map<String, String> requestHeaders);

  <T> ApiResponse invoke(String url, T requestPayload);

  <T> ApiResponse invoke(String url, T requestPayload, Map<String, String> requestHeaders);
}
