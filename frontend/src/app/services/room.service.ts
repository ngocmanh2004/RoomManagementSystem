import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoomService {
  private apiUrl = '/api/rooms'; 

  constructor(private http: HttpClient) {}

  getAllRooms(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}`);
  }

  searchRooms(filters: any): Observable<any[]> {
    let params = new HttpParams();

    if (filters.area) params = params.set('area', filters.area);
    if (filters.type) params = params.set('type', filters.type);
    if (filters.priceRange) params = params.set('priceRange', filters.priceRange);
    if (filters.acreage) params = params.set('acreage', filters.acreage);

    return this.http.get<any[]>(`${this.apiUrl}/search`, { params });
  }

  filterRooms(filters: any): Observable<any[]> {
    let params = new HttpParams();

    if (filters.minPrice) params = params.set('minPrice', filters.minPrice);
    if (filters.maxPrice) params = params.set('maxPrice', filters.maxPrice);
    if (filters.type) params = params.set('type', filters.type);
    if (filters.area) params = params.set('area', filters.area);
    if (filters.amenities && filters.amenities.length > 0) {
      params = params.set('amenities', filters.amenities.join(','));
    }

    return this.http.get<any[]>(`${this.apiUrl}/filter`, { params });
  }

  getRoomById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  getAmenities(): Observable<any[]> {
    return this.http.get<any[]>('/api/amenities');
  }
}
