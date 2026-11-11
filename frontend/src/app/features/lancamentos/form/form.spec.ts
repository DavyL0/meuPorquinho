import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';

import { LancamentoFormComponent } from './form';

describe('LancamentoFormComponent', () => {
  let component: LancamentoFormComponent;
  let fixture: ComponentFixture<LancamentoFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LancamentoFormComponent],
      providers: [provideHttpClient(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(LancamentoFormComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
