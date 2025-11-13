import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tenant } from '../models/tenant.model'; 

@Injectable({ providedIn: 'root' })
export class TenantService {
  private apiUrl = 'http://localhost:8081/api/tenants'; 

  constructor(private http: HttpClient) {}

  getAll(): Observable<Tenant[]> { 
    return this.http.get<Tenant[]>(this.apiUrl);
  }

  getById(id: number): Observable<Tenant> { 
    return this.http.get<Tenant>(`${this.apiUrl}/${id}`);
  }

  getTenantByUserId(userId: number): Observable<Tenant> {
    return this.http.get<Tenant>(`${this.apiUrl}/user/${userId}`);
  }

  add(tenant: any): Observable<Tenant> { 
    return this.http.post<Tenant>(this.apiUrl, tenant);
  }

  update(id: number, tenant: any): Observable<Tenant> { 
    return this.http.put<Tenant>(`${this.apiUrl}/${id}`, tenant);
  }

  delete(id: number): Observable<any> { 
    return this.http.delete<any>(`${this.apiUrl}/${id}`);
  }
}