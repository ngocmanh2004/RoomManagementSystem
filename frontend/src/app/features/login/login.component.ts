import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  login() {
    if (this.loginForm.invalid) {
      alert('Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!');
      return;
    }

    const { username, password } = this.loginForm.value;
    console.log('🔐 LoginComponent: Attempting login for:', username);

    this.authService.login(username, password).subscribe({
      next: (res: any) => {
        console.log('✅ LoginComponent: Login response received:', res);
        
        // ✅ 1. LƯU TOKEN VÀO LOCALSTORAGE (RẤT QUAN TRỌNG)
        if (res.accessToken) {
          localStorage.setItem('accessToken', res.accessToken);
          console.log('✅ LoginComponent: Access token saved');
        }
        
        if (res.refreshToken) {
          localStorage.setItem('refreshToken', res.refreshToken);
          console.log('✅ LoginComponent: Refresh token saved');
        }
        
        if (res.userInfo) {
          localStorage.setItem('currentUser', JSON.stringify(res.userInfo));
          console.log('✅ LoginComponent: User info saved:', res.userInfo);
        }

        alert(`Đăng nhập thành công! Chào mừng ${res.userInfo?.fullName || 'bạn'}`);
        
        // ✅ 2. ĐIỀU HƯỚNG THEO ROLE
        const userRole = res.userInfo?.role; // Backend trả về "ADMIN", "LANDLORD", "TENANT"
        console.log('🔑 LoginComponent: User role:', userRole);
        
        switch(userRole) {
          case 'ADMIN':
          case 0:
            this.router.navigate(['/admin/dashboard']);
            break;
            
          case 'LANDLORD':
          case 1:
            this.router.navigate(['/landlord/dashboard']);
            break;
            
          case 'TENANT':
          case 2:
            this.router.navigate(['/tenant/dashboard']);
            break;
            
          default:
            this.router.navigate(['/']);
        }
      },
      error: (err) => {
        console.error('❌ LoginComponent: Login error:', err);
        const errorMsg = err.error?.message || 'Sai tên đăng nhập hoặc mật khẩu';
        alert('Đăng nhập thất bại: ' + errorMsg);
      }
    });
  }
}