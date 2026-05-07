package com.zapier.deployments.seed;

import com.zapier.deployments.model.DeploymentEvent;
import com.zapier.deployments.model.DeploymentStatus;
import com.zapier.deployments.repository.DeploymentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
public class DeploymentDataSeeder {
    private static final String[] SERVICES = {
            "billing-api",
            "auth-service",
            "notifications-worker",
            "web-frontend"
    };

    private final DeploymentRepository repository;

    public DeploymentDataSeeder(DeploymentRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seed() {
        if (!repository.findAll().isEmpty()) {
            return;
        }

        Instant baseTime = Instant.parse("2025-04-30T12:00:00Z");
        List<DeploymentEvent> events = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            String id = String.format("deploy_%03d", i);
            String service = SERVICES[(i - 1) % SERVICES.length];
            DeploymentStatus status = computeStatus(i);
            int duration = 120 + ((i * 37) % 480);
            Instant timestamp = baseTime.minus(i * 8L, ChronoUnit.HOURS);
            String commitSha = String.format("%06x", (i * 123457) & 0xFFFFFF);

            events.add(new DeploymentEvent(
                    id,
                    service,
                    status,
                    duration,
                    timestamp,
                    commitSha
            ));
        }

        repository.saveAll(events);
    }

    private DeploymentStatus computeStatus(int i) {
        if (i % 11 == 0) {
            return DeploymentStatus.CANCELLED;
        }
        if (i % 4 == 0) {
            return DeploymentStatus.FAILED;
        }
        return DeploymentStatus.SUCCESS;
    }
}
