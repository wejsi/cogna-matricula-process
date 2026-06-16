package com.cogna.core.repository;

import com.cogna.core.model.Matricula;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MatriculaRepository extends MongoRepository<Matricula, String> {
    List<Matricula> findByBusinessKeyAndStatus(String businessKey, String status);
}
