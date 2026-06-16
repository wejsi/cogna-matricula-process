package com.cogna.core.dto;

import com.cogna.core.model.Turma;

public class TurmaAtualizadaEvent {
    private String businessKey;
    private Turma turma;
    private Integer cicloId;

    public String getBusinessKey() { return businessKey; }
    public void setBusinessKey(String businessKey) { this.businessKey = businessKey; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public Integer getCicloId() { return cicloId; }
    public void setCicloId(Integer cicloId) { this.cicloId = cicloId; }
}
