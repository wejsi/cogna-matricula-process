package com.cogna.core.service;

import com.cogna.core.client.CicloClient;
import com.cogna.core.dto.TurmaAtualizadaEvent;
import com.cogna.core.model.Matricula;
import com.cogna.core.model.Turma;
import com.cogna.core.repository.MatriculaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessorServiceEqualDiasTest {

    @Mock
    CicloClient cicloClient;

    @Mock
    MatriculaRepository repository;

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    ProcessorService service;
    ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        service = new ProcessorService(mapper, cicloClient, repository, kafkaTemplate);
    }

    @Test
    void whenDiasEqual_noSaveNoPublish() throws Exception {
        TurmaAtualizadaEvent event = new TurmaAtualizadaEvent();
        event.setBusinessKey("BK");
        event.setCicloId(1);
        event.setTurma(new Turma("T1", Arrays.asList("SEGUNDA","QUARTA"), "19:00", "22:00"));

        Matricula m = new Matricula();
        m.setId("1");
        m.setAlunoId("ALU");
        m.setBusinessKey("BK");
        m.setStatus("ATIVA");
        m.setTurma(new Turma("T1", Arrays.asList("SEGUNDA","QUARTA"), "19:00", "22:00"));

        when(cicloClient.getCiclo(1)).thenReturn(new CicloClient.CicloResponse());
        when(repository.findByBusinessKeyAndStatus("BK", "ATIVA")).thenReturn(Arrays.asList(m));

        // make ciclo vigente
        CicloClient.CicloResponse r = new CicloClient.CicloResponse();
        r.setAtivo(true);
        r.setDataInicioCaptura("2020-01-01");
        r.setDataFimCaptura("2100-01-01");
        when(cicloClient.getCiclo(1)).thenReturn(r);

        String msg = mapper.writeValueAsString(event);
        service.consume(msg);

        verify(repository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}
