import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordFeedbackComponent } from './landlord-feedback.component';

describe('LandlordFeedbackComponent', () => {
  let component: LandlordFeedbackComponent;
  let fixture: ComponentFixture<LandlordFeedbackComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordFeedbackComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordFeedbackComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
