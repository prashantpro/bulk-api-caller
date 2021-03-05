package in.prashantpro.bulkapicaller.control;

import in.prashantpro.bulkapicaller.boundary.ApiRequest;
import in.prashantpro.bulkapicaller.boundary.ResponseMapper;

public class StringResponseMapper implements ResponseMapper<String> {

  @Override
  public String map(ApiRequest request, String responseBody) {
    return responseBody;
  }
}
