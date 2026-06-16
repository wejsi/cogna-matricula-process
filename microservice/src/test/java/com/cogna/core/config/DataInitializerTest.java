package com.cogna.core.config;

import com.cogna.core.model.Matricula;
import com.cogna.core.model.Turma;
import com.cogna.core.repository.MatriculaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    MatriculaRepository repository;

    @InjectMocks
    DataInitializer initializer;

    @Test
    void init_whenEmpty_savesDocuments() {
        when(repository.count()).thenReturn(0L);

        initializer.init();

        verify(repository, atLeastOnce()).saveAll(anyIterable());
    }

    @Test
    void init_whenNotEmpty_doesNotSave() {
        when(repository.count()).thenReturn(5L);

        initializer.init();

        verify(repository, never()).saveAll(anyIterable());
    }
}
