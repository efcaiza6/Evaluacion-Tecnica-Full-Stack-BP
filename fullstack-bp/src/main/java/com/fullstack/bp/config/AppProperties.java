package com.fullstack.bp.config;

import java.math.BigDecimal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(BigDecimal limiteRetiroDiario) {
}
