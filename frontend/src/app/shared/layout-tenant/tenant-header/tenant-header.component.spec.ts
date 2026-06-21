import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TenantHeaderComponent } from './tenant-header.component';

describe('TenantHeaderComponent', () => {
  let component: TenantHeaderComponent;
  let fixture: ComponentFixture<TenantHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TenantHeaderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TenantHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
