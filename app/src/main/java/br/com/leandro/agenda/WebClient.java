package br.com.leandro.agenda;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class WebClient {

    private URL raizServidor;

    public WebClient() {
        try {

            this.raizServidor = new URL(Resources.getSystem().getString(R.string.HOST_URL));

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String post(String json) {
        return realizaConexao(json, "/alunos/medianotas");
    }

    public String insere(String json) {
        return realizaConexao(json, "/alunos");
    }

    @Nullable
    private String realizaConexao(String json, String api) {
        try {
            URL url = new URL(raizServidor, api);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Informa que enviará um json
            connection.setRequestProperty("Content-type", "application/json");

            // Informa que quer receber um json de resposta
            connection.setRequestProperty("Accept", "application/json");

            // Informa que haverá dados de saída no corpo da requisição
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            PrintStream output = new PrintStream(connection.getOutputStream());
            output.println(json);

            connection.connect();

            Scanner scanner = new Scanner(connection.getInputStream());

            String resultado = scanner.next();

            return resultado;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
