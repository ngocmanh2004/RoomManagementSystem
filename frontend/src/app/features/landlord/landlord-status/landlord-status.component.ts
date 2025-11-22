// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router } from '@angular/router';
// import { AuthService } from '../../../services/auth.service';
// import { LandlordService } from '../../../services/landlord.service';
// import { Landlord } from '../../../models/landlord.model';
// import { AboutComponent } from "../../about/about.component";

// @Component({
//   selector: 'app-landlord-status',
//   standalone: true,
//   imports: [CommonModule, AboutComponent],
//   templateUrl: './landlord-status.component.html',
//   styleUrls: ['./landlord-status.component.css']
// })
// export class LandlordStatusComponent implements OnInit {
//   landlord: Landlord | null = null;
//   loading = true;
//   notRegistered = false;

//   constructor(
//     private authService: AuthService,
//     private landlordService: LandlordService,
//     public router: Router
//   ) {}

//   ngOnInit(): void {
//     this.loadLandlordStatus();
//   }

//   loadLandlordStatus(): void {
//     const currentUser = this.authService.getCurrentUser();
//     if (!currentUser) {
//       this.router.navigate(['/login']);
//       return;
//     }

//     // Sử dụng endpoint mới dành cho tenant
//     this.landlordService.getRegistrationStatus(currentUser.id).subscribe({
//       next: (data) => {
//         this.landlord = data;
//         this.loading = false;
//       },
//       error: (err) => {
//         if (err.status === 404) {
//           this.notRegistered = true;
//         } else {
//           console.error('Lỗi khi tải trạng thái:', err);
//         }
//         this.loading = false;
//       }
//     });
//   }

//   getStatusBadgeClass(): string {
//     if (!this.landlord) return '';
    
//     switch (this.landlord.approved) {
//       case 'PENDING':
//         return 'status-pending';
//       case 'APPROVED':
//         return 'status-approved';
//       case 'REJECTED':
//         return 'status-rejected';
//       default:
//         return '';
//     }
//   }

//   getStatusText(): string {
//     if (!this.landlord) return '';
    
//     switch (this.landlord.approved) {
//       case 'PENDING':
//         return 'Đang chờ duyệt';
//       case 'APPROVED':
//         return 'Đã được duyệt';
//       case 'REJECTED':
//         return 'Đã bị từ chối';
//       default:
//         return '';
//     }
//   }

//   goToRegister(): void {
//     this.router.navigate(['/tenant/register-landlord']);
//   }

//   goBack(): void {
//     this.router.navigate(['/tenant/profile']);
//   }
// }