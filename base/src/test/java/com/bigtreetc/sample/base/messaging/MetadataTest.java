package com.bigtreetc.sample.base.messaging;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bigtreetc.sample.base.utils.JacksonUtils;
import java.util.HashMap;
import java.util.UUID;
import lombok.val;
import org.junit.jupiter.api.Test;

public class MetadataTest {

  @Test
  public void test1() {
    val sagaId = UUID.randomUUID();
    val metadata = Metadata.from(new HashMap<>()).andSagaId(sagaId);

    assertThat(metadata.getSagaId()).isEqualTo(sagaId);
  }

  @Test
  public void test2() {
    val sagaId = UUID.randomUUID();
    val metadata = Metadata.from(new HashMap<>()).andSagaId(sagaId);
    val payload = JacksonUtils.writeValueAsString(metadata);
    val checkMetadata = JacksonUtils.readValue(payload, Metadata.class);

    assertThat(checkMetadata.getSagaId()).isEqualTo(sagaId);
  }
}
