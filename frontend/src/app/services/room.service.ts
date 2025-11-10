import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RoomService {
  private readonly api = '/api';

  constructor(private http: HttpClient) {}

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


  filterRooms(filters: {
    area?: string;
    type?: string;
    minPrice?: number;
    maxPrice?: number;
    minArea?: number;
    maxArea?: number;
    amenities?: number[];
  }): Observable<any[]> {
    let params = new HttpParams();

    if (filters.area) params = params.set('area', filters.area);
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

  getAmenitiesByRoomId(roomId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/amenities/room/${roomId}`);
  }

  getAreas(): Observable<string[]> {
  return this.http.get<string[]>(`${this.api}/rooms/areas`);
}

}
