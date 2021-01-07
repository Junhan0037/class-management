package com.classmanagement.modules.oauth2;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class OauthClientDetailsSerializer extends JsonSerializer<OauthClientDetails> {

    @Override
    public void serialize(OauthClientDetails oauthClientDetails, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("REST_API_KEY", oauthClientDetails.getClientId());
        jsonGenerator.writeEndObject();
    }

}
