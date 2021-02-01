import com.oyorooms.aggregator.service.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestAggregatorApplication {
    public static void main(String[] args) throws InterruptedException {

        ServiceAggregator aggregator = new DAGWiseServiceAggregator();
        Map<Service, CompletableFuture<ServiceResponse>> futureMap =
                aggregator.aggregate(new HashSet<>(EnumSet.allOf(ExampleServiceName.class)), null, null);
        System.out.println(System.currentTimeMillis() + ":: <" + Thread.currentThread().getName() + ">:: Execution " +
                "has " +
                "already started but not all finished");
        CompletableFuture.allOf(futureMap.values().toArray(new CompletableFuture[0])).join();
        System.out.println(System.currentTimeMillis() + ":: <" + Thread.currentThread().getName() + ">:: Now they are" +
                " finished, moving to second example");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        CompletableFuture.allOf(aggregator.aggregateNow(ExampleServiceName.setOf(ExampleServiceName.G), null,
                executor).values().toArray(new CompletableFuture[0]));

        System.out.println(System.currentTimeMillis() + ":: <" + Thread.currentThread().getName() + ">:: Moving to " +
                "third example of aggregation with shared executor");
        CompletableFuture.allOf(aggregator.aggregate(ExampleServiceName.setOf(ExampleServiceName.C), null,
                executor).values().toArray(new CompletableFuture[0]));
        executor.shutdown();
        System.out.println(System.currentTimeMillis() + ":: <" + Thread.currentThread().getName() + ">:: Tasks " +
                "finished ?:: " + executor.awaitTermination(20L, TimeUnit.SECONDS));
        System.out.println("Some tasks were not yet scheduled and hence executor returned even though there was " +
                "some wait timeout");

        executor = Executors.newFixedThreadPool(2);
        System.out.println("Working with only 2 pool threads for a big task, yet most parallelization is used.");
        final CompletableFuture<Void> voidCompletableFuture =
                CompletableFuture.allOf(aggregator.aggregate(ExampleServiceName.setOf(ExampleServiceName.G), null,
                executor).values().toArray(new CompletableFuture[0]));
        final CompletableFuture<Void> voidCompletableFuture2 =
                CompletableFuture.allOf(aggregator.aggregateNow(ExampleServiceName.setOf(ExampleServiceName.G,
                ExampleServiceName.H), null,
                executor).values().toArray(new CompletableFuture[0]));
        voidCompletableFuture.join();
        voidCompletableFuture2.join();
        executor.shutdown();
    }
}
