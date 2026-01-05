import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordBookingDetailComponent } from './landlord-booking-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('LandlordBookingDetailComponent', () => {
  let component: LandlordBookingDetailComponent;
  let fixture: ComponentFixture<LandlordBookingDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordBookingDetailComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordBookingDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
