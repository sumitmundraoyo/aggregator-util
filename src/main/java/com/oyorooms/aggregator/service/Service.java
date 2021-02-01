package com.oyorooms.aggregator.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface Service {
    Set<Service> getPrecursors();

    CompletableFuture<ServiceResponse> executeAsync(Map<Service, CompletableFuture<ServiceResponse>> futureMap,
                                                    AggregatorRequest request, Executor executor);

    String getName();

}
