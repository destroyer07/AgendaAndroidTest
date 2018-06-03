package br.com.leandro.agenda.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ObjetoDiff {
    private String time;
    private AlunoSync[] Aluno;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public AlunoSync[] getAluno() {
        return Aluno;
    }

    public void setAluno(AlunoSync[] aluno) {
        Aluno = aluno;
    }
}
