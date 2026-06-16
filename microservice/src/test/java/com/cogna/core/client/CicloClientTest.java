package com.cogna.core.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CicloClientTest {

    @Mock
    RestTemplate restTemplate;

    CicloClient client;

    @BeforeEach
    void setUp() {
        client = new CicloClient(restTemplate, "http://base");
    }

    @Test
    void getCiclo_returnsBodyOn200() {
        CicloClient.CicloResponse resp = new CicloClient.CicloResponse();
        resp.setId(20261);
        resp.setAtivo(true);
        resp.setDataInicioCaptura("2026-01-01");
        resp.setDataFimCaptura(LocalDate.now().plusDays(1).toString());

        when(restTemplate.getForEntity("http://base/api/ciclos/20261", CicloClient.CicloResponse.class))
                .thenReturn(ResponseEntity.ok(resp));

        CicloClient.CicloResponse out = client.getCiclo(20261);
        assertNotNull(out);
        assertTrue(out.isAtivo());
    }

    @Test
    void getCiclo_returnsNullOnException() {
        when(restTemplate.getForEntity(anyString(), eq(CicloClient.CicloResponse.class)))
                .thenThrow(new RestClientException("err"));

        assertNull(client.getCiclo(1));
    }
}
