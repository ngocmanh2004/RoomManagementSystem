import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordStatusComponent } from './landlord-status.component';

describe('LandlordStatusComponent', () => {
  let component: LandlordStatusComponent;
  let fixture: ComponentFixture<LandlordStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordStatusComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
