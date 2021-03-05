package in.prashantpro.bulkapicaller.control;

import in.prashantpro.bulkapicaller.boundary.ApiResponse;
import in.prashantpro.bulkapicaller.boundary.RetryPolicy;
import java.util.Map;

public interface RetryableRestApiCaller {

  ApiResponse invoke(String url, RetryPolicy retryPolicy);

  ApiResponse invoke(String url, Map<String, String> requestHeaders, RetryPolicy retryPolicy);

  <T> ApiResponse invoke(String url, String requestPayload, RetryPolicy retryPolicy);

  <T> ApiResponse invoke(String url, String requestPayload, Map<String, String> requestHeaders,
      RetryPolicy retryPolicy);
}
