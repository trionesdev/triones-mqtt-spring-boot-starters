package com.moensun.mqttv3.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.mqtt")
public class MqttProperties {
    private Boolean asyncClientEnabled = true;
    private String clientIdPrefix;
    private String clientId;
    private String serverUri;
    private String username;
    private String password;
    private Integer connectionTimeout;
    private Integer keepAliveInterval;
    private Boolean automaticReconnect;
    private Boolean cleanSession = true;
    private Will will;
    private Boolean automaticConnect;

    @Data
    public static class Will {
        private String topic;
        private byte[] payload;
        private Integer qos = 2;
        private Boolean retained = true;
    }

}
