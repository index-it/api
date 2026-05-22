package app.index.api.di

import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module
@ComponentScan("app.index.api.core.logic")
class LogicModule
