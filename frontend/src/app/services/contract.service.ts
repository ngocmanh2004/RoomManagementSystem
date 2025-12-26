import { Observable } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Contract } from "../models/contract.model";
@Injectable({
  providedIn: 'root'
})
export class ContractService {

  private apiUrl = 'http://localhost:8081/api/contracts';

  constructor(private http: HttpClient) {}

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}/status`, { status });
  }

  getPendingContracts(): Observable<any> {
    return this.http.get(`${this.apiUrl}?status=PENDING`);
  }
  getContracts(status: string) {
    // For landlord UI we use the landlord-specific endpoint which returns a paged ApiResponse.
    let url = '/api/landlord/contracts';
    if (status) {
      url += `?status=${status}`;
    }
    return this.http.get<any>(url);
  }
}
