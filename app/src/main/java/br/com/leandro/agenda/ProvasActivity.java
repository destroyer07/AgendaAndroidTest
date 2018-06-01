package br.com.leandro.agenda;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import br.com.leandro.agenda.modelo.Prova;

public class ProvasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provas);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction tx = fragmentManager.beginTransaction();

        tx.replace(R.id.frame_principal, new ListaProvasFragment());

        if (estaNoModoPaisagem()) {
            tx.replace(R.id.frame_secundario, new DetalhesProvaFragment());
        }

        tx.commit();
    }

    private boolean estaNoModoPaisagem() {
        return getResources().getBoolean(R.bool.modoPaisagem);
    }

    public void selecionaProva(Prova prova) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (!estaNoModoPaisagem()) {
            FragmentTransaction tx = fragmentManager.beginTransaction();

            DetalhesProvaFragment detalhesFragment = new DetalhesProvaFragment();

            Bundle parametros = new Bundle();
            parametros.putSerializable("prova", prova);
            detalhesFragment.setArguments(parametros);
            
            tx.replace(R.id.frame_principal, detalhesFragment);

            tx.addToBackStack(null);
            tx.commit();
        } else {
            DetalhesProvaFragment fragment = (DetalhesProvaFragment) fragmentManager.findFragmentById(R.id.frame_secundario);
            fragment.populaCamposCom(prova);
        }
    }
}
