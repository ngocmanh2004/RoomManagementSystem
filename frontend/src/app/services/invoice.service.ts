import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { Invoice, InvoiceCreateRequest, InvoiceResponse } from '../models/invoice.model';
import { ApiResponse } from '../models/api-response.model';

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {
  /**
   * Lấy tiền điện theo phòng và tháng (trả về tổng tiền hoặc 0)
   */
  getElectricityByRoomMonth(roomId: number, month: string): Observable<number> {
    const url = `/api/utilities/electric?month=${encodeURIComponent(month)}&roomId=${roomId}`;
    return this.http.get<any[]>(url).pipe(
      map(list => {
        if (!list || !Array.isArray(list) || list.length === 0) return 0;
        // API may return list of records; pick the first matching and return totalAmount
        const rec = list[0];
        return rec && rec.totalAmount ? rec.totalAmount : 0;
      })
    );
  }

  /**
   * Lấy tiền nước theo phòng và tháng (trả về tổng tiền hoặc 0)
   */
  getWaterByRoomMonth(roomId: number, month: string): Observable<number> {
    const url = `/api/utilities/water?month=${encodeURIComponent(month)}&roomId=${roomId}`;
    return this.http.get<any[]>(url).pipe(
      map(list => {
        if (!list || !Array.isArray(list) || list.length === 0) return 0;
        const rec = list[0];
        return rec && rec.totalAmount ? rec.totalAmount : 0;
      })
    );
  }

  /**
   * Lấy tổng chi phí phát sinh theo phòng và tháng (tính tổng các extra-costs)
   */
  getExtraCostsTotalByRoomMonth(roomId: number, month: string): Observable<number> {
    const url = `/api/utilities/extra-costs?month=${encodeURIComponent(month)}&roomId=${roomId}`;
    return this.http.get<any[]>(url).pipe(
      map(list => {
        if (!list || !Array.isArray(list) || list.length === 0) return 0;
        return list.reduce((s: number, it: any) => s + (it.amount || it.totalAmount || 0), 0);
      })
    );
  }
  private readonly API_URL = '/api/invoices';

  constructor(private http: HttpClient) {}

  /**
   * Tạo hóa đơn mới
   */
  createInvoice(request: InvoiceCreateRequest): Observable<ApiResponse<InvoiceResponse>> {
    return this.http.post<ApiResponse<InvoiceResponse>>(this.API_URL, request);
  }

  /**
   * Lấy tất cả hóa đơn
   */
  getAll(): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(this.API_URL);
  }

  /**
   * Lấy hóa đơn theo ID
   */
  getById(id: number): Observable<ApiResponse<Invoice>> {
    return this.http.get<ApiResponse<Invoice>>(`${this.API_URL}/${id}`);
  }

  /**
   * Lấy hóa đơn theo hợp đồng
   */
  getByContractId(contractId: number): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/contract/${contractId}`);
  }

  /**
   * Lấy hóa đơn theo phòng
   */
  getByRoomId(roomId: number): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/room/${roomId}`);
  }

  /**
   * Lấy hóa đơn theo người thuê
   */
  getByTenantId(tenantId: number): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/tenant/${tenantId}`);
  }

  /**
   * Lấy hóa đơn theo chủ trọ
   */
  getByLandlordId(landlordId: number): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/landlord/${landlordId}`);
  }

  /**
   * Lấy hóa đơn theo tháng
   */
  getByMonth(month: string): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/month/${month}`);
  }

  /**
   * Lấy hóa đơn theo trạng thái
   */
  getByStatus(status: string): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/status/${status}`);
  }

  /**
   * Lấy hóa đơn theo chủ trọ và trạng thái
   */
  getByLandlordIdAndStatus(landlordId: number, status: string): Observable<ApiResponse<Invoice[]>> {
    return this.http.get<ApiResponse<Invoice[]>>(`${this.API_URL}/landlord/${landlordId}/status/${status}`);
  }

  /**
   * Cập nhật hóa đơn
   */
  updateInvoice(id: number, request: InvoiceCreateRequest): Observable<ApiResponse<InvoiceResponse>> {
    return this.http.put<ApiResponse<InvoiceResponse>>(`${this.API_URL}/${id}`, request);
  }

  /**
   * Cập nhật trạng thái hóa đơn
   */
  updateStatus(id: number, status: string): Observable<ApiResponse<InvoiceResponse>> {
    return this.http.patch<ApiResponse<InvoiceResponse>>(
      `${this.API_URL}/${id}/status?status=${status}`,
      {}
    );
  }

  /**
   * Xóa hóa đơn
   */
  deleteInvoice(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.API_URL}/${id}`);
  }
}
