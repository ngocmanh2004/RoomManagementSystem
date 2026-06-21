import { Injectable, ErrorHandler } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: Error | HttpErrorResponse): void {
    if (error instanceof HttpErrorResponse) {
      // Server error
      console.error('Server Error:', error);
      
      const message = error.error?.message || 'Có lỗi xảy ra từ server';
      alert(message);
    } else {
      // Client error
      console.error('Client Error:', error);
      alert('Có lỗi xảy ra: ' + error.message);
    }
  }
}