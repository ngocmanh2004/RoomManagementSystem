// src/app/services/room.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly api = '/api';

  constructor(private http: HttpClient) {}

  // ... (các hàm getAllRooms, getAmenities, searchRooms vẫn giữ nguyên) ...

  getAllRooms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/rooms`);
  }

  getAmenities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/amenities`);
  }

  searchRooms(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/rooms/search`, {
      params: { keyword }
    });
  }

  /**
   * SỬA LẠI HÀM NÀY
   */
  filterRooms(filters: {
    provinceCode?: number; // Sửa từ area
    districtCode?: number; // Thêm mới
    type?: string;
    minPrice?: number;
    maxPrice?: number;
    minArea?: number;
    maxArea?: number;
    amenities?: number[];
  }): Observable<any[]> {
    let params = new HttpParams();

    // Sửa logic params
    if (filters.provinceCode) params = params.set('provinceCode', filters.provinceCode);
    if (filters.districtCode) params = params.set('districtCode', filters.districtCode);
    
    // Các filter cũ giữ nguyên
    if (filters.type) params = params.set('type', filters.type);
    if (filters.minPrice != null)
      params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice != null)
      params = params.set('maxPrice', filters.maxPrice);
    if (filters.minArea != null)
      params = params.set('minArea', filters.minArea);
    if (filters.maxArea != null)
      params = params.set('maxArea', filters.maxArea);
    if (filters.amenities?.length) {
      params = params.set('amenities', filters.amenities.join(','));
    }

    return this.http.get<any[]>(`${this.api}/rooms/filter`, { params });
  }

  getRoomById(id: number): Observable<any> {
    return this.http.get<any>(`${this.api}/rooms/${id}`);
  }

  // ... (các hàm còn lại giữ nguyên) ...
  getAmenitiesByRoomId(roomId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/amenities/room/${roomId}`);
  }

  // API này đã bị xóa ở backend, nhưng nếu bạn vẫn cần thì
  // hãy báo tôi, chúng ta sẽ tạo lại
  getAreas(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/rooms/areas`);
  }
}