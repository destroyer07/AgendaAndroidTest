package br.com.leandro.agenda;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.helper.FormularioHelper;
import br.com.leandro.agenda.modelo.Aluno;
import br.com.leandro.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormularioActivity extends AppCompatActivity {

    public static final int CODIGO_CAMERA = 567;
    private FormularioHelper helper;
    private String caminhoFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        helper = new FormularioHelper(this);

        Intent intent = getIntent();
        Aluno aluno = (Aluno) intent.getSerializableExtra("aluno");

        if (aluno != null) {
            helper.preencheFormulario(aluno);
        }

        Button botaoFoto = findViewById(R.id.formulario_botao_foto);
        botaoFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caminhoFoto = getExternalFilesDir(null) + "/" + System.currentTimeMillis() + ".jpg";
                File arquivoFoto = new File(caminhoFoto);

                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(arquivoFoto));
                startActivityForResult(intentCamera, CODIGO_CAMERA);
            }
        });

        // Adicionadas essas duas linhas para poder gravar o arquivo da foto
        // feita com a câmera na memória do celular, sem isso dá erro
        StrictMode.VmPolicy.Builder newbuilder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(newbuilder.build());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODIGO_CAMERA:

//                    Bundle extras = data.getExtras();

//                    Bitmap bitmap = (Bitmap) extras.get("data");
                    helper.carregaImagem(caminhoFoto);
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_formulario, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_formulario_ok:

                Aluno aluno = helper.pegaAluno();

                if (aluno.getId() != null) {
                    alteraAluno(aluno);
                } else {
                    criaNovoAluno(aluno);
                }

                Toast.makeText(FormularioActivity.this, "Aluno " + aluno.getNome() + " salvo!", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void criaNovoAluno(Aluno aluno) {
        AlunoDAO dao = new AlunoDAO(this);
        dao.insere(aluno);
        dao.close();

        Call<Void> call = new RetrofitInicializador().getAlunoService().insere(aluno);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("onResponse", "Requisição com sucesso");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("onFailure", "Requisição falhou");
            }
        });
    }

    private void alteraAluno(Aluno aluno) {
        AlunoDAO dao = new AlunoDAO(this);
        dao.altera(aluno);
        dao.close();

        Call<Aluno> call = new RetrofitInicializador().getAlunoService().altera(aluno.getId(), aluno);

        call.enqueue(new Callback<Aluno>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.i("onResponse", "Requisição com sucesso");
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("onFailure", "Requisição falhou");
            }
        });
    }
}
