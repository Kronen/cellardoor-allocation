package com.github.kronen.cellardoor.system.database.orderline;

import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderLineMapper {

    OrderLine orderLineEntityToOrderLine(OrderLineEntity orderLine);

    OrderLineEntity orderLineToOrderLineEntity(OrderLine orderLine);

}
