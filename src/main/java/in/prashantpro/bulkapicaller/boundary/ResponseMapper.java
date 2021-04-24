package in.prashantpro.bulkapicaller.boundary;

public interface ResponseMapper<R> {

  /** map the response. */
  R map(ApiRequest request, String responseBody);
}
