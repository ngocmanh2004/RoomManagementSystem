import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordReviewPageComponent } from './landlord-review-page.component';

describe('LandlordReviewPageComponent', () => {
  let component: LandlordReviewPageComponent;
  let fixture: ComponentFixture<LandlordReviewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordReviewPageComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordReviewPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
