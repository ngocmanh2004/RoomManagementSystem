import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Building } from '../models/building.model';

export type { Building };

@Injectable({
  providedIn: 'root'
})
export class BuildingService {
  private readonly api = '/api/buildings';

  constructor(private http: HttpClient) { }

  getAllBuildings(): Observable<Building[]> {
    return this.http.get<Building[]>(this.api);
  }

  getBuildingById(id: number): Observable<Building> {
    return this.http.get<Building>(`${this.api}/${id}`);
  }

  getBuildingsByLandlord(landlordId: number): Observable<Building[]> {
    return this.http.get<Building[]>(`${this.api}/by-landlord/${landlordId}`);
  }
}