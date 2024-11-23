package com.github.kronen.cellardoor.infraestructure.order.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("orders")
public class OrderDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String reference;
}
