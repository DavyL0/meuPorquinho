import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { provideCharts, withDefaultRegisterables } from 'ng2-charts';
import { RelatoriosComponent } from './relatorios';

describe('RelatoriosComponent', () => {
  let component: RelatoriosComponent;
  let fixture: ComponentFixture<RelatoriosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RelatoriosComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([]), provideCharts(withDefaultRegisterables())],
    }).compileComponents();

    fixture = TestBed.createComponent(RelatoriosComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
