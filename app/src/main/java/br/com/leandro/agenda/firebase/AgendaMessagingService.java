package br.com.leandro.agenda.firebase;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Map;

import br.com.leandro.agenda.dao.AlunoDAO;
import br.com.leandro.agenda.dto.AlunoSync;
import br.com.leandro.agenda.dto.ObjetoDiff;
import br.com.leandro.agenda.event.AtualizaListaAlunoEvent;
import br.com.leandro.agenda.sync.AlunoSincronizador;

public class AgendaMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> mensagem = remoteMessage.getData();
        Log.i("Mensagem recebida", String.valueOf(mensagem));

        converteDados(mensagem);
    }

    private void converteDados(Map<String, String> mensagem) {

        if (mensagem.containsKey("aluno")) {
            String json = mensagem.toString();
            ObjectMapper mapper = new ObjectMapper();

            try {
                ObjetoDiff diff = mapper.readValue(json, ObjetoDiff.class);

                new AlunoSincronizador(this).sincroniza(diff);

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunoEvent());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
