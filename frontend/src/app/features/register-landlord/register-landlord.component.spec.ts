import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterLandlordComponent } from './register-landlord.component';

describe('RegisterLandlordComponent', () => {
  let component: RegisterLandlordComponent;
  let fixture: ComponentFixture<RegisterLandlordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegisterLandlordComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegisterLandlordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
