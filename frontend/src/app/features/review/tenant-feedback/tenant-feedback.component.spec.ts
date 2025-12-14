import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantFeedbackComponent } from './tenant-feedback.component';

describe('TenantFeedbackComponent', () => {
  let component: TenantFeedbackComponent;
  let fixture: ComponentFixture<TenantFeedbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantFeedbackComponent]
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
