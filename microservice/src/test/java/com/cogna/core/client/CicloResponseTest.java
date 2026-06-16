package com.cogna.core.client;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CicloResponseTest {

    @Test
    void isVigente_checksDatesAndAtivo() {
        CicloClient.CicloResponse r = new CicloClient.CicloResponse();
        r.setAtivo(true);
        r.setDataInicioCaptura(LocalDate.now().minusDays(1).toString());
        r.setDataFimCaptura(LocalDate.now().plusDays(1).toString());

        assertTrue(r.isVigente(LocalDate.now()));

        r.setAtivo(false);
        assertFalse(r.isVigente(LocalDate.now()));
    }
}
