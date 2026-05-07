package com.zapier.deployments.service;

import com.zapier.deployments.exception.DeploymentNotFoundException;
import com.zapier.deployments.model.DeploymentEvent;
import com.zapier.deployments.model.DeploymentStatus;
import com.zapier.deployments.repository.DeploymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeploymentService {
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
}
