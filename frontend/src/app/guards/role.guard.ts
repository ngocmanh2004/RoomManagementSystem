import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (allowedRoles: number[]): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isLoggedIn()) {
      router.navigate(['/login']);
      return false;
    }

    const userRole = authService.getUserRole();
    if (userRole !== null && allowedRoles.includes(userRole)) {
      return true;
    }

    alert('Bạn không có quyền truy cập trang này!');
    switch(userRole) {
      case 0:
        router.navigate(['/admin/dashboard']);
        break;
      case 1:
        router.navigate(['/landlord/dashboard']);
        break;
      case 2:
        router.navigate(['/tenant/dashboard']);
        break;
      default:
        router.navigate(['/login']);
    }
    
    return false;
  };
};