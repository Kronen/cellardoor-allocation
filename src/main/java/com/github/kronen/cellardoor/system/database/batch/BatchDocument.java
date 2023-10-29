package com.github.kronen.cellardoor.system.database.batch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("batches")
public class BatchDocument {

    @Id
    private ObjectId id;

    @Indexed(unique = true)
    private String reference;

    private String sku;

    @Field("purchased_quantity")
    private Integer purchasedQuantity;

    private OffsetDateTime eta;

}
