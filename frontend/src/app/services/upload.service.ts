import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UploadService {
  private apiUrl = '/api/upload'; // URL API upload của bạn

  constructor(private http: HttpClient) {}

  uploadImage(file: File, buildingId: number, roomId: number) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('buildingId', buildingId.toString());
    formData.append('roomId', roomId.toString());

    return this.http.post<{ url: string }>(
      'http://localhost:8081/api/upload',
      formData
    );
  }

}