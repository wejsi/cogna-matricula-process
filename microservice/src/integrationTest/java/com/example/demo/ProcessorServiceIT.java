package com.cogna.core;

import com.cogna.core.model.Matricula;
import com.cogna.core.model.Turma;
import com.cogna.core.repository.MatriculaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.util.Arrays;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProcessorServiceIT {

    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.1"));

    @Autowired
    MatriculaRepository repository;

    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    void setup() {
        mongo.start();
        kafka.start();

        // override environment properties via System (Spring picks these up)
        System.setProperty("spring.data.mongodb.uri", mongo.getReplicaSetUrl() + "matriculas");
        System.setProperty("spring.kafka.bootstrap-servers", kafka.getBootstrapServers());
    }

    @AfterAll
    void teardown() {
        kafka.stop();
        mongo.stop();
    }

    @Test
    void integration_flow_updatesMatricula() throws Exception {
        // ensure data initializer populated documents
        await().atMost(10, SECONDS).untilAsserted(() -> assertTrue(repository.count() >= 1));

        // build event that should update ALU-001 (adds SEXTA)
        String event = "{\"businessKey\":\"GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01\",\"turma\":{\"codigo\":\"T2026-001\",\"diasDaSemana\":[\"SEGUNDA\",\"QUARTA\",\"SEXTA\"],\"horarioInicio\":\"19:00\",\"horarioFim\":\"22:30\"},\"cicloId\":20261}";

        kafkaTemplate.send("turma-atualizada", event);

        // wait for consumer to process and repository to update
        await().atMost(15, SECONDS).untilAsserted(() -> {
            Matricula m = repository.findByBusinessKeyAndStatus("GRAD/ENG/BACH/PRESENCIAL/NOTURNO/UNIT-SP-01", "ATIVA").stream()
                    .filter(x -> "ALU-001".equals(x.getAlunoId()))
                    .findFirst().orElse(null);
            assertNotNull(m);
            assertTrue(m.getTurma().getDiasDaSemana().contains("SEXTA"));
        });
    }
}
