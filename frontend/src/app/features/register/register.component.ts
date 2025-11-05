import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required]],
      fullName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      address: ['', [Validators.required]],
      cccd: [''],
      dateOfBirth: [''],
      agreeTerms: [false, [Validators.requiredTrue]],
      receiveNews: [false]
    }, { 
      validators: this.passwordMatchValidator 
    });
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    
    if (password && confirmPassword && password !== confirmPassword) {
      return { passwordMismatch: true };
    }
    return null;
  }

  register() {
    if (this.registerForm.invalid) {
      if (!this.registerForm.get('username')?.value || 
          !this.registerForm.get('fullName')?.value || 
          !this.registerForm.get('email')?.value || 
          !this.registerForm.get('phone')?.value || 
          !this.registerForm.get('password')?.value || 
          !this.registerForm.get('address')?.value) {
        alert('Vui lòng điền đầy đủ các trường bắt buộc (*)');
        return;
      }

      if (this.registerForm.hasError('passwordMismatch')) {
        alert('Mật khẩu xác nhận không khớp!');
        return;
      }

      if (this.registerForm.get('password')?.hasError('minlength')) {
        alert('Mật khẩu phải có ít nhất 6 ký tự!');
        return;
      }

      if (!this.registerForm.get('agreeTerms')?.value) {
        alert('Bạn phải đồng ý với điều khoản sử dụng!');
        return;
      }

      if (this.registerForm.get('email')?.hasError('email')) {
        alert('Email không hợp lệ!');
        return;
      }

      if (this.registerForm.get('phone')?.hasError('pattern')) {
        alert('Số điện thoại phải có 10 chữ số!');
        return;
      }

      return;
    }

    const formValue = this.registerForm.value;
    const registerData = {
      username: formValue.username,
      fullName: formValue.fullName,
      email: formValue.email,
      phone: formValue.phone,
      password: formValue.password,
      address: formValue.address,
      cccd: formValue.cccd || null,
      dateOfBirth: formValue.dateOfBirth || null
    };

    this.authService.register(registerData).subscribe({
      next: () => {
        alert('Đăng ký thành công!');
        this.router.navigate(['/login']);
      },
      error: err => {
        console.error('Register error:', err);
        const errorMsg = err.error?.message || err.error || 'Lỗi không xác định';
        alert('Đăng ký thất bại: ' + errorMsg);
      }
    });
  }
}