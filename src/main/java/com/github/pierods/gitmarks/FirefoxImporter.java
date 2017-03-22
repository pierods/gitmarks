package com.github.pierods.gitmarks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by piero on 3/22/17.
 */
public class FirefoxImporter {

  public void importFirefoxJson(String fileURI) {

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer()).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer());

    Gson gson = gsonBuilder.create();

    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader(fileURI));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }

    gson.fromJson(reader, Bookmark.class);
  }

  class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
      Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
      Date date = Date.from(instant);
      return new JsonPrimitive(date.getTime());
    }
  }

  class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
      Instant instant = Instant.ofEpochMilli(jsonElement.getAsJsonPrimitive().getAsLong());
      return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
  }
}