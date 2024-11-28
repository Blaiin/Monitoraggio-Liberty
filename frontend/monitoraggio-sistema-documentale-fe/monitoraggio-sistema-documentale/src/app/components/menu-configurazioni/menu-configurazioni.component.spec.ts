import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuConfigurazioniComponent } from './menu-configurazioni.component';

describe('MenuConfigurazioniComponent', () => {
  let component: MenuConfigurazioniComponent;
  let fixture: ComponentFixture<MenuConfigurazioniComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MenuConfigurazioniComponent]
    });
    fixture = TestBed.createComponent(MenuConfigurazioniComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
