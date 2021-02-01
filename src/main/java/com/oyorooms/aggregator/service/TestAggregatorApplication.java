package com.oyorooms.aggregator.service;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestAggregatorApplication {
    public static void main(String[] args) {
        final ExecutorService executor = Executors.newFixedThreadPool(10);
        ServiceAggregator aggregator = new DAGWiseServiceAggregator();
        Map<Service, CompletableFuture<ServiceResponse>> futureMap = aggregator.aggregate(new HashSet<>(EnumSet.allOf(ServiceName.class)));
        CompletableFuture.allOf(futureMap.values().toArray(new CompletableFuture[0])).join();
        System.out.println("end");
        CompletableFuture.allOf(aggregator.aggregate(ServiceName.setOf(ServiceName.G)).values().toArray(new CompletableFuture[0])).join();
    }
}
