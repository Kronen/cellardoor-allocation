package com.github.kronen.cellardoor.boot.local;

import com.github.kronen.cellardoor.Application;

import org.springframework.boot.SpringApplication;

public class LocalDevApplication {

    public static void main(String[] args) {
        SpringApplication.from(Application::main)
                .with(LocalDevTestcontainersConfig.class)
                .run(args);
    }
}
