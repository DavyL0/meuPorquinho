import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ResumoService } from './resumo.service';

describe('ResumoService', () => {
  let service: ResumoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ResumoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
