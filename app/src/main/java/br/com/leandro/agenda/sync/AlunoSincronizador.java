package br.com.leandro.agenda.sync;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import br.com.leandro.agenda.ListaAlunosActivity;
import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.dto.AlunoSync;
import br.com.leandro.agenda.event.AtualizaListaAlunoEvent;
import br.com.leandro.agenda.modelo.Aluno;
import br.com.leandro.agenda.preferences.AlunoPreferences;
import br.com.leandro.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlunoSincronizador {
    private final Context context;
    private AlunoPreferences preferences;
    private EventBus bus = EventBus.getDefault();

    public AlunoSincronizador(Context context) {
        this.context = context;
        preferences = new AlunoPreferences(context);
    }

    public void buscaAlunos() {
        if (preferences.temVersao()) {
            buscaNovos();
        } else {
            buscaTodos();
        }
    }

    private void buscaNovos() {
        String versao = preferences.getVersao();
        Call<AlunoSync[]> call = new RetrofitInicializador().getAlunoService().novos(versao);
        call.enqueue(new Callback<AlunoSync[]>() {
            @Override
            public void onResponse(Call<AlunoSync[]> call, Response<AlunoSync[]> response) {

            }

            @Override
            public void onFailure(Call<AlunoSync[]> call, Throwable t) {

            }
        });
    }

    private void buscaTodos() {
        Call<List<Aluno>> call = new RetrofitInicializador().getAlunoService().lista();

        call.enqueue(new Callback<List<Aluno>>() {
            @Override
            public void onResponse(Call<List<Aluno>> call, Response<List<Aluno>> response) {
                List<Aluno> alunos = response.body();

                String versao = "1";
                preferences.salvaVersao(versao);

                AlunoDAO dao = new AlunoDAO(context);
                dao.sincroniza(alunos);
                dao.close();

                Log.i("versão", preferences.getVersao());

                bus.post(new AtualizaListaAlunoEvent());
            }

            @Override
            public void onFailure(Call<List<Aluno>> call, Throwable t) {
                Log.e("onFailure chamado", t.getMessage());
                bus.post(new AtualizaListaAlunoEvent());
            }
        });
    }
}