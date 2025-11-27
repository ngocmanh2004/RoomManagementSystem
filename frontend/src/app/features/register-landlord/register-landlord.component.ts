import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LandlordService } from '../../services/landlord.service';
import { ProvinceService } from '../../services/province.service';
import { Province, District } from '../../models/province.model';

@Component({
  selector: 'app-register-landlord',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './register-landlord.component.html',
  styleUrls: ['./register-landlord.component.css']
})
export class RegisterLandlordComponent implements OnInit {
  registerForm!: FormGroup;
  submitting = false;
  provinces: Province[] = [];
  districts: District[] = [];

  frontImagePreview: string | null = null;
  backImagePreview: string | null = null;
  businessLicensePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private landlordService: LandlordService,
    private provinceService: ProvinceService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadProvinces();
  }

  initForm(): void {
    this.registerForm = this.fb.group({
      cccd: ['', [Validators.required, Validators.pattern(/^\d{9,12}$/)]],
      address: ['', Validators.required],
      expectedRoomCount: ['', [Validators.required, Validators.min(1)]],
      provinceCode: [null],
      districtCode: [null],
      frontImage: [null, Validators.required],
      backImage: [null, Validators.required],
      businessLicense: [null, Validators.required]
    });
  }

  loadProvinces(): void {
    this.provinceService.getAllProvinces().subscribe({
      next: (data) => this.provinces = data,
      error: (err) => console.error('Lỗi tải tỉnh:', err)
    });
  }

  onProvinceChange(event: any): void {
    const provinceCode = event.target.value;
    if (provinceCode) {
      this.provinceService.getDistrictsByProvince(Number(provinceCode)).subscribe({
        next: (data) => this.districts = data,
        error: (err) => console.error('Lỗi tải quận:', err)
      });
    } else {
      this.districts = [];
    }
    this.registerForm.patchValue({ districtCode: null });
  }

  onFileChange(event: any, fieldName: string): void {
    const file = event.target.files[0];
    if (file) {
      this.registerForm.patchValue({ [fieldName]: file });

      const reader = new FileReader();
      reader.onload = () => {
        const result = reader.result as string;
        if (fieldName === 'frontImage') {
          this.frontImagePreview = result;
        } else if (fieldName === 'backImage') {
          this.backImagePreview = result;
        } else if (fieldName === 'businessLicense') {
          this.businessLicensePreview = result;
        }
      };
      reader.readAsDataURL(file);
    }
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      Object.keys(this.registerForm.controls).forEach(key => {
        this.registerForm.get(key)?.markAsTouched();
      });
      alert('Vui lòng điền đầy đủ thông tin');
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser) {
      alert('Vui lòng đăng nhập');
      return;
    }

    this.submitting = true;

    const formData = new FormData();
    formData.append('userId', currentUser.id.toString());
    formData.append('cccd', this.registerForm.get('cccd')?.value);
    formData.append('address', this.registerForm.get('address')?.value);
    formData.append('expectedRoomCount', this.registerForm.get('expectedRoomCount')?.value);

    if (this.registerForm.get('provinceCode')?.value) {
      formData.append('provinceCode', this.registerForm.get('provinceCode')?.value);
    }
    if (this.registerForm.get('districtCode')?.value) {
      formData.append('districtCode', this.registerForm.get('districtCode')?.value);
    }

    formData.append('frontImage', this.registerForm.get('frontImage')?.value);
    formData.append('backImage', this.registerForm.get('backImage')?.value);
    formData.append('businessLicense', this.registerForm.get('businessLicense')?.value);

    this.landlordService.registerLandlord(formData).subscribe({
      next: (response) => {
        alert(response.message);
        this.router.navigate(['/tenant/landlord-status']);
      },
      error: (err) => {
        console.error('Lỗi:', err);
        alert(err.error?.message || 'Có lỗi xảy ra');
        this.submitting = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/tenant/profile']);
  }
}