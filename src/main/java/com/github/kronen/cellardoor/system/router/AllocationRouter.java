package com.github.kronen.cellardoor.system.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AllocationRouter {

    @Bean
    public RouterFunction<ServerResponse> routes(AllocationHandler allocationHandler) {
        return route(POST("/allocate"), allocationHandler::allocate).andRoute(GET("/batch/{batch_reference}"),
                allocationHandler::getBatch);
    }

}
