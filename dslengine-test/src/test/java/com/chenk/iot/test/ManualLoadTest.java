package com.chenk.iot.test;

import com.chenk.iot.core.context.Key;
import com.chenk.iot.core.context.TypedContext;
import com.chenk.iot.core.engine.RuleEngine;
import com.chenk.iot.core.event.IotEvent;
import com.chenk.iot.core.node.GenericEventNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 手工压测：800 个亮度事件，验证规则过滤、绑定、条件与动作序列。
 */
public class ManualLoadTest {

    private static final Logger log = LoggerFactory.getLogger(ManualLoadTest.class);

    private static final int EVENT_COUNT = 800;
    private static final String DEVICE_ID = "light-001";
    private static final String EVENT_TYPE = "brightnessReport";
    private static final int BRIGHTNESS_MIN = 400;
    private static final int BRIGHTNESS_MAX = 700;
    private static final int THREAD_POOL_SIZE = 32;

    private static final String RULE_JSON =
            "{"
                    + "\"id\":\"rule-brightness\","
                    + "\"startNodeId\":\"event-1\","
                    + "\"nodes\":["
                    + "  {"
                    + "    \"id\":\"event-1\","
                    + "    \"type\":\"genericEvent\","
                    + "    \"config\":{"
                    + "      \"eventFilter\":{"
                    + "        \"eventType\":\"brightnessReport\","
                    + "        \"deviceIdPattern\":\"light-001\""
                    + "      },"
                    + "      \"bindings\":["
                    + "        {\"from\":\"event.data.brightness\",\"to\":\"brightness\"}"
                    + "      ],"
                    + "      \"condition\":\"brightness > 500\","
                    + "      \"actionSequence\":["
                    + "        {"
                    + "          \"type\":\"mqtt\","
                    + "          \"config\":{"
                    + "            \"topic\":\"device/light-001/control\","
                    + "            \"payload\":\"brightness=${brightness}\","
                    + "            \"qos\":1"
                    + "          }"
                    + "        },"
                    + "        {"
                    + "          \"type\":\"log\","
                    + "          \"config\":{"
                    + "            \"message\":\"Brightness high: ${brightness}\""
                    + "          }"
                    + "        }"
                    + "      ]"
                    + "    }"
                    + "  }"
                    + "]"
                    + "}";

    private static final Random RANDOM = new Random();

    public static void main(String[] args) throws IOException, InterruptedException {
        RuleEngine ruleEngine = new RuleEngine();
        ruleEngine.registerRule(RULE_JSON);

        String startNodeId = ruleEngine.getStartNodeId();
        Map<String, Object> nodeInstances = ruleEngine.getNodeInstances();
        Object startNode = nodeInstances.get(startNodeId);
        if (!(startNode instanceof GenericEventNode)) {
            throw new IllegalStateException("Start node is not GenericEventNode: " + startNodeId);
        }
        final GenericEventNode eventNode = (GenericEventNode) startNode;

        final CountDownLatch latch = new CountDownLatch(EVENT_COUNT);
        final AtomicLong totalLatencyMicros = new AtomicLong(0L);

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        long benchmarkStartNanos = System.nanoTime();

        for (int i = 0; i < EVENT_COUNT; i++) {
            final int brightness = BRIGHTNESS_MIN + RANDOM.nextInt(BRIGHTNESS_MAX - BRIGHTNESS_MIN + 1);
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    long eventStartNanos = System.nanoTime();
                    try {
                        TypedContext eventData = new TypedContext();
                        eventData.set(Key.of("brightness", Integer.class), Integer.valueOf(brightness));
                        IotEvent     event          = new IotEvent(DEVICE_ID, EVENT_TYPE, eventData);
                        TypedContext runtimeContext = new TypedContext();
                        eventNode.onEvent(event, runtimeContext);
                    } finally {
                        long eventEndNanos = System.nanoTime();
                        long latencyMicros = (eventEndNanos - eventStartNanos) / 1000L;
                        totalLatencyMicros.addAndGet(latencyMicros);
                        latch.countDown();
                    }
                }
            });
        }

        latch.await();
        long benchmarkEndNanos = System.nanoTime();
        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        long totalMillis = (benchmarkEndNanos - benchmarkStartNanos) / 1_000_000L;
        double totalSeconds = totalMillis / 1000.0;
        double tps = totalSeconds > 0 ? EVENT_COUNT / totalSeconds : 0.0;
        long avgLatencyMicros = totalLatencyMicros.get() / EVENT_COUNT;

        log.info("========== Manual Load Test Result ==========");
        log.info("Events: {}", Integer.valueOf(EVENT_COUNT));
        log.info("Device: {}, brightness range: {}-{}", DEVICE_ID,
                Integer.valueOf(BRIGHTNESS_MIN), Integer.valueOf(BRIGHTNESS_MAX));
        log.info("Total time: {} ms", Long.valueOf(totalMillis));
        log.info("TPS: {}", Double.valueOf(tps));
        log.info("Average latency: {} us", Long.valueOf(avgLatencyMicros));
        log.info("=============================================");
    }
}
