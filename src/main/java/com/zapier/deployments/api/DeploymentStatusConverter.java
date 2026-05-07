package com.zapier.deployments.api;

import com.zapier.deployments.model.DeploymentStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class DeploymentStatusConverter implements Converter<String, DeploymentStatus> {
    @Override
    public DeploymentStatus convert(String source) {
        return DeploymentStatus.valueOf(source.trim().toUpperCase(Locale.ROOT));
    }
}
