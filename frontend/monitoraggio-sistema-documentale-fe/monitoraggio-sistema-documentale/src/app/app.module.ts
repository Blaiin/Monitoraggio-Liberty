import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MenuConfigurazioniComponent } from './components/menu-configurazioni/menu-configurazioni.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { MonitoraggioStatiComponent } from './components/monitoraggio-stati/monitoraggio-stati.component';
import { NgChartsModule } from 'ng2-charts';
import { CreateConfigurazioneComponent } from './components/create-configurazione/create-configurazione.component';

@NgModule({
  declarations: [
    AppComponent,
    MenuConfigurazioniComponent,
    MonitoraggioStatiComponent,
    NavbarComponent,
    HomepageComponent,
    CreateConfigurazioneComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MaterialModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgChartsModule
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
