import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandlordSidebarComponent } from './landlord-sidebar.component';

describe('LandlordSidebarComponent', () => {
  let component: LandlordSidebarComponent;
  let fixture: ComponentFixture<LandlordSidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandlordSidebarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandlordSidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
