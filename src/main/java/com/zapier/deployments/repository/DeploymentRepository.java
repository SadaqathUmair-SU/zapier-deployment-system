package com.zapier.deployments.repository;

import com.zapier.deployments.model.DeploymentEvent;

import java.util.List;
import java.util.Optional;

public interface DeploymentRepository {
    List<DeploymentEvent> findAll();

    Optional<DeploymentEvent> findById(String id);

    void saveAll(List<DeploymentEvent> events);
}
