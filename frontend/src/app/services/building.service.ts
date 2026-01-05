import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Building } from '../models/building.model';
import { PageResponse } from '../models/page-response.model';
import { Room } from '../models/room.model';

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

  getAllBuildingsPaged(page: number = 0, size: number = 5): Observable<PageResponse<Building>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Building>>(this.api, { params });
  }

  searchBuildings(params: {
    provinceCode?: string;
    districtCode?: string;
    minPrice?: number;
    maxPrice?: number;
    minAcreage?: number;
    maxAcreage?: number;
  }): Observable<Building[]> {
    const queryParams: any = {};
    
    if (params.provinceCode) queryParams.provinceCode = params.provinceCode;
    if (params.districtCode) queryParams.districtCode = params.districtCode;
    if (params.minPrice !== undefined) queryParams.minPrice = params.minPrice;
    if (params.maxPrice !== undefined) queryParams.maxPrice = params.maxPrice;
    if (params.minAcreage !== undefined) queryParams.minAcreage = params.minAcreage;
    if (params.maxAcreage !== undefined) queryParams.maxAcreage = params.maxAcreage;

    return this.http.get<Building[]>(`${this.api}/search`, { params: queryParams });
  }

  getBuildingById(id: number): Observable<Building> {
    return this.http.get<Building>(`${this.api}/${id}`);
  }

  getBuildingsByLandlord(landlordId: number): Observable<Building[]> {
    return this.http.get<Building[]>(`${this.api}/by-landlord/${landlordId}`);
  }

  getRoomsByBuildingPaged(buildingId: number, page: number = 0, size: number = 8): Observable<PageResponse<Room>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Room>>(`${this.api}/${buildingId}/rooms`, { params });
  }

  createBuilding(building: Partial<Building>): Observable<Building> {
    return this.http.post<Building>(this.api, building);
  }

  updateBuilding(id: number, building: Partial<Building>): Observable<Building> {
    return this.http.put<Building>(`${this.api}/${id}`, building);
  }

  deleteBuilding(id: number): Observable<any> {
    return this.http.delete(`${this.api}/${id}`);
  }

  uploadBuildingImage(buildingId: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(`${this.api}/${buildingId}/image`, formData);
  }

  deleteBuildingImage(buildingId: number): Observable<any> {
    return this.http.delete(`${this.api}/${buildingId}/image`);
  }
}