package com.oyorooms.aggregator.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public enum ServiceName implements Service {
    A(Collections.EMPTY_SET),
    B(Collections.singleton(A)),
    C(Collections.singleton(A)),
    D(setOf(A, B, C)),
    E(setOf(D)),
    F(Collections.EMPTY_SET),
    G(setOf(A, B, E)),
    H(setOf(F));


    private final Set<Service> predecessors;

    ServiceName(Set<Service> predecessors) {
        this.predecessors = new HashSet<>(predecessors);
    }

    public static Set<Service> setOf(ServiceName... service) {
        return new HashSet<>(Arrays.asList(service));
    }

    @Override
    public Set<Service> getPrecursors() {
        return new HashSet<>(predecessors);
    }

    @Override
    public CompletableFuture<ServiceResponse> executeAsync(Map<Service, CompletableFuture<ServiceResponse>> futureMap) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ":: Executing " + this.name());
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                System.out.println("Interrupted in sleep");
            }
            return new ApiResponse();
        });
    }

    @Override
    public CompletableFuture<ServiceResponse> executeAsync(Map<Service, CompletableFuture<ServiceResponse>> futureMap, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread().getName() + ":: Executing " + this.name());
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                System.out.println("Interrupted in sleep");
            }
            return new ApiResponse();
        }, executor);
    }

    @Override
    public String getName() {
        return this.name();
    }

    static class ApiResponse implements ServiceResponse {
    }


}
