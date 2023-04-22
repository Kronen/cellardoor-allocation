package com.github.kronen.cellardoor.system.database.orderline;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import jakarta.validation.constraints.NotNull;
import java.math.BigInteger;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table("OrderLine")
public class OrderLineEntity {

    @Id
    private BigInteger id;
    private String orderId;
    private String sku;
    @NotNull
    private Integer quantity;

}
