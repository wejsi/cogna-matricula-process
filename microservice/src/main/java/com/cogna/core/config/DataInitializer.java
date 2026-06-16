package com.cogna.core.config;

import com.cogna.core.model.Matricula;
import com.cogna.core.model.Turma;
import com.cogna.core.repository.MatriculaRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private final MatriculaRepository repository;

    public DataInitializer(MatriculaRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        if (repository.count() > 0) return;

        Matricula d1 = new Matricula(
                "ALU-001",
                "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
                "ATIVA",
                new Turma("T2026-001", Arrays.asList("SEGUNDA","QUARTA"), "19:00", "22:30"),
                20261,
                Instant.parse("2026-02-10T08:00:00Z")
        );

        Matricula d2 = new Matricula(
                "ALU-002",
                "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
                "ATIVA",
                new Turma("T2026-001", Arrays.asList("SEGUNDA","QUARTA","SEXTA"), "19:00", "22:30"),
                20261,
                Instant.parse("2026-02-12T09:00:00Z")
        );

        Matricula d3 = new Matricula(
                "ALU-003",
                "GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01",
                "CANCELADA",
                new Turma("T2026-001", Arrays.asList("SEGUNDA","QUARTA"), "19:00", "22:30"),
                20261,
                Instant.parse("2026-02-10T10:00:00Z")
        );

        Matricula d4 = new Matricula(
                "ALU-004",
                "GRAD/ADM/BACH/EAD/NOTURNO/UNIT-RJ-02",
                "ATIVA",
                new Turma("T2026-050", Arrays.asList("TERÇA","QUINTA"), "19:00", "22:30"),
                20261,
                Instant.parse("2026-02-11T14:00:00Z")
        );

        List<Matricula> docs = Arrays.asList(d1,d2,d3,d4);
        repository.saveAll(docs);
    }
}
