import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonitoraggioStatiComponent } from './monitoraggio-stati.component';

describe('MonitoraggioStatiComponent', () => {
  let component: MonitoraggioStatiComponent;
  let fixture: ComponentFixture<MonitoraggioStatiComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MonitoraggioStatiComponent]
    });
    fixture = TestBed.createComponent(MonitoraggioStatiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
