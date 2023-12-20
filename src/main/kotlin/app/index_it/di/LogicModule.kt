package app.index_it.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("app.index_it.core.logic")
class LogicModule