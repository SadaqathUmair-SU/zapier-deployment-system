package com.zapier.deployments.repository;

import com.zapier.deployments.model.DeploymentEvent;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryDeploymentRepository implements DeploymentRepository {
    private final Map<String, DeploymentEvent> events = new ConcurrentHashMap<>();

    @Override
    public List<DeploymentEvent> findAll() {
        List<DeploymentEvent> all = new ArrayList<>(events.values());
        all.sort(Comparator.comparing(DeploymentEvent::timestamp).reversed());
        return all;
    }

    @Override
    public Optional<DeploymentEvent> findById(String id) {
        return Optional.ofNullable(events.get(id));
    }

    @Override
    public void saveAll(List<DeploymentEvent> newEvents) {
        newEvents.forEach(event -> events.put(event.id(), event));
    }
}
