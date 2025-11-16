import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { of } from 'rxjs';

import { RoomsComponent } from './rooms.component';
import { RoomService } from '../../services/room.service';
import { ProvinceService } from '../../services/province.service';

const MOCK_ROOMS = [
  { id: 1, name: 'Phòng 1', price: 100, createdAt: '2025-11-15' },
  { id: 2, name: 'Phòng 2', price: 200, createdAt: '2025-11-16' },
];
const MOCK_PROVINCES = [{ code: 79, name: 'TP. Hồ Chí Minh' }];
const MOCK_DISTRICTS = [{ code: 760, name: 'Quận 1' }];

@Component({
  selector: 'app-room-card',
  standalone: true,
  template: '<div class="mock-room-card"></div>',
})
class MockRoomCardComponent {
  @Input() room: any;
}

describe('RoomsComponent', () => {
  let component: RoomsComponent;
  let fixture: ComponentFixture<RoomsComponent>;
  let compiled: HTMLElement;
  let roomServiceSpy: jasmine.SpyObj<RoomService>;
  let provinceServiceSpy: jasmine.SpyObj<ProvinceService>;

  beforeEach(async () => {
    const roomSpy = jasmine.createSpyObj('RoomService', [
      'getAllRooms',
      'searchRooms',
      'filterRooms',
    ]);
    roomSpy.getAllRooms.and.returnValue(of(MOCK_ROOMS));
    roomSpy.searchRooms.and.returnValue(of([MOCK_ROOMS[0]]));
    roomSpy.filterRooms.and.returnValue(of(MOCK_ROOMS));

    const provinceSpy = jasmine.createSpyObj('ProvinceService', [
      'getAllProvinces',
      'getDistrictsByProvince',
    ]);
    provinceSpy.getAllProvinces.and.returnValue(of(MOCK_PROVINCES));
    provinceSpy.getDistrictsByProvince.and.returnValue(of(MOCK_DISTRICTS));

    await TestBed.configureTestingModule({
      imports: [
        RoomsComponent,
        HttpClientTestingModule,
        FormsModule,
      ],
      providers: [
        { provide: RoomService, useValue: roomSpy },
        { provide: ProvinceService, useValue: provinceSpy },
      ],
    })
      .overrideComponent(RoomsComponent, {
        set: {
          imports: [CommonModule, FormsModule, MockRoomCardComponent],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(RoomsComponent);
    component = fixture.componentInstance;
    compiled = fixture.nativeElement;
    roomServiceSpy = TestBed.inject(RoomService) as jasmine.SpyObj<RoomService>;
    provinceServiceSpy = TestBed.inject(ProvinceService) as jasmine.SpyObj<ProvinceService>;
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should load rooms and provinces on init', () => {
    fixture.detectChanges(); 
    
    expect(roomServiceSpy.getAllRooms).toHaveBeenCalled();
    expect(provinceServiceSpy.getAllProvinces).toHaveBeenCalled();
    expect(component.rooms.length).toBe(2);
    expect(component.provinces.length).toBe(1);
    expect(component.provinces[0].name).toBe('TP. Hồ Chí Minh');
  });

  it('should render room cards', () => {
    fixture.detectChanges();
    const roomCards = compiled.querySelectorAll('app-room-card');
    expect(roomCards.length).toBe(2);
  });

  it('should load districts when province changes', () => {
    fixture.detectChanges();
    
    component.selectedProvinceCode = '79';
    component.onProvinceChange();
    
    expect(provinceServiceSpy.getDistrictsByProvince).toHaveBeenCalledWith(79);
    expect(component.districts.length).toBe(1);
    expect(component.districts[0].name).toBe('Quận 1');
  });

  it('should call searchRooms when applyFilter is called with a keyword', () => {
    fixture.detectChanges();
    
    component.searchKeyword = 'Tìm phòng';
    component.applyFilter();
    
    expect(roomServiceSpy.searchRooms).toHaveBeenCalledWith('Tìm phòng');
    expect(roomServiceSpy.filterRooms).not.toHaveBeenCalled();
  });

  it('should call filterRooms when applyFilter is called without keyword', () => {
    fixture.detectChanges();
    
    component.searchKeyword = ''; 
    component.selectedPrice = '0-2000000';
    component.selectedAcreage = '15-25';
    component.applyFilter();
    
    const expectedFilters = {
      minPrice: 0,
      maxPrice: 2000000,
      minArea: 15,
      maxArea: 25,
    };

    expect(roomServiceSpy.searchRooms).not.toHaveBeenCalled();
    expect(roomServiceSpy.filterRooms).toHaveBeenCalledWith(expectedFilters);
  });

  it('should call loadAllRooms when clearFilters is called', () => {
    fixture.detectChanges();
    
    component.clearFilters();
    
    expect(roomServiceSpy.getAllRooms.calls.count()).toBe(2);
  });

  it('should sort rooms by price low-to-high', () => {
    component.rooms = [
      { price: 500, createdAt: '2025-11-15' },
      { price: 100, createdAt: '2025-11-16' },
    ];
    
    component.sortOption = 'Giá thấp đến cao';
    component.sortRooms();
    
    expect(component.rooms[0].price).toBe(100);
    expect(component.rooms[1].price).toBe(500);
  });
});