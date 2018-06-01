package br.com.leandro.agenda.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.leandro.agenda.R;
import br.com.leandro.agenda.helper.BitmapHelper;
import br.com.leandro.agenda.modelo.Aluno;

public class AlunosAdapter extends BaseAdapter {

    private final List<Aluno> alunos;
    private final Context context;

    public AlunosAdapter(Context context, List<Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
    }

    @Override
    public int getCount() {
        return alunos.size();
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Aluno aluno = alunos.get(position);


        LayoutInflater inflater = LayoutInflater.from(context);

        View view =  convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);
        }


        TextView campoNome = view.findViewById(R.id.item_nome);
        campoNome.setText(aluno.getNome());


        TextView campoTelefone = view.findViewById(R.id.item_telefone);
        campoTelefone.setText(aluno.getTelefone());


        ImageView campoFoto = view.findViewById(R.id.item_foto);

        TextView campoEndereco = view.findViewById(R.id.item_endereco);
        if (campoEndereco != null) {
            campoEndereco.setText(aluno.getEndereco());
        }

        TextView campoSite = view.findViewById(R.id.item_site);
        if (campoSite != null) {
            campoSite.setText(aluno.getSite());
        }

        if (aluno.getCaminhoFoto() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(aluno.getCaminhoFoto());
            bitmap = BitmapHelper.resize(bitmap, 700, 700);

            campoFoto.setImageBitmap(bitmap);
            campoFoto.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


        return view;
    }
}
