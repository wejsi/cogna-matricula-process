package com.cogna.core.service;

import com.cogna.core.client.CicloClient;
import com.cogna.core.dto.MatriculaAtualizadaEvent;
import com.cogna.core.dto.TurmaAtualizadaEvent;
import com.cogna.core.model.Matricula;
import com.cogna.core.repository.MatriculaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ProcessorService {

    private final Logger log = LoggerFactory.getLogger(ProcessorService.class);
    private final ObjectMapper mapper;
    private final CicloClient cicloClient;
    private final MatriculaRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProcessorService(ObjectMapper mapper, CicloClient cicloClient, MatriculaRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.mapper = mapper;
        this.cicloClient = cicloClient;
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "turma-atualizada", groupId = "matricula-processor")
    public void consume(String message) {
        try {
            TurmaAtualizadaEvent event = mapper.readValue(message, TurmaAtualizadaEvent.class);

            Integer cicloId = event.getCicloId();
            var ciclo = cicloClient.getCiclo(cicloId);
            if (ciclo == null) {
                log.info("Ciclo {} não encontrado ou erro ao consultar - descartando evento", cicloId);
                return;
            }

            LocalDate today = LocalDate.now();
            if (!ciclo.isVigente(today)) {
                log.info("Ciclo {} não vigente - descartando evento", cicloId);
                return;
            }

            List<Matricula> matriculas = repository.findByBusinessKeyAndStatus(event.getBusinessKey(), "ATIVA");
            for (Matricula m : matriculas) {
                var diasAntigos = m.getTurma().getDiasDaSemana();
                var diasNovos = event.getTurma().getDiasDaSemana();
                if (diasAntigos == null || !diasAntigos.equals(diasNovos)) {
                    MatriculaAtualizadaEvent out = new MatriculaAtualizadaEvent();
                    out.setMatriculaId(m.getId());
                    out.setAlunoId(m.getAlunoId());
                    out.setBusinessKey(m.getBusinessKey());
                    out.setCicloId(m.getCicloId());
                    out.setDiasDaSemanaAnterior(diasAntigos);
                    out.setDiasDaSemanaNovo(diasNovos);
                    out.setDataAtualizacao(Instant.now());

                    // atualizar e persistir
                    m.getTurma().setDiasDaSemana(diasNovos);
                    repository.save(m);

                    // publicar evento
                    kafkaTemplate.send("matricula-atualizada", out);
                    log.info("Matricula {} atualizada e evento publicado", m.getId());
                }
            }

        } catch (Exception ex) {
            log.error("Erro processando evento turma-atualizada", ex);
        }
    }
}
