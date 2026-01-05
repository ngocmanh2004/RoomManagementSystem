import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer } from '@angular/platform-browser';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { RoomDetailComponent } from './room-detail.component';
import { RoomService } from '../../services/room.service';
import { AmenityService } from '../../services/amenity.service';
import { AuthService } from '../../services/auth.service';
import { Room } from '../../models/room.model';

describe('RoomDetailComponent - Sprint 2', () => {
  let component: RoomDetailComponent;
  let fixture: ComponentFixture<RoomDetailComponent>;
  let mockRoomService: jasmine.SpyObj<RoomService>;
  let mockAmenityService: jasmine.SpyObj<AmenityService>;
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockActivatedRoute: any;
  let mockSanitizer: jasmine.SpyObj<DomSanitizer>;

  const mockRoom: Room = {
    id: 1,
    name: 'Phòng 101',
    price: 3000000,
    area: 25,
    address: '123 Nguyễn Huệ, Quy Nhơn',
    description: 'Phòng đẹp, đầy đủ tiện nghi',
    maxOccupants: 2,
    status: 'AVAILABLE',
    images: [
      { id: 1, imageUrl: 'image1.jpg', roomId: 1 },
      { id: 2, imageUrl: 'image2.jpg', roomId: 1 }
    ],
    building: {
      id: 1,
      name: 'Dãy trọ A',
      address: '123 Nguyễn Huệ, Quy Nhơn, Bình Định'
    }
  } as any;

  beforeEach(async () => {
    mockRoomService = jasmine.createSpyObj('RoomService', ['getRoomById']);
    mockAmenityService = jasmine.createSpyObj('AmenityService', ['getAmenitiesByRoom']);
    mockAuthService = jasmine.createSpyObj('AuthService', ['isLoggedIn', 'getUserRole']);
    mockSanitizer = jasmine.createSpyObj('DomSanitizer', ['bypassSecurityTrustResourceUrl']);
    
    mockActivatedRoute = {
      params: of({ id: '1' })
    };

    await TestBed.configureTestingModule({
      imports: [RoomDetailComponent, HttpClientTestingModule],
      providers: [
        { provide: RoomService, useValue: mockRoomService },
        { provide: AmenityService, useValue: mockAmenityService },
        { provide: AuthService, useValue: mockAuthService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        { provide: DomSanitizer, useValue: mockSanitizer }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RoomDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ========== US 8.6: Xem vị trí phòng trên Google Map ==========
  describe('US 8.6: Xem vị trí phòng trên Google Map', () => {
    
    // TEST 1: Load thông tin phòng và hiển thị map
    it('should load room and display Google Map', () => {
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.ngOnInit();
      
      expect(mockRoomService.getRoomById).toHaveBeenCalledWith(1);
      expect(component.room).toEqual(mockRoom);
      expect(component.mapsEmbedUrl).toContain('google.com/maps');
      expect(component.mapsEmbedUrl).toContain(encodeURIComponent('123 Nguyễn Huệ, Quy Nhơn, Bình Định'));
    });

    // TEST 2: Tạo URL Google Maps từ địa chỉ phòng
    it('should create Google Maps URL from room address', () => {
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.loadRoomDetail();
      
      expect(component.mapsEmbedUrl).toBeTruthy();
      expect(component.mapsEmbedUrl).toContain('https://www.google.com/maps?q=');
      expect(component.mapsEmbedUrl).toContain('&output=embed');
    });

    // TEST 3: Sanitize Google Maps URL để embed an toàn
    it('should sanitize Google Maps URL for safe embedding', () => {
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.loadRoomDetail();
      
      expect(mockSanitizer.bypassSecurityTrustResourceUrl).toHaveBeenCalled();
      expect(component.mapsUrl).toBe('safe-url' as any);
    });

    // TEST 4: Xử lý khi phòng không có địa chỉ
    it('should handle room without address', () => {
      const roomWithoutAddress = { ...mockRoom, building: null };
      mockRoomService.getRoomById.and.returnValue(of(roomWithoutAddress as any));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      
      component.loadRoomDetail();
      
      expect(component.mapsUrl).toBeNull();
    });
  });

  // ========== Load Room Details ==========
  describe('Load Room Details', () => {
    
    // TEST 5: Load chi tiết phòng thành công
    it('should load room details successfully', () => {
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.roomId = 1;
      component.loadRoomDetail();
      
      expect(component.room).toEqual(mockRoom);
      expect(component.isLoading).toBe(false);
      expect(component.error).toBe('');
    });

    // TEST 6: Hiển thị lỗi khi load phòng fail
    it('should show error when loading room fails', () => {
      mockRoomService.getRoomById.and.returnValue(
        throwError(() => new Error('Not found'))
      );
      
      component.roomId = 1;
      component.loadRoomDetail();
      
      expect(component.error).toBe('Không thể tải thông tin phòng');
      expect(component.isLoading).toBe(false);
    });

    // TEST 7: Hiển thị ảnh đầu tiên làm ảnh chính
    it('should display first image as main image', () => {
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.loadRoomDetail();
      
      expect(component.currentMainImage).toContain('image1.jpg');
    });

    // TEST 8: Mở modal đặt phòng
    it('should open booking modal', () => {
      spyOn(window, 'alert');
      mockAuthService.isLoggedIn.and.returnValue(true);
      mockAuthService.getUserRole.and.returnValue(1);
      component.openBookingModal();
      
      expect(component.isBookingModalOpen).toBe(true);
    });

    // TEST 9: Đóng modal đặt phòng
    it('should close booking modal', () => {
      component.isBookingModalOpen = true;
      
      component.closeBookingModal();
      
      expect(component.isBookingModalOpen).toBe(false);
    });
  });

  // ========== Additional Feature Tests ==========
  describe('Additional Features: Amenities & Image Gallery', () => {
    
    // TEST 10: Load danh sách tiện ích của phòng
    it('should load room amenities', () => {
      const mockAmenities = [
        { id: 1, name: 'Wifi', icon: 'wifi', roomId: 1 },
        { id: 2, name: 'Điều hòa', icon: 'ac', roomId: 1 }
      ];
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of(mockAmenities as any));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.loadRoomDetail();
      
      expect(mockAmenityService.getAmenitiesByRoom).toHaveBeenCalledWith(1);
      expect(component.amenities.length).toBe(2);
    });

    // TEST 11: Navigate through image gallery
    it('should navigate to next image in gallery', () => {
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      component.loadRoomDetail();
      
      component.currentImageIndex = 0;
      const mockEvent = new Event('click');
      component.nextImage(mockEvent);
      
      expect(component.currentImageIndex).toBe(1);
      expect(component.currentMainImage).toContain('image2.jpg');
    });

    // TEST 12: Navigate to previous image in gallery
    it('should navigate to previous image in gallery', () => {
      component.roomId = 1;
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      component.loadRoomDetail();
      
      component.currentImageIndex = 1;
      const mockEvent = new Event('click');
      component.prevImage(mockEvent);
      
      expect(component.currentImageIndex).toBe(0);
      expect(component.currentMainImage).toContain('image1.jpg');
    });

    // TEST 13: Handle empty amenities list
    it('should handle empty amenities list gracefully', () => {
      mockRoomService.getRoomById.and.returnValue(of(mockRoom));
      mockAmenityService.getAmenitiesByRoom.and.returnValue(of([]));
      mockSanitizer.bypassSecurityTrustResourceUrl.and.returnValue('safe-url' as any);
      
      component.loadRoomDetail();
      
      expect(component.amenities.length).toBe(0);
      expect(component.room).toBeDefined();
    });
  });
});
