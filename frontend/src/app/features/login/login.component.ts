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
        console.log('Login response:', res);
        
        // 1. LƯU TOKEN (Rất quan trọng, nếu không lưu thì không gọi được API khác)
        // Giả sử authService của bạn có hàm này, hoặc bạn lưu trực tiếp vào localStorage
        localStorage.setItem('accessToken', res.accessToken);
        localStorage.setItem('refreshToken', res.refreshToken);
        localStorage.setItem('user', JSON.stringify(res.user));

        alert(`Đăng nhập thành công! Chào mừng ${res.user.fullName}`);
        
        // 2. ĐIỀU HƯỚNG THEO ROLE (STRING)
        const userRole = res.user.role; // Backend trả về "ADMIN", "LANDLORD"...
        
        switch(userRole) {
          case 'ADMIN':      // Khớp với getRoleName() trong AuthController
          case 0:            // (Dự phòng nếu backend trả về số)
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
        console.error('Login error:', err);
        const errorMsg = err.error?.message || 'Sai tên đăng nhập hoặc mật khẩu';
        alert('Đăng nhập thất bại: ' + errorMsg);
      }
    });
  }
}