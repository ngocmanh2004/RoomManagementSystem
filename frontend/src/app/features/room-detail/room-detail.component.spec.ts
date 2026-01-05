import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomDetailComponent } from './room-detail.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('RoomDetailComponent', () => {
  let component: RoomDetailComponent;
  let fixture: ComponentFixture<RoomDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomDetailComponent, HttpClientTestingModule, RouterTestingModule]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
