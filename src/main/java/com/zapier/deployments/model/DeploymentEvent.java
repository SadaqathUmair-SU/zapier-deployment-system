package com.zapier.deployments.model;

import java.time.Instant;

public record DeploymentEvent(
        String id,
        String service,
        DeploymentStatus status,
        int duration,
        Instant timestamp,
        String commitSha
) {
}
