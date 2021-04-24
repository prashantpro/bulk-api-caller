package in.prashantpro.bulkapicaller.control;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringResponseMapperTest {

  private final StringResponseMapper mapper = new StringResponseMapper();

  @Test
  void should_return_input_as_response() {
    String RESP_BODY = "response body";

    String mappedResponse = mapper.map(null, RESP_BODY);
    assertEquals(mappedResponse, RESP_BODY);
  }
}
