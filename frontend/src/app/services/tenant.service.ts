import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class TenantService {
  private apiUrl = 'http://localhost:8080/api/tenants'; 

  constructor(private http: HttpClient) {}

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  add(tenant: any): Observable<any> {
    return this.http.post(this.apiUrl, tenant);
  }

  update(id: number, tenant: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, tenant);
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
