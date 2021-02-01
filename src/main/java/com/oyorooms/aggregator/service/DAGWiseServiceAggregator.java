package com.oyorooms.aggregator.service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Collections.reverse;

public class DAGWiseServiceAggregator implements ServiceAggregator {

    @Override
    public Map<Service, CompletableFuture<ServiceResponse>> aggregate(Set<Service> services) {
        final Map<Service, CompletableFuture<ServiceResponse>> futureMap = new ConcurrentHashMap<>();
        Set<Service> serviceList = getRequiredServices(services);
        Map<Service, List<Service>> successorMap = buildSuccessorMap(serviceList);
        List<Service> dagList = dagList(new ArrayList<>(serviceList), successorMap);
        System.out.println("DAG:: " + dagList);
        for (Service service : dagList) {
            final CompletableFuture<ServiceResponse> serviceFuture = buildServiceFuture(service, futureMap);
            futureMap.put(service, serviceFuture);
        }
        return futureMap;
    }

    private CompletableFuture<ServiceResponse> buildServiceFuture(Service service,
                                                                  Map<Service, CompletableFuture<ServiceResponse>> futureMap) {
        if (service.getPrecursors() == null || service.getPrecursors().isEmpty()) {
            return service.executeAsync(futureMap);
        }
        Set<Service> precursors = service.getPrecursors();
        List<CompletableFuture<ServiceResponse>> precursorFutures = new ArrayList<>();
        for (Service precursor : precursors) {
            if (futureMap.get(precursor) == null) {
                System.out.println("ERROR : precursor" + precursor.getName() + " has not future in map");
            } else {
                precursorFutures.add(futureMap.get(precursor));
            }
        }
        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(precursorFutures.toArray(new CompletableFuture[0]));
        return voidCompletableFuture.thenCompose((x) -> service.executeAsync(futureMap));
    }

    private Map<Service, List<Service>> buildSuccessorMap(Set<Service> serviceList) {
        Map<Service, List<Service>> successorMap = new HashMap<>();
        for (Service service : serviceList) {
            Set<Service> precursors = service.getPrecursors();
            if (precursors == null || precursors.isEmpty()) {
                continue;
            }
            for (Service precursor : precursors) {
                successorMap.putIfAbsent(precursor, new ArrayList<>());
                successorMap.get(precursor).add(service);
            }
        }
        return successorMap;
    }

    private List<Service> getStarters(Set<Service> services) {
        Predicate<Service> noDependency = x -> x.getPrecursors() == null || x.getPrecursors().isEmpty();
        return services.stream().filter(noDependency).collect(Collectors.toList());
    }

    private Set<Service> getRequiredServices(Set<Service> services) {
        Set<Service> resultSet = new HashSet<>();
        if(services == null || services.isEmpty())
            return resultSet;
        for (Service service : services) {
            resultSet.add(service);
            resultSet.addAll(getRequiredServices(service.getPrecursors()));
        }
        return resultSet;
    }

    private List<Service> dagList(List<Service> services, Map<Service, List<Service>> successorMap) {
        List<Service> result = new LinkedList<>();
        Map<Service, Boolean> visited = new HashMap<>();
        for (Service service : services) {
            visited.put(service, Boolean.FALSE);
        }

        for (Service service : services) {
            visit(service, successorMap, visited, result);
        }
        reverse(result);
        return result;
    }

    private void visit(Service service, Map<Service, List<Service>> successorMap, Map<Service, Boolean> visited,
                       List<Service> result) {
        if (visited.get(service)) {
            return;
        }
        if (successorMap.get(service) != null && !successorMap.get(service).isEmpty()) {
            for (Service service1 : successorMap.get(service)) {
                visit(service1, successorMap, visited, result);
            }
        }
        visited.put(service, Boolean.TRUE);
        result.add(service);
    }


}
