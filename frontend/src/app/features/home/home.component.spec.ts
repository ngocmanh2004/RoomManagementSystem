import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component, Input } from '@angular/core';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { HomeComponent } from './home.component';
import { RoomService } from '../../services/room.service';
import { ProvinceService } from '../../services/province.service';
import { RoomCardComponent } from '../../shared/components/room-card/room-card.component';

const MOCK_ROOMS = [
  { id: 1, name: 'Phòng 1', building: { address: 'Địa chỉ 1' } },
  { id: 2, name: 'Phòng 2', building: { address: 'Địa chỉ 2' } },
];
const MOCK_AMENITIES = [{ id: 1, name: 'Wifi' }];
const MOCK_PROVINCES = [{ code: 79, name: 'TP. Hồ Chí Minh' }];
const MOCK_DISTRICTS = [{ code: 760, name: 'Quận 1' }];

@Component({
  selector: 'app-room-card',
  standalone: true,
  template: '<div class="mock-room-card"></div>',
  inputs: ['room'],
})
class MockRoomCardComponent {}

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;
  let compiled: HTMLElement;
  let roomServiceSpy: jasmine.SpyObj<RoomService>;
  let provinceServiceSpy: jasmine.SpyObj<ProvinceService>;

  beforeEach(async () => {
    const roomSpy = jasmine.createSpyObj('RoomService', [
      'getAllRooms',
      'getAmenities',
      'filterRooms',
    ]);
    roomSpy.getAllRooms.and.returnValue(of(MOCK_ROOMS));
    roomSpy.getAmenities.and.returnValue(of(MOCK_AMENITIES));
    roomSpy.filterRooms.and.returnValue(of(MOCK_ROOMS));

    const provinceSpy = jasmine.createSpyObj('ProvinceService', [
      'getAllProvinces',
      'getDistrictsByProvince',
    ]);
    provinceSpy.getAllProvinces.and.returnValue(of(MOCK_PROVINCES));
    provinceSpy.getDistrictsByProvince.and.returnValue(of(MOCK_DISTRICTS));

    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        { provide: RoomService, useValue: roomSpy },
        { provide: ProvinceService, useValue: provinceSpy },
      ],
    })
    .overrideComponent(HomeComponent, {
      set: {
        imports: [CommonModule, FormsModule, MockRoomCardComponent]
      }
    })
    .compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    roomServiceSpy = TestBed.inject(RoomService) as jasmine.SpyObj<RoomService>;
    provinceServiceSpy = TestBed.inject(ProvinceService) as jasmine.SpyObj<ProvinceService>;
    
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load rooms, amenities, and provinces on init', () => {
    expect(roomServiceSpy.getAllRooms).toHaveBeenCalled();
    expect(roomServiceSpy.getAmenities).toHaveBeenCalled();
    expect(provinceServiceSpy.getAllProvinces).toHaveBeenCalled();
    expect(component.rooms.length).toBe(2);
    expect(component.provinces.length).toBe(1);
  });

  it('should render room cards', () => {
    const roomCards = compiled.querySelectorAll('app-room-card');
    expect(roomCards.length).toBe(2);
  });

  it('should load districts when a province is selected', () => {
    component.selectedProvinceCode = '79';
    component.onProvinceChange();
    fixture.detectChanges();

    expect(provinceServiceSpy.getDistrictsByProvince).toHaveBeenCalledWith(79);
    expect(component.districts.length).toBe(1);
    expect(component.districts[0].name).toBe('Quận 1');
  });

  it('should call filterRooms when onSearch is triggered', () => {
    component.selectedProvinceCode = '79';
    component.selectedDistrictCode = '760';
    component.onSearch();
    
    const expectedFilters = {
      provinceCode: 79,
      districtCode: 760
    };
    
    expect(roomServiceSpy.filterRooms).toHaveBeenCalledWith(expectedFilters);
  });

  it('should call getAllRooms when clearFilters is triggered', () => {
    component.clearFilters();
    expect(roomServiceSpy.getAllRooms.calls.count()).toBe(2);
  });
});