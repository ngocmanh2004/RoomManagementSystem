import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportManagementComponent } from './report-management.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('ReportManagementComponent', () => {
  let component: ReportManagementComponent;
  let fixture: ComponentFixture<ReportManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReportManagementComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReportManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
