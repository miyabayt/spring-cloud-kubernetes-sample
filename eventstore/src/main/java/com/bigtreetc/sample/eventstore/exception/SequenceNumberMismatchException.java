package com.bigtreetc.sample.eventstore.exception;

import static com.bigtreetc.sample.base.web.BaseWebConst.OPTIMISTIC_LOCKING_FAILURE_ERROR;

import com.bigtreetc.sample.base.utils.MessageUtils;
import java.io.Serial;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SequenceNumberMismatchException extends RuntimeException {

  @Serial private static final long serialVersionUID = -833368502100268040L;

  public SequenceNumberMismatchException() {
    super(MessageUtils.getMessage(OPTIMISTIC_LOCKING_FAILURE_ERROR));
  }

  public SequenceNumberMismatchException(String message) {
    super(message);
  }

  public SequenceNumberMismatchException(int expected, int actual) {
    super(String.format("sequence expected=%d, actual=%d", expected, actual));
  }
}
