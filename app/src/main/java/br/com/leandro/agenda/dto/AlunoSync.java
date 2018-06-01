package br.com.leandro.agenda.dto;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.lang.reflect.Array;

import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.modelo.Aluno;

public class AlunoSync {

    private enum Actions {
        add, del, upd
    }

    @JsonProperty("action")
    private Actions action;
    private Aluno data;


    public Actions getAction() {
        return action;
    }

    public void setAction(Actions action) {
        this.action = action;
    }

    public Aluno getData() {
        return data;
    }

    public void setData(Aluno data) {
        this.data = data;
    }

    public void atualizaBanco(Context context) {

        AlunoDAO dao = new AlunoDAO(context);

        switch (action) {
            case add:
                Log.i("Aluno adicionado: ", data.toString());
                dao.insere(data);
                break;
            case upd:
                Log.i("Aluno alterado: ", data.toString());
                dao.altera(data);
                break;
            case del:
                Log.i("Aluno removido: ", data.toString());
                dao.deleta(data);
                break;
        }

        dao.close();
    }
}
