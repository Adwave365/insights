package com.adwave365.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by alexboyce on 8/21/16.
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    @Override
    public void serialize(ZonedDateTime dt, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")));
    }
}
