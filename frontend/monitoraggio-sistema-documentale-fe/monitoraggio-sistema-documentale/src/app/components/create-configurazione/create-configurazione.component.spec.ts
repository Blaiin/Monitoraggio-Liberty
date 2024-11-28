import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateConfigurazioneComponent } from './create-configurazione.component';

describe('CreateConfigurazioneComponent', () => {
  let component: CreateConfigurazioneComponent;
  let fixture: ComponentFixture<CreateConfigurazioneComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CreateConfigurazioneComponent]
    });
    fixture = TestBed.createComponent(CreateConfigurazioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
