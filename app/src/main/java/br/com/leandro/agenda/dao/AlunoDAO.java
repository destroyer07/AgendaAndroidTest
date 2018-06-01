package br.com.leandro.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.com.leandro.agenda.modelo.Aluno;

public class AlunoDAO extends SQLiteOpenHelper {


    public AlunoDAO(Context context) {
        super(context, "Agenda", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        atualizaBanco(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        atualizaBanco(db, oldVersion);
    }

    private void atualizaBanco(SQLiteDatabase db, int version) {
        switch (version) {
            case 0:
                // Cria tabela
                db.execSQL(
                    "CREATE TABLE Alunos (" +
                        "id INTEGER PRIMARY KEY," +
                        "nome TEXT," +
                        "endereco TEXT," +
                        "telefone TEXT," +
                        "site TEXT," +
                        "nota REAL" +
                    ")"
                );
            case 1:
                // Adiciona coluna de caminho de foto
                db.execSQL(
                    "ALTER TABLE Alunos ADD COLUMN caminhoFoto;"
                );
            case 2:
                // Cria nova tabela com tipo de ID diferente
                db.execSQL(
                    "CREATE TABLE Alunos_novo ( " +
                        "id CHAR(36) PRIMARY KEY, " +
                        "nome TEXT, " +
                        "endereco TEXT, " +
                        "telefone TEXT, " +
                        "site TEXT, " +
                        "nota REAL, " +
                        "caminhoFoto TEXT" +
                    ");"
                );

                // Transefere dados para nova tabela
                db.execSQL(
                    "INSERT INTO Alunos_novo (id, nome, endereco, telefone, site, nota, caminhoFoto) " +
                        "SELECT id, nome, endereco, telefone, site, nota, caminhoFoto FROM Alunos;"
                );

                // Apaga tabela antiga
                db.execSQL(
                    "DROP TABLE Alunos;"
                );

                // Substitui a tabela antiga pela nova
                db.execSQL(
                    "ALTER TABLE Alunos_novo RENAME TO Alunos;"
                );
            case 3:
                Cursor c = db.rawQuery("SELECT * FROM Alunos", null);

                List<Aluno> alunos = populaAlunos(c);

                for (Aluno aluno : alunos) {
                    db.execSQL(
                            "UPDATE Alunos SET id=? WHERE id=?",
                            new String[] { geraUUID(), String.valueOf(aluno.getId())}
                    );
                }
        }
    }

    private String geraUUID() {
        return UUID.randomUUID().toString();
    }

    public void insere(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        insereIdSeNecessario(aluno);

        ContentValues dados = pegaDadosDoAluno(aluno);

        db.insert("Alunos", null, dados);
    }

    private void insereIdSeNecessario(Aluno aluno) {
        // Se não possui, adiciona um novo ID
        if (aluno.getId() == null) {
            aluno.setId(geraUUID());
        }
    }

    @NonNull
    private ContentValues pegaDadosDoAluno(Aluno aluno) {
        ContentValues dados = new ContentValues();
        dados.put("id", aluno.getId());
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        return dados;
    }

    public List<Aluno> buscaAlunos() {
        String sql = "SELECT * FROM Alunos";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(sql, null);

        List<Aluno> alunos = populaAlunos(c);

        c.close();

        return alunos;
    }

    @NonNull
    private List<Aluno> populaAlunos(Cursor c) {
        List<Aluno> alunos = new ArrayList<>();

        while (c.moveToNext()) {
            Aluno aluno = new Aluno();

            aluno.setId(c.getString(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));

            alunos.add(aluno);
        }
        return alunos;
    }

    public void deleta(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        String[] params = { aluno.getId().toString() };
        db.delete("Alunos", "id = ?", params);
    }

    public void altera(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues dados = pegaDadosDoAluno(aluno);

        String[] params = { aluno.getId().toString() };
        db.update("Alunos", dados, "id = ?", params);
    }

    public boolean ehAluno(String telefone) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM Alunos WHERE telefone = ?", new String[] { telefone });
        int resultados = c.getCount();

        c.close();

        return resultados > 0;
    }

    public void sincroniza(List<Aluno> alunos) {
        for (Aluno aluno : alunos) {
            if (existe(aluno)) {
                altera(aluno);
            } else {
                insere(aluno);
            }
        }
    }

    private boolean existe(Aluno aluno) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM Alunos WHERE id = ? LIMIT 1",
                new String[]{aluno.getId()}
        );
        return cursor.getCount() > 0;
    }
}
