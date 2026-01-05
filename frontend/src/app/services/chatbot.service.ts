import { Injectable } from '@angular/core';
import { HttpClient, HttpBackend } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { switchMap, catchError } from 'rxjs/operators';
import { CHATBOT_DATA } from '../shared/components/chatbot/chatbot-data';
import { RoomService } from './room.service';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {
  private API_KEY = 'AIzaSyDH2OK8yL4sGSBSm0DCmcJ6jxkB2IMiDD4'; 
  
private URL = `https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=${this.API_KEY}`;
private cachedRooms: any[] | null = null;
  private cacheTime: number = 0;
  private CACHE_DURATION = 5 * 60 * 1000;

  private geminiClient: HttpClient;

  constructor(
    private http: HttpClient,
    private roomService: RoomService,
    handler: HttpBackend
  ) {
    this.geminiClient = new HttpClient(handler);
  }

  preloadCache(): void {
    console.log('Preloading room data...');
    this.roomService.getAllRoomsPaged(0, 100).subscribe({
      next: (response: any) => {
        const rooms = response.content || response.data || response || [];
        this.cachedRooms = rooms;
        this.cacheTime = Date.now();
        console.log('Preloaded', rooms?.length, 'phòng vào cache');
      },
      error: (err) => console.error('⚠️ Preload failed:', err)
    });
  }

  sendMessage(userPrompt: string, userName: string, userPhone: string): Observable<any> {
    const now = Date.now();
    if (this.cachedRooms && (now - this.cacheTime) < this.CACHE_DURATION) {
      console.log('Sử dụng cache');
      return this.buildPromptAndSend(this.cachedRooms, userPrompt, userName, userPhone);
    }

    console.log('Đang tải dữ liệu phòng...');
    return this.roomService.getAllRoomsPaged(0, 100).pipe(
      switchMap((response: any) => {
        const rooms = response.content || response.data || response || [];
        this.cachedRooms = rooms;
        this.cacheTime = now;
        console.log('Đã cache', rooms?.length, 'phòng');
        
        return this.buildPromptAndSend(rooms, userPrompt, userName, userPhone);
      }),
      catchError((error) => {
        console.error('Error:', error);
        return of({
          candidates: [{
            content: {
              parts: [{
                text: 'Xin lỗi Anh/Chị, em đang gặp chút trục trặc kỹ thuật. Vui lòng liên hệ hotline 0779 421 219 nhé! 🙏'
              }]
            }
          }]
        });
      })
    );
  }

  private buildPromptAndSend(rooms: any[], userPrompt: string, userName: string, userPhone: string): Observable<any> {
    console.log('📊 Số phòng trong cache:', rooms?.length);
    
    const filteredRooms = this.filterRelevantRooms(rooms, userPrompt);
    console.log('✅ Sau khi lọc:', filteredRooms.length, 'phòng');
    
    let roomsData = '\n\n=== DANH SÁCH PHÒNG PHÙ HỢP ===\n';
    
    if (filteredRooms.length > 0) {
      filteredRooms.forEach((room: any, index: number) => {
        const buildingName = room.buildingName || 'Chưa xác định';
        const address = room.buildingAddress || 'Chưa cập nhật';
        const price = room.price || 0;
        const area = room.area || room.acreage || 0;
        
        roomsData += `\n[${index + 1}] ${room.name} (ID: ${room.id})`;
        roomsData += `\n    Giá: ${price.toLocaleString('vi-VN')} VNĐ/tháng`;
        roomsData += `\n    Diện tích: ${area} m2`;
        roomsData += `\n    Trạng thái: ${room.status}`;
        roomsData += `\n    Dãy trọ: ${buildingName}`;
        roomsData += `\n    Địa chỉ: ${address}\n`;
      });
    } else {
      roomsData += '\n(Không tìm thấy phòng phù hợp với yêu cầu)\n';
    }

    const fullPrompt = `
Em là trợ lý tư vấn phòng trọ của TechRoom - hệ thống quản lý phòng trọ đa chủ trọ.

${CHATBOT_DATA}

${roomsData}

Thông tin khách hàng:
- Họ tên: ${userName}
- Số điện thoại: ${userPhone}

Khách vừa nói: "${userPrompt}"

Lưu ý quan trọng:
- Trả lời ngắn gọn, thân thiện, xưng "Em", gọi "Anh/Chị"
- KHÔNG chào lại, chỉ dùng "Dạ"
- DANH SÁCH TRÊN ĐÃ LỌC SẴN PHÒNG PHÙ HỢP - chỉ cần format HTML đẹp
- Format HTML (HIỂN THỊ TẤT CẢ các phòng trong danh sách):
  
  Dạ anh/chị, em tìm thấy phòng phù hợp:<br><br>
  🏠 <a href="/rooms/1" target="_blank" style="color:#667eea;text-decoration:none;font-weight:bold;">Phòng 101 - Dãy trọ Nguyễn Thái Học</a><br>
  - Địa chỉ: 69 Cần Vương, Quy Nhơn, Bình Định<br>
  - Giá thuê: 2.5 triệu/tháng - Diện tích: 20m² - Còn trống<br><br>
  
  Anh/chị vui lòng click vào tên phòng để xem chi tiết phòng ạ!

- QUAN TRỌNG: 
  * Link: <a href="/rooms/{ID}" target="_blank">...</a> với {ID} thật
  * Tên dãy trọ: PHẢI LẤY ĐÚNG từ "Dãy trọ:" trong danh sách
  * KHÔNG được tự bịa "Dãy 1", "Dãy 2" - PHẢI dùng tên thật từ data
- Chỉ giới thiệu phòng AVAILABLE (Còn trống)
- Giá hiển thị: "2.5 triệu" không phải "2.500.000đ"
`;

    const body = {
      contents: [{
        parts: [{ text: fullPrompt }]
      }]
    };

    console.log('Sending to Gemini, prompt length:', fullPrompt.length, 'chars');
    
    return this.geminiClient.post(this.URL, body).pipe(
      catchError((error) => {
        console.error('Gemini API Error:', error);
        console.error('Status:', error.status);
        console.error('URL:', this.URL);
        console.error('API Key:', this.API_KEY);
        if (error.error) {
          console.error('❌ Error detail:', JSON.stringify(error.error, null, 2));
        }
        
        let errorMessage = 'Xin lỗi Anh/Chị, em đang gặp trục trặc. Vui lòng liên hệ 0779 421 219!';
        
        if (error.status === 400) {
          errorMessage = 'Lỗi cấu hình API (Bad Request). Vui lòng kiểm tra API key và model!';
        } else if (error.status === 429) {
          errorMessage = 'Hệ thống đang quá tải, vui lòng thử lại sau 30 giây! 🙏';
        } else if (error.status === 404) {
          errorMessage = 'Lỗi kết nối AI (Model not found). Vui lòng kiểm tra lại cấu hình!';
        }
        
        return of({
          candidates: [{
            content: {
              parts: [{
                text: errorMessage
              }]
            }
          }]
        });
      })
    );
  }

  private filterRelevantRooms(rooms: any[], userPrompt: string): any[] {
    if (!rooms || rooms.length === 0) return [];

    const prompt = userPrompt.toLowerCase();
    let filtered = rooms.filter((r: any) => r.status === 'AVAILABLE');
    
    console.log('Filter Debug - Prompt:', prompt);
    console.log('AVAILABLE rooms:', filtered.length);

    const priceMatch = prompt.match(/(\d+)\s*(triệu|tr|trieu)/);
    if (priceMatch) {
      const priceValue = parseInt(priceMatch[1]) * 1000000;
      console.log('Price filter:', priceValue, 'VNĐ');
      console.log('Sample room prices:', filtered.slice(0, 3).map(r => r.price));
      
      if (prompt.includes('dưới') || prompt.includes('duoi') || prompt.includes('<')) {
        console.log('Filtering: price <', priceValue);
        filtered = filtered.filter((r: any) => (r.price || 0) < priceValue);
        console.log('After filter:', filtered.length, 'rooms');
      } else if (prompt.includes('trên') || prompt.includes('tren') || prompt.includes('>')) {
        filtered = filtered.filter((r: any) => (r.price || 0) > priceValue);
      } else if (prompt.includes('khoảng') || prompt.includes('tầm') || prompt.includes('khoang')) {
        const min = priceValue * 0.8;
        const max = priceValue * 1.2;
        filtered = filtered.filter((r: any) => {
          const price = r.price || 0;
          return price >= min && price <= max;
        });
      } else {
        const min = priceValue - 500000;
        const max = priceValue + 500000;
        filtered = filtered.filter((r: any) => {
          const price = r.price || 0;
          return price >= min && price <= max;
        });
      }
    }

    const areaMatch = prompt.match(/(\d+)\s*(m2|m²|met)/);
    if (areaMatch) {
      const areaValue = parseInt(areaMatch[1]);
      
      if (prompt.includes('dưới') || prompt.includes('duoi')) {
        filtered = filtered.filter((r: any) => (r.area || r.acreage || 0) < areaValue);
      } else if (prompt.includes('trên') || prompt.includes('tren') || prompt.includes('rộng')) {
        filtered = filtered.filter((r: any) => (r.area || r.acreage || 0) >= areaValue);
      }
    }

    const locationKeywords = ['gần', 'gan', 'ở', 'o', 'tại', 'tai', 'đường', 'duong', 'phòng nào ở', 'phong nao o'];
    const hasLocation = locationKeywords.some(k => prompt.includes(k));
    
    if (hasLocation) {
      console.log('📍 Location filter detected');
      
      const cityMap: { [key: string]: string[] } = {
        'hồ chí minh': ['hồ chí minh', 'ho chi minh', 'tp hcm', 'tphcm', 'hcm', 'sài gòn', 'saigon'],
        'hà nội': ['hà nội', 'ha noi', 'hanoi'],
        'đà nẵng': ['đà nẵng', 'da nang', 'danang'],
        'quy nhơn': ['quy nhơn', 'quy nhon', 'quynhon', 'bình định', 'binh dinh'],
        'cần thơ': ['cần thơ', 'can tho', 'cantho'],
        'nha trang': ['nha trang', 'khánh hòa', 'khanh hoa']
      };
      
      let locationMatched = false;
      
      for (const [city, variants] of Object.entries(cityMap)) {
        if (variants.some(v => prompt.includes(v))) {
          console.log(`📍 City detected: ${city}`);
          filtered = filtered.filter((r: any) => {
            const address = (r.buildingAddress || '').toLowerCase();
            return variants.some(v => address.includes(v));
          });
          locationMatched = true;
          break;
        }
      }
      
      if (!locationMatched) {
        const words = prompt.split(' ').filter((w: string) => w.length > 2);
        console.log('📍 Generic location search with words:', words);
        filtered = filtered.filter((r: any) => {
          const address = (r.buildingAddress || '').toLowerCase();
          return words.some((word: string) => address.includes(word));
        });
      }
      
      console.log('📍 After location filter:', filtered.length, 'rooms');
    }

    filtered.sort((a: any, b: any) => (a.price || 0) - (b.price || 0));

    return filtered.slice(0, 5);
  }
}