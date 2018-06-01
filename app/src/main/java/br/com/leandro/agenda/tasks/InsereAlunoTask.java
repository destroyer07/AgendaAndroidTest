package br.com.leandro.agenda.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import br.com.leandro.agenda.WebClient;
import br.com.leandro.agenda.converter.AlunoConverter;
import br.com.leandro.agenda.modelo.Aluno;

public class InsereAlunoTask extends AsyncTask<Object, Void, String> {
    private final Context context;
    private final Aluno aluno;

    public InsereAlunoTask(Context context, Aluno aluno) {
        this.context = context;
        this.aluno = aluno;
    }

    @Override
    protected String doInBackground(Object[] objects) {

        String json = new AlunoConverter().converterParaJSON(aluno);

        return new WebClient().insere(json);
    }

    @Override
    protected void onPostExecute(String resposta) {
        Toast.makeText(context, resposta, Toast.LENGTH_SHORT).show();
    }
}
