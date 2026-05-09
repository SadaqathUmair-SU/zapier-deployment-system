package com.zapier.deployments.controller;

import com.zapier.deployments.api.ApiResponse;
import com.zapier.deployments.api.ServiceMetrics;
import com.zapier.deployments.service.DeploymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/metrics")
public class MetricsController {
    private final DeploymentService service;

    public MetricsController(DeploymentService service) {
        this.service = service;
    }

    @GetMapping({"", "/"})
    public ApiResponse<Map<String, ServiceMetrics>> getMetrics(
            @RequestParam(name = "time_range") Optional<String> timeRange
    ) {
        return new ApiResponse<>(service.getMetricsByService(timeRange));
    }
}
