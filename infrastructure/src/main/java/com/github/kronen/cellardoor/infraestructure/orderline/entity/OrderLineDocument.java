package com.github.kronen.cellardoor.infraestructure.orderline.entity;

import com.github.kronen.cellardoor.infraestructure.batch.entity.BatchDocument;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("order_lines")
public class OrderLineDocument {

  @Id
  private String id;

  private String sku;

  @NotNull private Integer quantity;

  @Field("order_id")
  private String orderId;

  @DBRef
  private BatchDocument batch;
}
