package br.com.leandro.agenda;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.Manifest;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class Localizador implements GoogleApiClient.ConnectionCallbacks, LocationListener {
    private final GoogleApiClient client;
    private final GoogleMap mapa;
    private final Context context;

    public Localizador(Context context, GoogleMap mapa) {
        client = new GoogleApiClient
                .Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        client.connect();

        this.context = context;
        this.mapa = mapa;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationRequest request = LocationRequest.create();

        // Só atualiza a posição após andar 30 metros
        request.setSmallestDisplacement(30);

        // Atualiza a posição a cada 1 segundo
        request.setInterval(1000);

        // Prioriza a precisão e não a economia de energia
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        boolean temPermissao = true;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {

            temPermissao = false;

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 345);
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            temPermissao = false;

            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, 345);
        }

        if (temPermissao) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng coordenada = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(coordenada);
        mapa.moveCamera(cameraUpdate);
    }
}
