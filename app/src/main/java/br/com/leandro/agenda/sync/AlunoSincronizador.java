package br.com.leandro.agenda.sync;

import android.content.Context;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.dto.AlunoSync;
import br.com.leandro.agenda.dto.ObjetoDiff;
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
        buscaNovos();
    }

    private void buscaNovos() {
        String versao = preferences.getVersao();
        Call<ObjetoDiff> call = new RetrofitInicializador(context).getAlunoService().novos(versao);
        call.enqueue(new Callback<ObjetoDiff>() {
            @Override
            public void onResponse(Call<ObjetoDiff> call, Response<ObjetoDiff> response) {

                ObjetoDiff diff = response.body();

                sincroniza(diff);

                Log.i("Atualiza", "Atualizado para versão: " + preferences.getVersao());

                bus.post(new AtualizaListaAlunoEvent());
                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<ObjetoDiff> call, Throwable t) {
                Log.e("Atualiza", "Não foi possível atualizar");
            }
        });
    }

    public void sincroniza(ObjetoDiff diff) {

        String versao = diff.getTime();

        Log.i("Versão externa", versao);

        if (versaoEhMaisNova(versao)) {

            preferences.salvaVersao(versao);

            AlunoSync[] syncs = diff.getAluno();

            if (syncs != null) {

                AlunoDAO dao = new AlunoDAO(context);

                for (AlunoSync sync : syncs) {
                    sync.atualizaBanco(dao);
                }

                dao.close();
            }

            Log.i("Versão atual", preferences.getVersao());
        }
    }

    private boolean versaoEhMaisNova(String versao) {

        if (!preferences.temVersao())
            return true;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        try {

            Date versaoExterna = format.parse(versao);
            Date versaoInterna = format.parse(preferences.getVersao());

            Log.i("Versão interna", preferences.getVersao());


            return versaoExterna.after(versaoInterna);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void sincronizaAlunosInternos() {
        final AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.listaNaoSincronizados();


        Call<List<Aluno>> call = new RetrofitInicializador(context).getAlunoService().atualiza(alunos);
        call.enqueue(new Callback<List<Aluno>>() {
            @Override
            public void onResponse(Call<List<Aluno>> call, Response<List<Aluno>> response) {
                List<Aluno> alunos = response.body();

                if (alunos != null) {
                    dao.sincroniza(alunos);
                    dao.close();
                }

                Log.e("Sincronizador alunos: ", "Alunos sincronizados com sucesso");
            }

            @Override
            public void onFailure(Call<List<Aluno>> call, Throwable t) {
                Log.e("Sincronizador alunos: ", t.getMessage());
            }
        });
    }

    public void remove(final Aluno aluno) {
        Call<Aluno> call = new RetrofitInicializador(context).getAlunoService().remove(aluno.getId());
        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call<Aluno> call, Response<Aluno> response) {
                AlunoDAO dao = new AlunoDAO(context);
                dao.deleta(aluno);
                dao.close();
            }

            @Override
            public void onFailure(Call<Aluno> call, Throwable t) {
                Log.e("Remoção do aluno: ", "Não foi possível remover o aluno no servidor");
            }
        });
    }
}