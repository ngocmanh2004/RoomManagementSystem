import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordLayoutComponent } from './landlord-layout.component';

describe('LandlordLayoutComponent', () => {
  let component: LandlordLayoutComponent;
  let fixture: ComponentFixture<LandlordLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordLayoutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
