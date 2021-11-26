package com.github.kronen.cellardoor.boot.local;

import org.springframework.boot.SpringApplication;

import com.github.kronen.cellardoor.Application;

public class LocalDevApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(LocalDevTestcontainersConfig.class)
                .run(args);
    }
}
