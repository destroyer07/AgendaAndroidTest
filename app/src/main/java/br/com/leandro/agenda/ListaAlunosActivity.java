package br.com.leandro.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import br.com.leandro.agenda.adapter.AlunosAdapter;
import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.event.AtualizaListaAlunoEvent;
import br.com.leandro.agenda.helper.StringHelper;
import br.com.leandro.agenda.modelo.Aluno;
import br.com.leandro.agenda.sync.AlunoSincronizador;

public class ListaAlunosActivity extends AppCompatActivity {

    private final AlunoSincronizador alunoSincronizador = new AlunoSincronizador(this);
    private ListView listaAlunos;
    private SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_alunos);

        EventBus eventBus = EventBus.getDefault();
        eventBus.register(this);

        Button btnNovoAluno = findViewById(R.id.novo_aluno);
        btnNovoAluno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                startActivity(intentVaiProFormulario);

            }
        });

        listaAlunos = findViewById(R.id.lista_alunos);

        swipe = findViewById(R.id.swipe_lista_alunos);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                alunoSincronizador.buscaAlunos();
            }
        });


        listaAlunos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> lista, View item, int position, long id) {
                Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(position);
                Intent intentVaiProFormulario = new Intent(ListaAlunosActivity.this, FormularioActivity.class);
                intentVaiProFormulario.putExtra("aluno", aluno);
                startActivity(intentVaiProFormulario);
            }
        });

        registerForContextMenu(listaAlunos);

        alunoSincronizador.buscaAlunos();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void atualizaListaAlunoEvent(AtualizaListaAlunoEvent event) {
        if (swipe.isRefreshing())
            swipe.setRefreshing(false);
        carregaLista();
    }

    private void carregaLista() {
        AlunoDAO dao = new AlunoDAO(this);
        List<Aluno> alunos = dao.buscaAlunos();
        dao.close();


        AlunosAdapter adapter = new AlunosAdapter(this, alunos);
        listaAlunos.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_alunos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_enviar_notas:
                new EnviaAlunosTask(this).execute();
                break;
            case R.id.menu_baixar_provas:
                Intent vaiParaProvas = new Intent(this, ProvasActivity.class);
                startActivity(vaiParaProvas);
                break;
            case R.id.menu_mapa:
                Intent vaiParaMapa = new Intent(this, MapaActivity.class);
                startActivity(vaiParaMapa);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        final Aluno aluno = (Aluno) listaAlunos.getItemAtPosition(info.position);


        // Opção de ligar para aluno
        MenuItem itemLigar = menu.add("Ligar");
        final String telefone = aluno.getTelefone();

        // Opção de enviar SMS para aluno
        MenuItem itemSMS = menu.add("Enviar SMS");
        Intent intentSMS = new Intent(Intent.ACTION_VIEW);

        // Monta as duas opções
        if (!StringHelper.isNullOrWhitespace(telefone)) {
            itemLigar.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (ActivityCompat.checkSelfPermission(ListaAlunosActivity.this, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ListaAlunosActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE}, 123);
                    } else {
                        Intent intentLigar = new Intent(Intent.ACTION_CALL);
                        intentLigar.setData(Uri.parse("tel:" + telefone));
                        startActivity(intentLigar);
                    }


                    return false;
                }
            });


            intentSMS.setData(Uri.parse("sms:" + telefone));
            itemSMS.setIntent(intentSMS);
        } else {
            itemLigar.setEnabled(false);
            itemSMS.setEnabled(false);
        }


        // Opção de visualização de endereço de aluno no mapa
        MenuItem itemMapa = menu.add("Visualizar no mapa");
        Intent intentMapa = new Intent(Intent.ACTION_VIEW);

        String endereco = aluno.getEndereco();
        if (!StringHelper.isNullOrWhitespace(endereco)) {
            intentMapa.setData(Uri.parse("geo:0,0?q=" + endereco));
            itemMapa.setIntent(intentMapa);
        } else {
            itemMapa.setEnabled(false);
        }


        // Opção de acesso ao site do aluno
        MenuItem itemSite = menu.add("Visitar site");
        Intent intentSite = new Intent(Intent.ACTION_VIEW);

        String site = aluno.getSite();
        if (!StringHelper.isNullOrWhitespace(site)) {
            if (!site.startsWith("http://")) {
                site = "http://" + site;
            }

            intentSite.setData(Uri.parse(site));
            itemSite.setIntent(intentSite);
        } else {
            itemSite.setEnabled(false);
        }


        // Opção de remover o aluno
        MenuItem remover = menu.add("Remover");
        remover.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                AlunoDAO dao = new AlunoDAO(ListaAlunosActivity.this);
                dao.deleta(aluno);
                dao.close();
                carregaLista();

                alunoSincronizador.remove(aluno);

                return false;
            }
        });
    }

}
