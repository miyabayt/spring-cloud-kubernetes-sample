package com.bigtreetc.sample.base.messaging

import com.bigtreetc.sample.base.utils.JacksonUtils
import spock.lang.Specification

class MetadataTest extends Specification {

    def "check addSagaId and getSagaId"() {
        when:
        def sagaId = UUID.randomUUID()
        def metadata = Metadata.from(new HashMap<String, Object>()).andSagaId(sagaId)
        then:
        metadata.getSagaId() == sagaId
    }

    def "check json reversible"() {
        when:
        def sagaId = UUID.randomUUID()
        def metadata = Metadata.from(new HashMap<String, Object>()).andSagaId(sagaId)
        def payload = JacksonUtils.writeValueAsString(metadata)
        then:
        def checkMetadata = JacksonUtils.readValue(payload, Metadata.class)
        checkMetadata.getSagaId() == sagaId
    }
}
