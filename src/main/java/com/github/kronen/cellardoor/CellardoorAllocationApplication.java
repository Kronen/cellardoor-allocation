package com.github.kronen.cellardoor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication
@EnableR2dbcRepositories
public class CellardoorAllocationApplication {

    public static void main(String[] args) {
        SpringApplication.run(CellardoorAllocationApplication.class, args);
    }

}
