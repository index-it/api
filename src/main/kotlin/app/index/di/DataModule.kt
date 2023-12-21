package app.index.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(includes = [LogicModule::class, ClientModule::class])
@ComponentScan("app.index_it.data")
class DataModule
