package ar.edu.uade.deremateapp.di;

import androidx.annotation.NonNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    TokenRepository tokenRepository;

    public TokenInterceptor(TokenRepository tokenRepository){
        this.tokenRepository = tokenRepository;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

        if(tokenRepository.getToken() != null){
            Request newRequest = chain.request().newBuilder()
                    .header("Authorization", "Bearer "+tokenRepository.getToken())
                    .build();
            return chain.proceed(newRequest);
        }

        return chain.proceed(chain.request());
    }
}
