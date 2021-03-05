package in.prashantpro.bulkapicaller.boundary;

import java.util.concurrent.TimeUnit;

public class RetryPolicy {

  int retryAttempts = 0;
  int retryInterval = 0;
  TimeUnit retryUnit = TimeUnit.SECONDS;

  public int getRetryAttempts() {
    return retryAttempts;
  }

  public void setRetryAttempts(int retryAttempts) {
    this.retryAttempts = retryAttempts;
  }

  public int getRetryInterval() {
    return retryInterval;
  }

  public void setRetryInterval(int retryInterval) {
    this.retryInterval = retryInterval;
  }

  public TimeUnit getRetryUnit() {
    return retryUnit;
  }

  public void setRetryUnit(TimeUnit retryUnit) {
    this.retryUnit = retryUnit;
  }
}
