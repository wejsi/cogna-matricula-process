package com.cogna.core.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "matriculas")
public class Matricula {
    @Id
    private String id;
    private String alunoId;
    private String businessKey;
    private String status;
    private Turma turma;
    private Integer cicloId;
    private Instant dataMatricula;

    public Matricula() {}

    public Matricula(String alunoId, String businessKey, String status, Turma turma, Integer cicloId, Instant dataMatricula) {
        this.alunoId = alunoId;
        this.businessKey = businessKey;
        this.status = status;
        this.turma = turma;
        this.cicloId = cicloId;
        this.dataMatricula = dataMatricula;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAlunoId() { return alunoId; }
    public void setAlunoId(String alunoId) { this.alunoId = alunoId; }

    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public Integer getCicloId() { return cicloId; }
    public void setCicloId(Integer cicloId) { this.cicloId = cicloId; }

    public Instant getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(Instant dataMatricula) { this.dataMatricula = dataMatricula; }
}
