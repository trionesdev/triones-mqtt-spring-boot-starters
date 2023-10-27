package com.moensun.mqttv3.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Configuration
@EnableConfigurationProperties(value = {MqttProperties.class})
public class MqttV3AutoConfiguration {
    private final MqttProperties mqttProperties;

    public MqttV3AutoConfiguration(MqttProperties mqttProperties) {
        this.mqttProperties = mqttProperties;
    }

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions co = new MqttConnectOptions();
        if (Objects.nonNull(mqttProperties)) {
            if (Objects.nonNull(mqttProperties.getUsername())) {
                co.setUserName(mqttProperties.getUsername());
            }
            if (Objects.nonNull(mqttProperties.getPassword())) {
                co.setPassword(mqttProperties.getPassword().toCharArray());
            }
            if (Objects.nonNull(mqttProperties.getConnectionTimeout())) {
                co.setConnectionTimeout(mqttProperties.getConnectionTimeout());
            }
            if (Objects.nonNull(mqttProperties.getKeepAliveInterval())) {
                co.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());
            }
            if (Objects.nonNull(mqttProperties.getAutomaticReconnect())) {
                co.setAutomaticReconnect(mqttProperties.getAutomaticReconnect());
            }
            if (Objects.nonNull(mqttProperties.getCleanSession())) {
                co.setCleanSession(mqttProperties.getCleanSession());
            }
            if (Objects.nonNull(mqttProperties.getWill())) {
                MqttProperties.Will will = mqttProperties.getWill();
                co.setWill(will.getTopic(), will.getPayload(), will.getQos(), will.getRetained());
            }
        }
        return co;
    }


    @Bean
    @ConditionalOnMissingBean(IMqttAsyncClient.class)
    @ConditionalOnProperty(prefix = "spring.mqtt", value = "asyncClientEnabled", havingValue = "true", matchIfMissing = true)
    public IMqttAsyncClient mqttAsyncClient(MqttConnectOptions co) throws MqttException {
        String clientId = clientIdGenerate();
        MqttAsyncClient client = new MqttAsyncClient(mqttProperties.getServerUri(), clientId, new MemoryPersistence());
        if (mqttProperties.getAutomaticConnect()) {
            client.connect(co);
        }
        return client;
    }

    @Bean
    @ConditionalOnMissingBean(IMqttClient.class)
    @ConditionalOnProperty(prefix = "spring.mqtt", value = "asyncClientEnabled", havingValue = "false", matchIfMissing = true)
    public IMqttClient mqttClient(MqttConnectOptions co) throws MqttException {
        String clientId = clientIdGenerate();
        MqttClient client = new MqttClient(mqttProperties.getServerUri(), clientId, new MemoryPersistence());
        if (mqttProperties.getAutomaticConnect()) {
            client.connect(co);
        }
        return client;
    }

    private String clientIdGenerate() {
        return (StringUtils.hasText(mqttProperties.getClientIdPrefix()) ? mqttProperties.getClientIdPrefix() : "") + (StringUtils.hasText(mqttProperties.getClientId()) ? mqttProperties.getClientId() : UUID.randomUUID().toString());
    }

}
