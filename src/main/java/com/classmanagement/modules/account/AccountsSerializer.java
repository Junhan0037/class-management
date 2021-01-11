package com.classmanagement.modules.account;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class AccountsSerializer extends JsonSerializer<List<Account>> {

    @Override
    public void serialize(List<Account> accounts, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeFieldName("members");
        jsonGenerator.writeStartArray();

        accounts.forEach(e -> {
            try {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("name", e.getName());
                jsonGenerator.writeEndObject();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        jsonGenerator.writeEndArray();
    }

}