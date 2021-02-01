package com.oyorooms.aggregator.service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ServiceAggregator {

    Map<Service, CompletableFuture<ServiceResponse>> aggregate(Set<Service> services);

}
