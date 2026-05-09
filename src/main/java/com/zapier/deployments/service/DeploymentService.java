package com.zapier.deployments.service;

import com.zapier.deployments.api.ServiceMetrics;
import com.zapier.deployments.exception.DeploymentNotFoundException;
import com.zapier.deployments.exception.InvalidTimeRangeException;
import com.zapier.deployments.model.DeploymentEvent;
import com.zapier.deployments.model.DeploymentStatus;
import com.zapier.deployments.repository.DeploymentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DeploymentService {
    private static final Pattern TIME_RANGE_PATTERN = Pattern.compile("^(\\d+)([dh])$");
    private static final String DEFAULT_TIME_RANGE = "7d";
    private final DeploymentRepository repository;

    public DeploymentService(DeploymentRepository repository) {
        this.repository = repository;
    }

    public List<DeploymentEvent> list(Optional<String> service, Optional<DeploymentStatus> status) {
        return repository.findAll().stream()
                .filter(event -> service
                        .map(value -> event.service().equalsIgnoreCase(value))
                        .orElse(true))
                .filter(event -> status
                        .map(value -> event.status() == value)
                        .orElse(true))
                .toList();
    }

    public DeploymentEvent getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new DeploymentNotFoundException(id));
    }

    public Map<String, ServiceMetrics> getMetricsByService(Optional<String> timeRange) {
        String resolvedTimeRange = timeRange
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .orElse(DEFAULT_TIME_RANGE);

        Instant cutoff = resolveCutoff(resolvedTimeRange);
        Map<String, List<DeploymentEvent>> groupedEvents = repository.findAll().stream()
                .filter(event -> !event.timestamp().isBefore(cutoff))
                .collect(Collectors.groupingBy(DeploymentEvent::service));

        return groupedEvents.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> toServiceMetrics(entry.getValue()),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }

    private ServiceMetrics toServiceMetrics(List<DeploymentEvent> events) {
        int frequency = events.size();

        List<Integer> durations = events.stream()
                .map(DeploymentEvent::duration)
                .sorted()
                .toList();
        int p95Duration = percentileNearestRank(durations, 0.95);

        long failedCount = events.stream()
                .filter(event -> event.status() == DeploymentStatus.FAILED)
                .count();
        long completedCount = events.stream()
                .filter(event -> event.status() == DeploymentStatus.SUCCESS || event.status() == DeploymentStatus.FAILED)
                .count();

        double failureRate = completedCount == 0 ? 0.0 : (double) failedCount / completedCount;

        return new ServiceMetrics(frequency, p95Duration, failureRate);
    }

    private int percentileNearestRank(List<Integer> sortedValues, double percentile) {
        if (sortedValues.isEmpty()) {
            return 0;
        }
        int rank = (int) Math.ceil(percentile * sortedValues.size());
        int index = Math.max(0, Math.min(rank - 1, sortedValues.size() - 1));
        return sortedValues.get(index);
    }

    private Instant resolveCutoff(String timeRange) {
        Matcher matcher = TIME_RANGE_PATTERN.matcher(timeRange.toLowerCase());
        if (!matcher.matches()) {
            throw new InvalidTimeRangeException(timeRange);
        }

        long value = Long.parseLong(matcher.group(1));
        if (value <= 0) {
            throw new InvalidTimeRangeException(timeRange);
        }

        String unit = matcher.group(2);
        Instant now = Instant.now();
        return switch (unit) {
            case "d" -> now.minus(value, ChronoUnit.DAYS);
            case "h" -> now.minus(value, ChronoUnit.HOURS);
            default -> throw new InvalidTimeRangeException(timeRange);
        };
    }
}
