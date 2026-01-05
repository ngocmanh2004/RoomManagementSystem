import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantFeedbackComponent } from './tenant-feedback.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('TenantFeedbackComponent', () => {
  let component: TenantFeedbackComponent;
  let fixture: ComponentFixture<TenantFeedbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantFeedbackComponent, HttpClientTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantFeedbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
