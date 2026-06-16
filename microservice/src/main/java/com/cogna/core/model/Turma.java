package com.cogna.core.model;

import java.util.List;

public class Turma {
    private String codigo;
    private List<String> diasDaSemana;
    private String horarioInicio;
    private String horarioFim;

    public Turma() {}

    public Turma(String codigo, List<String> diasDaSemana, String horarioInicio, String horarioFim) {
        this.codigo = codigo;
        this.diasDaSemana = diasDaSemana;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public List<String> getDiasDaSemana() { return diasDaSemana; }
    public void setDiasDaSemana(List<String> diasDaSemana) { this.diasDaSemana = diasDaSemana; }

    public String getHorarioInicio() { return horarioInicio; }
    public void setHorarioInicio(String horarioInicio) { this.horarioInicio = horarioInicio; }

    public String getHorarioFim() { return horarioFim; }
    public void setHorarioFim(String horarioFim) { this.horarioFim = horarioFim; }
}
