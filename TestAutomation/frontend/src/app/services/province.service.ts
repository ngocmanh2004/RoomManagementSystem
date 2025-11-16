// src/app/services/province.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Province, District } from '../models/province.model'; // Giữ nguyên model

@Injectable({ providedIn: 'root' })
export class ProvinceService {
  
  // Trỏ thẳng đến backend của chúng ta
  private readonly provinceApi = '/api/provinces';
  private readonly districtApi = '/api/districts';

  constructor(private http: HttpClient) {}

  /**
   * API mới: Lấy TẤT CẢ Tỉnh/Thành
   */
  getAllProvinces(): Observable<Province[]> {
    return this.http.get<Province[]>(this.provinceApi);
  }

  /**
   * API mới: Lấy Quận/Huyện THEO MÃ Tỉnh/Thành
   */
  getDistrictsByProvince(provinceCode: number): Observable<District[]> {
    return this.http.get<District[]>(`${this.districtApi}/by-province/${provinceCode}`);
  }
}