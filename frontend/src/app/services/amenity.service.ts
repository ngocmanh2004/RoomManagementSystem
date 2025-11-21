import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  getAmenities(): Observable<Amenity[]> {
    return this.http.get<Amenity[]>(this.api);
  }

  getAmenitiesByRoom(roomId: number): Observable<Amenity[]> {
    return this.http.get<Amenity[]>(`${this.api}/room/${roomId}`);
  }

  addAmenity(name: string): Observable<Amenity> {
    return this.http.post<Amenity>(this.api, { name });
  }
}