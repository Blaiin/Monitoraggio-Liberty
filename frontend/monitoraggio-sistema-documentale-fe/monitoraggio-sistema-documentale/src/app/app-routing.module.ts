import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MenuConfigurazioniComponent } from './components/menu-configurazioni/menu-configurazioni.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { MonitoraggioStatiComponent } from './components/monitoraggio-stati/monitoraggio-stati.component';
import { CreateConfigurazioneComponent } from './components/create-configurazione/create-configurazione.component';

const routes: Routes = [
  {
    path: 'monitoraggio-sistema-documentale/menu-configurazioni',
    component: MenuConfigurazioniComponent,
  },
  {
    path: 'monitoraggio-sistema-documentale/menu-configurazioni/create-configurazione',
    component: CreateConfigurazioneComponent,
  },
  {
    path: 'monitoraggio-sistema-documentale/monitoraggio-stati',
    component: MonitoraggioStatiComponent,
  },
  {
    path: 'monitoraggio-sistema-documentale/home',
    component: HomepageComponent,
  },
  {
    path: '',
    redirectTo: 'monitoraggio-sistema-documentale/home',
    pathMatch: 'full',
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
