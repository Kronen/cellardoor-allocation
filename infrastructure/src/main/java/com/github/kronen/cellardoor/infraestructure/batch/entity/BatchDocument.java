package com.github.kronen.cellardoor.infraestructure.batch.entity;

import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("batches")
public class BatchDocument {

  @Id private String reference;

  private String sku;

  @Field("purchased_quantity")
  private Integer purchasedQuantity;

  private OffsetDateTime eta;

  private Set<OrderLine> allocations;
}
