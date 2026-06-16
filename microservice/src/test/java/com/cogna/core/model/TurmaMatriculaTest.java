package com.cogna.core.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TurmaMatriculaTest {

    @Test
    void gettersAndSetters_work() {
        Turma t = new Turma("T1", Arrays.asList("SEGUNDA"), "08:00", "10:00");
        assertEquals("T1", t.getCodigo());

        Matricula m = new Matricula("ALU","BK","ATIVA", t, 1, Instant.now());
        assertEquals("ALU", m.getAlunoId());
        assertEquals("BK", m.getBusinessKey());
        assertEquals("ATIVA", m.getStatus());
        assertEquals(t, m.getTurma());
    }
}
