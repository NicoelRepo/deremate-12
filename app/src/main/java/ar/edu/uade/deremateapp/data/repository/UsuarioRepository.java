package ar.edu.uade.deremateapp.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ar.edu.uade.deremateapp.data.api.model.UsuarioDTO;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class UsuarioRepository {
    private static final String KEY_DETALLE_USUARIO = "detalle_usuario";
    private static final String USUARIO_PREFS = "usuario_prefs";
    private final SharedPreferences encryptedPrefs;
    private final Gson gson = new Gson();

    @Inject
    public UsuarioRepository(@ApplicationContext Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            this.encryptedPrefs = EncryptedSharedPreferences.create(
                    context,
                    USUARIO_PREFS,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Error initializing EncryptedSharedPreferences", e);
        }
    }

    public void saveUsuario(UsuarioDTO usuario) {
        encryptedPrefs.edit().putString(KEY_DETALLE_USUARIO, gson.toJson(usuario)).apply();
    }

    public UsuarioDTO getUsuario() {
        String json = encryptedPrefs.getString(KEY_DETALLE_USUARIO, null);
        if (json != null) {
            return gson.fromJson(json, UsuarioDTO.class);
        }
        return null;
    }

    public void clearUsuario() {
        encryptedPrefs.edit().remove(KEY_DETALLE_USUARIO).apply();
    }
}