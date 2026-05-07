package com.zapier.deployments.controller;

import com.zapier.deployments.api.ApiResponse;
import com.zapier.deployments.model.DeploymentEvent;
import com.zapier.deployments.model.DeploymentStatus;
import com.zapier.deployments.service.DeploymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/deployments")
public class DeploymentController {
    private final DeploymentService service;

    public DeploymentController(DeploymentService service) {
        this.service = service;
    }

    @GetMapping({"", "/"})
    public ApiResponse<List<DeploymentEvent>> getDeployments(
            @RequestParam Optional<String> service,
            @RequestParam Optional<DeploymentStatus> status
    ) {
        return new ApiResponse<>(this.service.list(service, status));
    }

    @GetMapping("/{id}")
    public ApiResponse<DeploymentEvent> getDeploymentById(@PathVariable String id) {
        return new ApiResponse<>(service.getById(id));
    }
}
