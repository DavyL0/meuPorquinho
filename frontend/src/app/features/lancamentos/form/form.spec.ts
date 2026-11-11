import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { LancamentoFormComponent } from './form';

describe('LancamentoFormComponent', () => {
  let component: LancamentoFormComponent;
  let fixture: ComponentFixture<LancamentoFormComponent>;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LancamentoFormComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    httpMock = TestBed.inject(HttpTestingController);
    fixture = TestBed.createComponent(LancamentoFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();

    // Responde as chamadas do painel "Resumo do mês"
    for (const req of httpMock.match(() => true)) {
      if (req.request.url.includes('/api/resumo/categorias')) {
        req.flush([]);
      } else if (req.request.url.includes('/api/resumo')) {
        req.flush({ totalEntradas: 0, totalGastos: 0, totalInvestimentos: 0, saldo: 0 });
      } else {
        req.flush({ content: [], totalElements: 0, totalPages: 0, size: 1, number: 0 });
      }
    }
    await fixture.whenStable();
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
