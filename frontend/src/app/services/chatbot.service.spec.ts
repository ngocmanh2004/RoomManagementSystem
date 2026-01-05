import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { ChatbotService } from './chatbot.service';
import { RoomService } from './room.service';
import { PageResponse } from '../models/page-response.model';

describe('ChatbotService - Sprint 3', () => {
  let service: ChatbotService;
  let httpMock: HttpTestingController;
  let mockRoomService: jasmine.SpyObj<RoomService>;

  const mockRooms = [
    {
      id: 1,
      name: 'Phòng 101',
      price: 2500000,
      area: 20,
      status: 'AVAILABLE',
      buildingName: 'Dãy trọ A',
      buildingAddress: '123 Nguyễn Huệ, Quy Nhơn, Bình Định'
    },
    {
      id: 2,
      name: 'Phòng 102',
      price: 3000000,
      area: 25,
      status: 'AVAILABLE',
      buildingName: 'Dãy trọ B',
      buildingAddress: '456 Trần Phú, Quy Nhơn, Bình Định'
    },
    {
      id: 3,
      name: 'Phòng 201',
      price: 4000000,
      area: 30,
      status: 'OCCUPIED',
      buildingName: 'Dãy trọ A',
      buildingAddress: '123 Nguyễn Huệ, Quy Nhơn, Bình Định'
    }
  ];

  beforeEach(() => {
    mockRoomService = jasmine.createSpyObj('RoomService', ['getAllRoomsPaged']);

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        ChatbotService,
        { provide: RoomService, useValue: mockRoomService }
      ]
    });

    service = TestBed.inject(ChatbotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(service).toBeTruthy();
  });

  // ========== US 15.1: Chatbot tư vấn phòng trọ ==========
  describe('US 15.1: Chatbot tư vấn phòng trọ', () => {
    
    // TEST 1: Load và cache danh sách phòng
    it('should load and cache room data', () => {
      const mockResponse: PageResponse<any> = {
        content: mockRooms,
        number: 0,
        size: 100,
        totalElements: mockRooms.length,
        totalPages: 1,
        last: true
      };
      mockRoomService.getAllRoomsPaged.and.returnValue(of(mockResponse));

      service.preloadCache();

      expect(mockRoomService.getAllRoomsPaged).toHaveBeenCalledWith(0, 100);
    });

    // TEST 2: Sử dụng cache nếu còn hạn (không gọi API lại)
    it('should use cached data if available and not expired', (done) => {
      service['cachedRooms'] = mockRooms;
      service['cacheTime'] = Date.now();

      service.sendMessage('Tìm phòng', 'Test User', '0912345678').subscribe(() => {
        expect(mockRoomService.getAllRoomsPaged).not.toHaveBeenCalled();
        done();
      });

      const req = httpMock.expectOne((request) => 
        request.url.includes('generativelanguage.googleapis.com')
      );
      req.flush({
        candidates: [{ content: { parts: [{ text: 'Response' }] } }]
      });
    });

    // TEST 3: Lọc phòng theo giá (dưới X triệu)
    it('should filter rooms by price (under)', () => {
      const filtered = service['filterRelevantRooms'](
        mockRooms,
        'Tìm phòng dưới 3 triệu'
      );

      expect(filtered.length).toBe(1);
      expect(filtered[0].price).toBeLessThan(3000000);
    });

    // TEST 4: Lọc phòng theo diện tích
    it('should filter rooms by area', () => {
      const filtered = service['filterRelevantRooms'](
        mockRooms,
        'Phòng rộng trên 20m2'
      );

      expect(filtered.every(r => r.area >= 20)).toBe(true);
    });

    // TEST 5: Lọc phòng theo địa điểm
    it('should filter rooms by location', () => {
      const filtered = service['filterRelevantRooms'](
        mockRooms,
        'Phòng ở Quy Nhơn'
      );

      expect(filtered.every(r => 
        r.buildingAddress.toLowerCase().includes('quy nhơn')
      )).toBe(true);
    });

    // TEST 6: Chỉ hiển thị phòng AVAILABLE
    it('should only show AVAILABLE rooms', () => {
      const filtered = service['filterRelevantRooms'](
        mockRooms,
        'Tìm phòng'
      );

      expect(filtered.every(r => r.status === 'AVAILABLE')).toBe(true);
    });

    // TEST 7: Giới hạn kết quả tối đa 5 phòng
    it('should limit results to 5 rooms', () => {
      const manyRooms = Array.from({ length: 20 }, (_, i) => ({
        id: i + 1,
        name: `Phòng ${i + 1}`,
        price: 2000000 + (i * 100000),
        area: 20,
        status: 'AVAILABLE',
        buildingName: 'Dãy trọ Test',
        buildingAddress: 'Test Address'
      }));

      const filtered = service['filterRelevantRooms'](manyRooms, 'Tìm phòng');

      expect(filtered.length).toBeLessThanOrEqual(5);
    });

    // TEST 8: Sắp xếp phòng theo giá tăng dần
    it('should sort rooms by price', () => {
      const filtered = service['filterRelevantRooms'](mockRooms, 'Tìm phòng');

      for (let i = 1; i < filtered.length; i++) {
        expect(filtered[i].price).toBeGreaterThanOrEqual(filtered[i - 1].price);
      }
    });
  });

  // ========== US 15.2: Chatbot hướng dẫn sử dụng hệ thống ==========
  describe('US 15.2: Chatbot hướng dẫn sử dụng', () => {
    
    // TEST 9: Gửi câu hỏi hướng dẫn đến Gemini API
    it('should send system guide questions to Gemini', (done) => {
      service['cachedRooms'] = [];
      service['cacheTime'] = Date.now();

      const userPrompt = 'Làm sao để đăng ký chủ trọ?';
      const userName = 'Test User';
      const userPhone = '0912345678';

      service.sendMessage(userPrompt, userName, userPhone).subscribe(response => {
        expect(response.candidates).toBeDefined();
        done();
      });

      const req = httpMock.expectOne((request) => 
        request.url.includes('generativelanguage.googleapis.com')
      );
      
      expect(req.request.body.contents[0].parts[0].text).toContain(userPrompt);
      expect(req.request.body.contents[0].parts[0].text).toContain(userName);

      req.flush({
        candidates: [{ 
          content: { 
            parts: [{ 
              text: 'Để đăng ký chủ trọ, anh/chị vui lòng...' 
            }] 
          } 
        }]
      });
    });

    // TEST 10: Include CHATBOT_DATA trong prompt (hướng dẫn hệ thống)
    it('should include CHATBOT_DATA in prompt for system guidance', (done) => {
      service['cachedRooms'] = [];
      service['cacheTime'] = Date.now();

      service.sendMessage('Cách thanh toán hóa đơn?', 'Test', '0912345678')
        .subscribe(() => done());

      const req = httpMock.expectOne((request) => 
        request.url.includes('generativelanguage.googleapis.com')
      );

      const requestBody = req.request.body.contents[0].parts[0].text;
      expect(requestBody).toContain('TechRoom');

      req.flush({
        candidates: [{ content: { parts: [{ text: 'Hướng dẫn thanh toán...' }] } }]
      });
    });
  });

  // ========== Xử lý lỗi ==========
  describe('Error Handling', () => {
    beforeEach(() => {
      service['cachedRooms'] = mockRooms;
      service['cacheTime'] = Date.now();
    });

    // TEST 11: Xử lý lỗi API gracefully
    it('should handle API error gracefully', (done) => {
      service.sendMessage('Test', 'User', '0912345678').subscribe(response => {
        expect(response.candidates[0].content.parts[0].text)
          .toContain('trục trặc');
        done();
      });

      const req = httpMock.expectOne((request) => 
        request.url.includes('generativelanguage.googleapis.com')
      );
      req.error(new ErrorEvent('Network error'));
    });

    // TEST 12: Xử lý khi không tìm thấy phòng phù hợp
    it('should handle no matching rooms', (done) => {
      service['cachedRooms'] = mockRooms;
      service['cacheTime'] = Date.now();

      service.sendMessage('Phòng dưới 1 triệu', 'Test', '0912345678')
        .subscribe(() => done());

      const req = httpMock.expectOne((request) => 
        request.url.includes('generativelanguage.googleapis.com')
      );

      const promptText = req.request.body.contents[0].parts[0].text;
      expect(promptText).toContain('Không tìm thấy phòng phù hợp');

      req.flush({
        candidates: [{ content: { parts: [{ text: 'Không có phòng phù hợp' }] } }]
      });
    });
  });
});

