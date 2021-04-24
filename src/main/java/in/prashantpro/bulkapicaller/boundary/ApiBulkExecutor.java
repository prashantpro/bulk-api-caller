package in.prashantpro.bulkapicaller.boundary;

import in.prashantpro.bulkapicaller.control.DefaultRetryableRestApiCaller;
import in.prashantpro.bulkapicaller.control.RestApiCaller;
import in.prashantpro.bulkapicaller.control.RetryableRestApiCaller;
import in.prashantpro.bulkapicaller.control.StringResponseMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiBulkExecutor {

  private static final Logger log = LoggerFactory.getLogger(ApiBulkExecutor.class);
  // Stats generated as part of processing the requests
  private Map<Integer, Integer> responses = new ConcurrentHashMap<>();

  private RetryPolicy retryPolicy;
  private Map<String, String> headers;
  private List<ApiRequest> requests;
  private ResponseMapper<?> mapper;

  private Duration timeout;
  private int concurrentRequests;
  private Executor executor;
  private RestApiCaller restApiCaller;
  private RetryableRestApiCaller retryableRestApiCaller;
  private Duration timeTaken;

  private ApiBulkExecutor() {}

  public static ApiBulkExecutorBuilder builder() {
    return new ApiBulkExecutorBuilder();
  }

  public List<ApiResponse> execute() {
    Instant start = Instant.now();

    List<CompletableFuture<ApiResponse>> futures = new ArrayList<>();
    for (ApiRequest request : requests) {
      futures.add(invoke(request));
    }

    List<ApiResponse> result =
        futures.stream()
            .map(CompletableFuture::join)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    this.timeTaken = Duration.between(start, Instant.now());
    return result;
  }

  public Map<Integer, Integer> getResponses() {
    return responses;
  }

  public Duration getTimeTaken() {
    return timeTaken;
  }

  private CompletableFuture<ApiResponse> invoke(ApiRequest request) {
    CompletableFuture<ApiResponse> future =
        CompletableFuture.supplyAsync(
                () ->
                    retryableRestApiCaller.invoke(
                        request.getUrl(), request.getRequestBody(), this.headers, this.retryPolicy),
                executor)
            .exceptionally(
                ex -> {
                  log.error("Something went wrong", ex);
                  return null;
                });

    return future.thenApply(
        response -> {
          Integer count = responses.getOrDefault(response.getStatusCode(), 0);
          responses.put(response.getStatusCode(), ++count);

          try {
            if (this.mapper != null) {
              final Object responseBody =
                  this.mapper.map(request, response.getResponseBodyString());
              response.setResponseBody(responseBody);
            }
          } catch (Exception e) {
            log.error("Failed to map response", e);
          }
          return response;
        });
  }

  public static class ApiBulkExecutorBuilder {

    private RetryPolicy retryPolicy;

    private Map<String, String> headers;
    private List<ApiRequest> requests;
    private ResponseMapper<?> mapper;

    private Duration timeout;
    private int concurrentRequests;
    private Executor executor;
    private RetryableRestApiCaller retryableRestApiCaller;

    /**
     * Set the number of concurrent requests allowed to be executed
     *
     * @param concurrentRequests The number of concurrent calls to allow
     * @return ApiBulkExecutorBuilder
     */
    public ApiBulkExecutorBuilder concurrentRequests(int concurrentRequests) {
      this.concurrentRequests = concurrentRequests;
      return this;
    }

    public ApiBulkExecutorBuilder executor(Executor executor) {
      this.executor = executor;
      return this;
    }

    public ApiBulkExecutorBuilder retryAttempts(int retryAttempts) {
      this.retryPolicy = this.retryPolicy != null ? retryPolicy : new RetryPolicy();

      this.retryPolicy.setRetryAttempts(retryAttempts);
      return this;
    }

    public ApiBulkExecutorBuilder retryInterval(int retryInterval, TimeUnit retryUnit) {
      this.retryPolicy = this.retryPolicy != null ? retryPolicy : new RetryPolicy();

      this.retryPolicy.setRetryInterval(retryInterval);
      this.retryPolicy.setRetryUnit(retryUnit);
      return this;
    }

    public ApiBulkExecutorBuilder headers(Map<String, String> headers) {
      this.headers = headers;
      return this;
    }

    public ApiBulkExecutorBuilder requests(List<ApiRequest> requests) {
      this.requests = requests;
      return this;
    }

    public ApiBulkExecutorBuilder responseMapper(ResponseMapper<?> mapper) {
      this.mapper = mapper;
      return this;
    }

    public ApiBulkExecutorBuilder timeout(Duration timeout) {
      this.timeout = timeout;
      return this;
    }

    public ApiBulkExecutor build() {
      ApiBulkExecutor apiBulkExecutor = new ApiBulkExecutor();
      apiBulkExecutor.headers = this.headers != null ? this.headers : new HashMap<>();

      apiBulkExecutor.timeout = this.timeout;

      buildRequests(apiBulkExecutor);
      buildRetryContext(apiBulkExecutor);
      buildRetryableCaller(apiBulkExecutor);
      buildResponseMapper(apiBulkExecutor);
      buildExecutor(apiBulkExecutor);

      return apiBulkExecutor;
    }

    private void buildExecutor(ApiBulkExecutor apiBulkExecutor) {
      if (this.concurrentRequests < 1) {
        this.concurrentRequests = 1;
      }
      if (this.executor == null) {
        throw new IllegalStateException("Executor must be supplied");
      }
      apiBulkExecutor.executor = this.executor;
    }

    private void buildResponseMapper(ApiBulkExecutor apiBulkExecutor) {
      apiBulkExecutor.mapper = this.mapper != null ? this.mapper : new StringResponseMapper();
    }

    private void buildRetryableCaller(ApiBulkExecutor apiBulkExecutor) {
      if (this.retryPolicy != null) {
        this.retryableRestApiCaller = new DefaultRetryableRestApiCaller();
        apiBulkExecutor.retryableRestApiCaller = this.retryableRestApiCaller;
      }
    }

    private void buildRetryContext(ApiBulkExecutor apiBulkExecutor) {
      if (this.retryPolicy != null) {
        if (this.retryPolicy.getRetryAttempts() < 1) {
          throw new IllegalStateException("Retry attempts must be greater than one");
        }
        if (this.retryPolicy.getRetryAttempts() > 10) {
          log.warn("A high retry count may result in slower processing");
        }
        apiBulkExecutor.retryPolicy = this.retryPolicy;
      }
    }

    private void buildRequests(ApiBulkExecutor apiBulkExecutor) {
      if (this.requests == null || this.requests.isEmpty()) {
        throw new IllegalStateException("We need list of ApiRequest to process");
      }
      apiBulkExecutor.requests = this.requests;
    }
  }
}
