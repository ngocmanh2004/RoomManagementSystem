import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { registerLocaleData } from '@angular/common';
import localeVi from '@angular/common/locales/vi';
import { LOCALE_ID } from '@angular/core';

import { TenantManagementComponent } from './tenant-management.component';

// ✅ register locale TRƯỚC KHI chạy test
registerLocaleData(localeVi);

describe('TenantManagementComponent', () => {
  let component: TenantManagementComponent;
  let fixture: ComponentFixture<TenantManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        TenantManagementComponent,
        HttpClientTestingModule
      ],
      providers: [
        { provide: LOCALE_ID, useValue: 'vi' } // ✅ QUAN TRỌNG
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TenantManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
