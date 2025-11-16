import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { RoomDetailComponent } from './room-detail.component';
import { RoomService } from '../../services/room.service';
import { RouterTestingModule } from '@angular/router/testing';
import { CommonModule } from '@angular/common';

const MOCK_ROOM = {
  id: 1,
  name: 'Phòng trọ cao cấp Quận 7',
  price: 4500000,
  area: 25,
  status: 'AVAILABLE',
  description: 'Mô tả chi tiết',
  building: { address: '123 Đường ABC, Quận 7' },
  images: [{ imageUrl: 'image1.jpg' }],
  createdAt: new Date().toISOString(),
};

const MOCK_AMENITIES = [
  { id: 1, name: 'Điều hòa' },
  { id: 2, name: 'Wifi' },
];

describe('RoomDetailComponent', () => {
  let component: RoomDetailComponent;
  let fixture: ComponentFixture<RoomDetailComponent>;
  let compiled: HTMLElement;
  let roomServiceSpy: jasmine.SpyObj<RoomService>;

  const mockActivatedRoute = {
    snapshot: {
      paramMap: {
        get: (key: string) => '1',
      },
    },
  };

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('RoomService', [
      'getRoomById',
      'getAmenitiesByRoomId',
    ]);
    spy.getRoomById.and.returnValue(of(MOCK_ROOM));
    spy.getAmenitiesByRoomId.and.returnValue(of(MOCK_AMENITIES));

    await TestBed.configureTestingModule({
      imports: [RoomDetailComponent, RouterTestingModule.withRoutes([])],
      providers: [
        { provide: RoomService, useValue: spy },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
      ],
    })
    .overrideComponent(RoomDetailComponent, {
        set: { imports: [CommonModule, RouterTestingModule] }
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomDetailComponent);
    component = fixture.componentInstance;
    roomServiceSpy = TestBed.inject(RoomService) as jasmine.SpyObj<RoomService>;
    
    fixture.detectChanges();
    compiled = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fetch room and amenities on init', () => {
    expect(roomServiceSpy.getRoomById).toHaveBeenCalledWith(1);
    expect(roomServiceSpy.getAmenitiesByRoomId).toHaveBeenCalledWith(1);
  });

  it('should render room name and address', () => {
    const titleElement = compiled.querySelector('h1');
    const addressElement = compiled.querySelector('p.address');
    expect(titleElement?.textContent).toContain(MOCK_ROOM.name);
    expect(addressElement?.textContent).toContain(MOCK_ROOM.building.address);
  });

  it('should render room price and area', () => {
    const infoBarElements = compiled.querySelectorAll('.info-bar strong');
    expect(infoBarElements[0]?.textContent).toContain('4,500,000');
    expect(infoBarElements[1]?.textContent).toContain('25 m²');
  });

  it('should render amenities list', () => {
    const amenityItems = compiled.querySelectorAll('.amenities-list li');
    expect(amenityItems.length).toBe(2);
    expect(amenityItems[0]?.textContent).toContain('Điều hòa');
    expect(amenityItems[1]?.textContent).toContain('Wifi');
  });
});