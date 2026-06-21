import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantProfileComponent } from './tenant-profile.component';

describe('TenantProfileComponent', () => {
  let component: TenantProfileComponent;
  let fixture: ComponentFixture<TenantProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantProfileComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
