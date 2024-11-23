package com.github.kronen.cellardoor.infraestructure.orderline.mapper;

import org.mapstruct.Mapper;

import com.github.kronen.cellardoor.domain.allocation.entity.OrderLine;
import com.github.kronen.cellardoor.infraestructure.orderline.entity.OrderLineDocument;

@Mapper
public interface OrderLineMapper {

    OrderLine toOrderLine(OrderLineDocument orderLine);

    OrderLineDocument toOrderLineDocument(OrderLine orderLine);
}
