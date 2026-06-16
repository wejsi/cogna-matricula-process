package com.cogna.core.dto;

import java.time.Instant;
import java.util.List;

public class MatriculaAtualizadaEvent {
    private String matriculaId;
    private String alunoId;
    private String businessKey;
    private Integer cicloId;
    private List<String> diasDaSemanaAnterior;
    private List<String> diasDaSemanaNovo;
    private Instant dataAtualizacao;

    public String getMatriculaId() { return matriculaId; }
    public void setMatriculaId(String matriculaId) { this.matriculaId = matriculaId; }

    public String getAlunoId() { return alunoId; }
    public void setAlunoId(String alunoId) { this.alunoId = alunoId; }

    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }

    public Integer getCicloId() { return cicloId; }
    public void setCicloId(Integer cicloId) { this.cicloId = cicloId; }

    public List<String> getDiasDaSemanaAnterior() { return diasDaSemanaAnterior; }
    public void setDiasDaSemanaAnterior(List<String> diasDaSemanaAnterior) { this.diasDaSemanaAnterior = diasDaSemanaAnterior; }

    public List<String> getDiasDaSemanaNovo() { return diasDaSemanaNovo; }
    public void setDiasDaSemanaNovo(List<String> diasDaSemanaNovo) { this.diasDaSemanaNovo = diasDaSemanaNovo; }

    public Instant getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(Instant dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}
