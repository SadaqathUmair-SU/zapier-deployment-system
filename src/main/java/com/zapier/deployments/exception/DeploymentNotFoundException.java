package com.zapier.deployments.exception;

public class DeploymentNotFoundException extends RuntimeException {
    public DeploymentNotFoundException(String id) {
        super("Deployment not found for id: " + id);
    }
}
