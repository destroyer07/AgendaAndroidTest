package br.com.leandro.agenda.converter;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.List;

import br.com.leandro.agenda.modelo.Aluno;

public class AlunoConverter {
    public String converterParaJSON(List<Aluno> alunos) {

        JSONStringer json = new JSONStringer();

        try {
            json.array();

            for (Aluno aluno : alunos) {
                json.object();
                json.key("nome").value(aluno.getNome());
                json.key("nota").value(aluno.getNota());
                json.endObject();
            }

            json.endArray();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public String converterParaJSON(Aluno aluno) {
        JSONStringer json = new JSONStringer();

        try {
            json.object();
            json.key("nome").value(aluno.getNome());
            json.key("endereco").value(aluno.getEndereco());
            json.key("telefone").value(aluno.getTelefone());
            json.key("site").value(aluno.getSite());
            json.key("nota").value(aluno.getNota());
            json.endObject();

            return json.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
