package com.edgeapi.service.fastporteiot.application.external.communication;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class RestTemplateConfig {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    /**
     * Configura y proporciona un bean de RestTemplate con un interceptor para logging detallado.
     * 
     * @return una instancia de RestTemplate configurada.
     */
    @Bean
    public RestTemplate restTemplate() {
        // Configura la fábrica de solicitudes HTTP con un tiempo de espera de conexión de 5000 ms
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(5000);

        // Crea una instancia de RestTemplate con la fábrica de solicitudes configurada
        RestTemplate restTemplate = new RestTemplate(factory);

        // Agrega un interceptor para logging detallado de las solicitudes y respuestas HTTP
        restTemplate.setInterceptors(Collections.singletonList((request, body, execution) -> {
            log.debug("Making request to: {} {}", request.getMethod(), request.getURI());
            log.debug("Request headers: {}", request.getHeaders());
            log.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));

            ClientHttpResponse response = execution.execute(request, body);

            log.debug("Got response: {}", response.getStatusCode());
            log.debug("Response headers: {}", response.getHeaders());

            return response;
        }));

        return restTemplate;
    }
}
