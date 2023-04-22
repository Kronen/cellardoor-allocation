package com.github.kronen.cellardoor.system.database.batch;

import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("Batch")
public class BatchEntity {

    @Id
    private String reference;
    private String sku;
    private Integer purchasedQuantity;
    private Set<OrderLine> allocations;
    private OffsetDateTime eta;

}
