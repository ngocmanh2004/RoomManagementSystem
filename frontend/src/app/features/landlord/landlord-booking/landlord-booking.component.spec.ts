import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { LandlordBookingComponent } from './landlord-booking.component';

describe('LandlordBookingComponent', () => {
  let component: LandlordBookingComponent;
  let fixture: ComponentFixture<LandlordBookingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        LandlordBookingComponent,
        HttpClientTestingModule,
        RouterTestingModule // ✅ BẮT BUỘC
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LandlordBookingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
