import org.example.NBKIApplication;
import org.example.domain.entity.Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = NBKIApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PerformanceTests {

    private static final int RECORDS_TO_CREATE = 100_000;
    private static final int RECORDS_TO_FETCH = 1_000_000;
    private static final int THREAD_COUNT = 100;

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        this.restTemplate = new RestTemplate();
    }

    @Test
    public void testInsert100kRecords() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Callable<Void>> tasks = IntStream.range(0, RECORDS_TO_CREATE)
                .mapToObj(i -> (Callable<Void>) () -> {
                    Record record = new Record();
                    record.setData("Sample data " + i);
                    ResponseEntity<Record> response = restTemplate.postForEntity(
                            "http://localhost:" + port + "/api/v1/records", record, Record.class);
                    assertNotNull(response.getBody());
                    return null;
                }).collect(Collectors.toList());

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

        stopWatch.stop();
        System.out.println("Time to insert 100k records: " + stopWatch.getTotalTimeMillis() + "ms");
    }

    @Test
    public void testConcurrentFetch() throws InterruptedException, ExecutionException {
        testInsert100kRecords();

        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<List<Long>>> futures = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            futures.add(executorService.submit(() -> {
                List<Long> fetchTimes = new ArrayList<>();
                for (int j = 0; j < RECORDS_TO_FETCH / THREAD_COUNT; j++) {
                    long start = System.nanoTime();
                    long randomId = (long) (Math.random() * RECORDS_TO_CREATE) + 1;
                    ResponseEntity<Record> response = restTemplate.getForEntity(
                            "http://localhost:" + port + "/api/v1/records/" + randomId, Record.class);
                    assertNotNull(response.getBody());
                    long duration = Duration.ofNanos(System.nanoTime() - start).toMillis();
                    fetchTimes.add(duration);
                }
                return fetchTimes;
            }));
        }

        List<Long> allFetchTimes = new ArrayList<>();
        for (Future<List<Long>> future : futures) {
            allFetchTimes.addAll(future.get());
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        allFetchTimes.sort(Long::compare);
        long totalTime = allFetchTimes.stream().mapToLong(Long::longValue).sum();
        double medianTime = allFetchTimes.get(allFetchTimes.size() / 2);
        double percentile95 = allFetchTimes.get((int) (allFetchTimes.size() * 0.95));
        double percentile99 = allFetchTimes.get((int) (allFetchTimes.size() * 0.99));

        System.out.println("Total fetch time: " + totalTime + " ms");
        System.out.println("Median fetch time: " + medianTime + " ms");
        System.out.println("95th percentile fetch time: " + percentile95 + " ms");
        System.out.println("99th percentile fetch time: " + percentile99 + " ms");
    }
}
