import { HttpErrorResponse } from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export const tokenInterceptor = (req: any, next: any) => {
  const token = localStorage.getItem('accessToken') || localStorage.getItem('token');

  console.log('🔐 Interceptor - URL:', req.url);
  console.log('🔐 Interceptor - Token:', token ? 'EXISTS' : 'MISSING');

  if (token && !req.url.includes('/login') && !req.url.includes('/register')) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    console.log('✅ Interceptor - Authorization header added');
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('❌ HTTP Error:', error.status, error.url);
      console.error('❌ Error body:', error.error);
      
      if (error.status === 401) {
        console.warn('⚠️ 401 Unauthorized - Check token validity');
      }
      
      if (error.status === 403) {
        console.warn('⚠️ 403 Forbidden - Check user permissions');
      }

      return throwError(() => error);
    })
  );
};