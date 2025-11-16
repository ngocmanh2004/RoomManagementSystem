import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Amenity } from './amenity.service';

// THÊM CÁC INTERFACE MỚI (ĐỂ CODE SẠCH HƠN)
export type RoomStatus = 'AVAILABLE' | 'OCCUPIED' | 'REPAIRING';

export interface Room {
  id?: number;
  name: string;
  price: number;
  area: number;
  status: RoomStatus;
  description?: string;
  building?: any; 
  images?: any[];
  tenantName?: string;
  amenities?: Amenity[]; // Dùng cho UI
}

export interface RoomImage {
  id: number;
  imageUrl: string;
}

@Injectable({ providedIn: 'root' })
export class RoomService {
  // Sửa private readonly api = '/api'; thành base path
  private readonly apiBase = '/api'; 

  constructor(private http: HttpClient) {}

  // ===========================================
  // CÁC HÀM CŨ (GIỮ NGUYÊN)
  // ===========================================
  getAllRooms(): Observable<Room[]> { // Sửa lại kiểu trả về
    return this.http.get<Room[]>(`${this.apiBase}/rooms`);
  }

  getAmenities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/amenities`);
  }

  searchRooms(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/rooms/search`, {
      params: { keyword }
    });
  }

  filterRooms(filters: {
    provinceCode?: number;
    districtCode?: number;
    type?: string;
    minPrice?: number;
    maxPrice?: number;
    minArea?: number;
    maxArea?: number;
    amenities?: number[];
  }): Observable<any[]> {
    let params = new HttpParams();

    if (filters.provinceCode) params = params.set('provinceCode', filters.provinceCode);
    if (filters.districtCode) params = params.set('districtCode', filters.districtCode);
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

    return this.http.get<any[]>(`${this.apiBase}/rooms/filter`, { params });
  }

  getRoomById(id: number): Observable<any> { // Giữ nguyên kiểu 'any'
    return this.http.get<any>(`${this.apiBase}/rooms/${id}`);
  }
  getAmenitiesByRoomId(roomId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/amenities/room/${roomId}`);
  }

  getAreas(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiBase}/rooms/areas`);
  }

  // ===========================================
  // THÊM CÁC HÀM MỚI CHO SPRINT 1
  // ===========================================
  
  /**
   * US 1.1: Thêm phòng trọ
   */
  addRoom(room: Omit<Room, 'id'>): Observable<Room> {
    return this.http.post<Room>(`${this.apiBase}/rooms`, room);
  }

  /**
   * US 1.2: Chỉnh sửa phòng trọ
   */
  updateRoom(id: number, room: Room): Observable<Room> {
    return this.http.put<Room>(`${this.apiBase}/rooms/${id}`, room);
  }

  /**
   * US 1.4: Cập nhật trạng thái
   */
  updateRoomStatus(id: number, status: RoomStatus): Observable<Room> {
    return this.http.patch<Room>(`${this.apiBase}/rooms/${id}/status`, { status });
  }

  /**
   * US 1.3: Xóa phòng trọ
   */
  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/rooms/${id}`);
  }

  /**
   * Tải lên danh sách file ảnh cho một phòng
   */
  uploadImages(roomId: number, files: File[]): Observable<RoomImage[]> {
    const formData = new FormData();
    for (const file of files) {
      formData.append('files', file); // 'files' phải khớp với @RequestParam("files")
    }

    // Gửi dưới dạng multipart/form-data
    return this.http.post<RoomImage[]>(`${this.apiBase}/images/room/${roomId}`, formData);
  }

  /**
   * Xóa một ảnh
   */
  deleteImage(imageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/images/${imageId}`);
  }
}