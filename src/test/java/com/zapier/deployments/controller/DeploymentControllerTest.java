package com.zapier.deployments.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DeploymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getDeployments_returnsSeededData() throws Exception {
        mockMvc.perform(get("/deployments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDeployments_filtersByServiceAndStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                        .param("service", "billing-api")
                        .param("status", "failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDeploymentById_returnsItem() throws Exception {
        mockMvc.perform(get("/deployments/deploy_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("deploy_001"));
    }

    @Test
    void getDeployments_withTrailingSlash_returnsOk() throws Exception {
        mockMvc.perform(get("/deployments/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    void getDeploymentById_returnsNotFoundForMissingId() throws Exception {
        mockMvc.perform(get("/deployments/deploy_999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    @Test
    void getDeployments_returnsBadRequestForInvalidStatus() throws Exception {
        mockMvc.perform(get("/deployments")
                        .param("status", "broken"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }

    @Test
    void getMetrics_returnsPerServiceMetricsForTimeRange() throws Exception {
        mockMvc.perform(get("/metrics")
                        .param("time_range", "10000d"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data['billing-api'].frequency").isNumber())
                .andExpect(jsonPath("$.data['billing-api'].p95Duration").isNumber())
                .andExpect(jsonPath("$.data['billing-api'].failureRate").isNumber());
    }

    @Test
    void getMetrics_usesDefaultTimeRangeWhenMissing() throws Exception {
        mockMvc.perform(get("/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isMap());
    }

    @Test
    void getMetrics_returnsBadRequestForInvalidTimeRange() throws Exception {
        mockMvc.perform(get("/metrics")
                        .param("time_range", "abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"));
    }
}
