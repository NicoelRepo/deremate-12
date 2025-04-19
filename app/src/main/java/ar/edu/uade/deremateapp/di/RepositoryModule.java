package ar.edu.uade.deremateapp.di;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {
    
//    @Binds
//    @Singleton
//    public abstract PokemonRepository providePokemonRepository(PokemonRetrofitRepository implementation);
}
