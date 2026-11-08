import { TestBed } from '@angular/core/testing';

import { ResumoService } from './resumo.service';

describe('ResumoService', () => {
  let service: ResumoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResumoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
