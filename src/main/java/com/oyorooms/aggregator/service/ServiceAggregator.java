package com.oyorooms.aggregator.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public interface ServiceAggregator {

    Map<Service, CompletableFuture<ServiceResponse>> aggregate(Set<Service> services, AggregatorRequest request);

    Map<Service, CompletableFuture<ServiceResponse>> aggregate(Set<Service> services, AggregatorRequest request,
                                                               Executor executor);

}
