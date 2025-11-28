import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordBookingComponent } from './landlord-booking.component';

describe('LandlordBookingComponent', () => {
  let component: LandlordBookingComponent;
  let fixture: ComponentFixture<LandlordBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordBookingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
