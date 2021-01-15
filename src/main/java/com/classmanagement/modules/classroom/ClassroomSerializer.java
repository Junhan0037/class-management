package com.classmanagement.modules.classroom;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ClassroomSerializer extends JsonSerializer<Classroom> {

    @Override
    public void serialize(Classroom classroom, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", classroom.getId());
        jsonGenerator.writeStringField("name", classroom.getName());
        jsonGenerator.writeEndObject();
    }

}
