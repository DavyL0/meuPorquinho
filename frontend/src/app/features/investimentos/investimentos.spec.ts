import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

import { InvestimentosComponent } from './investimentos';

describe('InvestimentosComponent', () => {
  let component: InvestimentosComponent;
  let fixture: ComponentFixture<InvestimentosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvestimentosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(InvestimentosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
