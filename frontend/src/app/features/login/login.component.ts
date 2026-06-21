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

        alert(`Đăng nhập thành công! Chào mừng ${res.userInfo?.fullName || res.user?.fullName || 'bạn'}`);

        // ✅ 2. ĐIỀU HƯỚNG THEO ROLE
        // Backend có thể trả về trong res.userInfo hoặc res.user
        const userInfo = res.userInfo || res.user;
        const userRole = userInfo?.role;

        console.log('🔑 LoginComponent: Full user info:', userInfo);
        console.log('🔑 LoginComponent: User role:', userRole, 'Type:', typeof userRole);

        // Normalize role to number
        let roleNum: number;
        if (typeof userRole === 'string') {
          const roleMap: { [key: string]: number } = { 'ADMIN': 0, 'LANDLORD': 1, 'TENANT': 2 };
          roleNum = roleMap[userRole] ?? 2;
        } else {
          roleNum = userRole ?? 2;
        }

        console.log('🎯 LoginComponent: Normalized role:', roleNum);

        if (roleNum === 0) {
          // Admin
          console.log('👑 Redirecting to Admin dashboard');
          this.router.navigate(['/admin/dashboard']);
        } else if (roleNum === 1) {
          // Landlord
          console.log('🏠 Redirecting to Landlord dashboard');
          this.router.navigate(['/landlord/dashboard']);
        } else if (roleNum === 2) {
          // Tenant - Smart routing
          console.log('🔍 Tenant login - Checking if has active contract...');
          this.authService.hasActiveContract().subscribe({
            next: (hasContract) => {
              console.log('✅ Has contract:', hasContract);
              if (hasContract) {
                console.log('📊 Redirecting to Tenant dashboard');
                this.router.navigate(['/tenant/dashboard']);
              } else {
                console.log('🏡 No contract - Redirecting to homepage');
                this.router.navigate(['/']);
              }
            },
            error: (err) => {
              console.error('❌ Error checking contract:', err);
              this.router.navigate(['/']);
            }
          });
        } else {
          // Unknown role - go to homepage
          console.log('❓ Unknown role - Redirecting to homepage');
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