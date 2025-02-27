package com.github.kronen.cellardoor.infraestructure.config.mongo;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

public class MongoOffsetDateTimeReader implements Converter<Document, OffsetDateTime> {

  @Override
  public OffsetDateTime convert(final Document document) {
    final Date dateTime = document.getDate(MongoOffsetDateTimeWriter.DATE_FIELD);
    final ZoneOffset offset = ZoneOffset.of(document.getString(MongoOffsetDateTimeWriter.OFFSET_FIELD));
    return OffsetDateTime.ofInstant(dateTime.toInstant(), offset);
  }
}
