import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordHeaderComponent } from './landlord-header.component';

describe('LandlordHeaderComponent', () => {
  let component: LandlordHeaderComponent;
  let fixture: ComponentFixture<LandlordHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
