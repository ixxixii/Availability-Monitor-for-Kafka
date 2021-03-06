package com.microsoft.kafkaavailability.threads;

import com.microsoft.kafkaavailability.metrics.MetricNameEncodedFactory;
import com.microsoft.kafkaavailability.properties.AppProperties;
import com.microsoft.kafkaavailability.reporters.ScheduledReporterCollector;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HeartBeatTest {
    static final private long DEFAULT_INTERVAL = 30l;
    static final private long DIFFERENT_INTERVAL = 90l;

    @Mock
    private ScheduledReporterCollector mockScheduledReporterCollector;
    @Mock
    private ScheduledExecutorService mockScheduledExecutorService;
    @Mock
    private AppProperties mockAppProperties;
    @Mock
    private MetricNameEncodedFactory mockMetricNameEncodedFactory;

    private HeartBeat heartBeat;

    @Before
    public void setup() {
        heartBeat = new HeartBeat(mockScheduledReporterCollector, mockAppProperties, mockScheduledExecutorService, mockMetricNameEncodedFactory);
    }

    @Test
    public void start() {
        heartBeat.start();

        verify(mockScheduledExecutorService).scheduleAtFixedRate(any(HeartBeatThread.class), eq(0l), eq(DEFAULT_INTERVAL), eq(TimeUnit.SECONDS));
    }

    @Test
    public void start_OverrideDefault() {
        Whitebox.setInternalState(mockAppProperties, "heartBeatIntervalInSeconds", DIFFERENT_INTERVAL);

        heartBeat = new HeartBeat(mockScheduledReporterCollector, mockAppProperties, mockScheduledExecutorService, mockMetricNameEncodedFactory);
        heartBeat.start();

        verify(mockScheduledExecutorService).scheduleAtFixedRate(any(HeartBeatThread.class), eq(0l), eq(DIFFERENT_INTERVAL), eq(TimeUnit.SECONDS));
    }

    @Test
    public void stop() {
        heartBeat.stop();
        verify(mockScheduledExecutorService).shutdownNow();
    }

}