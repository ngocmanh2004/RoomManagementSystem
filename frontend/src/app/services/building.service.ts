import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Tạo interface để code an toàn hơn
export interface Building {
  id: number;
  name: string;
  address: string;
}

@Injectable({
  providedIn: 'root'
})
export class BuildingService {
  private readonly api = '/api/buildings';

  constructor(private http: HttpClient) { }

  getAllBuildings(): Observable<Building[]> {
    return this.http.get<Building[]>(this.api);
  }

  getBuildingsByLandlord(landlordId: number): Observable<Building[]> {
    return this.http.get<Building[]>(`${this.api}/by-landlord/${landlordId}`);
  }
}