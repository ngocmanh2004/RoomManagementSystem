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

    this.authService.login(username, password).subscribe({
      next: (res: any) => {
        if (res.accessToken) {
          localStorage.setItem('accessToken', res.accessToken);
        }
        
        if (res.refreshToken) {
          localStorage.setItem('refreshToken', res.refreshToken);
        }
        
        if (res.userInfo) {
          localStorage.setItem('currentUser', JSON.stringify(res.userInfo));
        }

        alert(`Đăng nhập thành công! Chào mừng ${res.userInfo?.fullName || 'bạn'}`);
        
        const userRole = res.userInfo?.role;
        
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
        const errorMsg = err.error?.message || 'Sai tên đăng nhập hoặc mật khẩu';
        alert('Đăng nhập thất bại: ' + errorMsg);
      }
    });
  }
}