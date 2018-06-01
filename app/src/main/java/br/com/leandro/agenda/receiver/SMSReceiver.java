package br.com.leandro.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.Toast;
import android.telephony.SmsMessage;

import java.io.Serializable;

import br.com.leandro.agenda.R;
import br.com.leandro.agenda.dao.AlunoDAO;

public class SMSReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        byte[] pdu = (byte[]) pdus[0];
        String formato = (String) intent.getSerializableExtra("format");

        SmsMessage sms = SmsMessage.createFromPdu(pdu, formato);
        String telefone = sms.getDisplayOriginatingAddress();
        AlunoDAO dao = new AlunoDAO(context);

        if (dao.ehAluno(telefone)) {
            Toast.makeText(context, "Chegou o SMS de um aluno!", Toast.LENGTH_SHORT).show();
            MediaPlayer mp = MediaPlayer.create(context, R.raw.air_horn);
            mp.start();
        }

        dao.close();
    }
}
