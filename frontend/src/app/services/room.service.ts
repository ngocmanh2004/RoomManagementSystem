// Removed duplicate getRoomsByLandlord definition accidentally placed outside the class
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Amenity } from './amenity.service';
import { PageResponse } from '../models/page-response.model';

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
  amenities?: Amenity[];
}

export interface RoomImage {
  id: number;
  imageUrl: string;
}

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly apiBase = '/api';

  constructor(private http: HttpClient) { }

  getAllRooms(): Observable<Room[]> {
    return this.http.get<any>(`${this.apiBase}/rooms`).pipe(
      map((res: any) => {
        // Backend can return PageResponse or Array
        if (res.content && Array.isArray(res.content)) {
          return res.content; // PageResponse format
        }
        return Array.isArray(res) ? res : [];
      })
    );
  }

  getAllRoomsPaged(page: number = 0, size: number = 10): Observable<PageResponse<Room>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Room>>(`${this.apiBase}/rooms`, { params });
  }

  getAmenities(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/amenities`);
  }

  searchRooms(keyword: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/rooms/search`, {
      params: { keyword },
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

    if (filters.provinceCode)
      params = params.set('provinceCode', filters.provinceCode);
    if (filters.districtCode)
      params = params.set('districtCode', filters.districtCode);
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

  getRoomById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiBase}/rooms/${id}`);
  }

  getAmenitiesByRoom(roomId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiBase}/amenities/room/${roomId}`);
  }

  getAreas(): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiBase}/rooms/areas`);
  }

  addRoom(room: Omit<Room, 'id'>): Observable<Room> {
    return this.http.post<Room>(`${this.apiBase}/rooms`, room);
  }

  updateRoom(id: number, room: Room): Observable<Room> {
    return this.http.put<Room>(`${this.apiBase}/rooms/${id}`, room);
  }

  updateRoomStatus(id: number, status: RoomStatus): Observable<Room> {
    return this.http.patch<Room>(`${this.apiBase}/rooms/${id}/status`, {
      status,
    });
  }

  deleteRoom(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/rooms/${id}`);
  }

  uploadImages(roomId: number, files: File[]): Observable<RoomImage[]> {
    const formData = new FormData();
    for (const file of files) {
      formData.append('files', file);
    }

    return this.http.post<RoomImage[]>(
      `${this.apiBase}/images/room/${roomId}`,
      formData
    );
  }

  deleteImage(imageId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiBase}/images/${imageId}`);
  }

  getRoomsByLandlord(landlordId: number): Observable<Room[]> {
    return this.http.get<Room[]>(
      `${this.apiBase}/rooms/by-landlord/${landlordId}`
    );
  }

  getMyRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.apiBase}/rooms/my`);
  }

  getRoomsByBuilding(buildingId: number): Observable<Room[]> {
    return this.http.get<Room[]>(`${this.apiBase}/rooms/by-building/${buildingId}`);
  }
}
