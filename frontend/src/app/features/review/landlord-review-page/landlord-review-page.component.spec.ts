import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordReviewPageComponent } from './landlord-review-page.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';

describe('LandlordReviewPageComponent', () => {
  let component: LandlordReviewPageComponent;
  let fixture: ComponentFixture<LandlordReviewPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordReviewPageComponent, HttpClientTestingModule]
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
