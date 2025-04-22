package ar.edu.uade.deremateapp.di;

import android.content.Context;

import java.io.File;

import javax.inject.Singleton;

import ar.edu.uade.deremateapp.BuildConfig;
import ar.edu.uade.deremateapp.data.api.EntregasAPIService;
import ar.edu.uade.deremateapp.data.api.LoginAPIService;
import ar.edu.uade.deremateapp.data.api.RegistroAPIService;
import ar.edu.uade.deremateapp.data.api.UsuarioService;
import ar.edu.uade.deremateapp.data.repository.token.TokenRepository;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    Cache provideCache(@ApplicationContext Context context) {
        int cacheSize = 10 * 1024 * 1024; // 10 MB
        File cacheDir = new File(context.getCacheDir(), "http-cache");
        return new Cache(cacheDir, cacheSize);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, TokenRepository tokenRepository) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        TokenInterceptor tokenInterceptor = new TokenInterceptor(tokenRepository);
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
        
        return new OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(tokenInterceptor)
            .cache(cache)
            .addNetworkInterceptor(chain -> {
                return chain.proceed(chain.request())
                    .newBuilder()
                    .header("Cache-Control", "public, max-age=60") // Cache por 60 segundos
                    .build();
            })
            .build();
    }
    
    @Provides
    @Singleton
    Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
    
    @Provides
    @Singleton
    LoginAPIService provideLoginService(Retrofit retrofit) {
        return retrofit.create(LoginAPIService.class);
    }

    @Provides
    @Singleton
    EntregasAPIService provideEntregasApiService(Retrofit retrofit) {
        return retrofit.create(EntregasAPIService.class);
    }

    @Provides
    @Singleton
    public static UsuarioService provideUsuarioService(Retrofit retrofit) {
        return retrofit.create(UsuarioService.class);
    }

    @Provides
    @Singleton
    public static RegistroAPIService provideRegistroService(Retrofit retrofit) {
        return retrofit.create(RegistroAPIService.class);
    }
}
