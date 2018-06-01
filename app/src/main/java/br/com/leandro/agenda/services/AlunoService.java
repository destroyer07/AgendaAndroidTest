package br.com.leandro.agenda.services;


import java.util.List;

import br.com.leandro.agenda.dto.AlunoSync;
import br.com.leandro.agenda.modelo.Aluno;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AlunoService {

    @POST("alunos")
    Call<Void> insere(@Body Aluno aluno);

    @GET("alunos")
    Call<List<Aluno>> lista();

    @DELETE("alunos/{id}")
    Call<Aluno> remove(@Path("id") String id);

    @PUT("alunos/{id}")
    Call<Aluno> altera(@Path("id") String id, @Body Aluno aluno);

    @GET("alunos/diff")
    Call<AlunoSync[]> novos(@Header("dataHora") String versao);
}
