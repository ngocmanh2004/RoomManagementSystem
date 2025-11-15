import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Tạo interface để code an toàn hơn
export interface Amenity {
  id: number;
  name: string;
}

@Injectable({
  providedIn: 'root'
})
export class AmenityService {
  private readonly api = '/api/amenities';

  constructor(private http: HttpClient) { }

  /** Lấy tất cả tiện ích có sẵn */
  getAmenities(): Observable<Amenity[]> {
    return this.http.get<Amenity[]>(this.api);
  }

  /** Thêm một tiện ích mới vào CSDL (cho nút "+") */
  addAmenity(name: string): Observable<Amenity> {
    return this.http.post<Amenity>(this.api, { name });
  }
}