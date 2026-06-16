package com.cogna.core.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Component
public class CicloClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public CicloClient(RestTemplate restTemplate, @Value("${ciclo.api-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public CicloResponse getCiclo(Integer cicloId) {
        try {
            String url = String.format("%s/api/ciclos/%d", baseUrl, cicloId);
            ResponseEntity<CicloResponse> resp = restTemplate.getForEntity(url, CicloResponse.class);
            return resp.getBody();
        } catch (RestClientException ex) {
            return null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CicloResponse {
        private Integer id;
        private boolean ativo;
        @JsonProperty("dataInicioCaptura")
        private String dataInicioCaptura;
        @JsonProperty("dataFimCaptura")
        private String dataFimCaptura;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
        public String getDataInicioCaptura() { return dataInicioCaptura; }
        public void setDataInicioCaptura(String dataInicioCaptura) { this.dataInicioCaptura = dataInicioCaptura; }
        public String getDataFimCaptura() { return dataFimCaptura; }
        public void setDataFimCaptura(String dataFimCaptura) { this.dataFimCaptura = dataFimCaptura; }

        public boolean isVigente(LocalDate today) {
            try {
                LocalDate inicio = LocalDate.parse(dataInicioCaptura);
                LocalDate fim = LocalDate.parse(dataFimCaptura);
                return ativo && ( !today.isBefore(inicio) && today.isBefore(fim) );
            } catch (Exception e) {
                return false;
            }
        }
    }
}
