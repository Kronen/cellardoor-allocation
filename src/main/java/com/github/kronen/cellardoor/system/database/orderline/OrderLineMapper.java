package com.github.kronen.cellardoor.system.database.orderline;

import com.github.kronen.cellardoor.domain.allocation.OrderLine;
import org.mapstruct.Mapper;

@Mapper
public interface OrderLineMapper {

  OrderLine toOrderLine(OrderLineDocument orderLine);

  OrderLineDocument toOrderLineDocument(OrderLine orderLine);

}
