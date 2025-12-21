import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { RoomsComponent } from './features/rooms/rooms.component';
import { AboutComponent } from './features/about/about.component';
import { ContactComponent } from './features/contact/contact.component';
import { RoomDetailComponent } from './features/room-detail/room-detail.component';
import { LoginComponent } from './features/login/login.component';
import { RegisterComponent } from './features/register/register.component';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';
import { AdminLayoutComponent } from './shared/layout-admin/admin-layout/admin-layout.component';
import { PublicLayoutComponent } from './shared/layout/public-layout/public-layout.component';
import { DashboardAdminComponent } from './features/admin/dasboard-admin/dasboard-admin.component';
import { TenantManagementComponent } from './features/landlord/tenant-management/tenant-management.component';
import { RoomManagementComponent } from './features/landlord/room-management/room-management.component';
import { TenantProfileComponent } from './features/tenant-profile/tenant-profile.component';
import { UserManagementComponent } from './features/admin/user-management/user-management.component';
import { RegisterLandlordComponent } from './features/register-landlord/register-landlord.component';
import { AdminLandlordApprovalComponent } from './features/admin/admin-landlord-approval/admin-landlord-approval.component';
import { ContractDetailComponent } from './features/contract-detail/contract-detail.component';
import { LandlordLayoutComponent } from './shared/layout-landlord/landlord-layout/landlord-layout.component';
import { DashboardLandlordComponent } from './features/landlord/dashboard-landlord/dashboard-landlord.component';
import { LandlordBookingComponent } from './features/landlord/landlord-booking/landlord-booking.component';
import { LandlordBookingDetailComponent } from './features/landlord/landlord-booking-detail/landlord-booking-detail.component';
import { CreateDirectContractComponent } from './features/landlord/create-direct-contract/create-direct-contract.component';
import { ElectricityManagementComponent } from './features/landlord/electricity-management/electricity-management.component';
import { WaterManagementComponent } from './features/landlord/water-management/water-management.component';
import { ExtraCostManagementComponent } from './features/landlord/extra-cost-management/extra-cost-management.component';
import { ReportManagementComponent } from './features/admin/report-management/report-management.component';

export const routes: Routes = [
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'rooms', component: RoomsComponent },
      { path: 'rooms/:id', component: RoomDetailComponent },
      { path: 'about', component: AboutComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'register-landlord', component: RegisterLandlordComponent },
      { path: 'tenant-profile', component: TenantProfileComponent },
      { path: 'contract-detail', component: ContractDetailComponent },
    ],
  },
  // ADMIN ROUTES (role = 0)
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [authGuard, roleGuard([0])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardAdminComponent },
      { path: 'users', component: UserManagementComponent },
      { path: 'landlords', component: AdminLandlordApprovalComponent },
      {
        path: 'report-management',
        component: ReportManagementComponent,
        canActivate: [roleGuard([0])], // nếu có guard cho admin
      },
    ],
  },

  // LANDLORD ROUTES (role = 1)
  {
    path: 'landlord',
    component: LandlordLayoutComponent,
    canActivate: [authGuard, roleGuard([1])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardLandlordComponent },
      { path: 'tenants', component: TenantManagementComponent },
      { path: 'rooms', component: RoomManagementComponent },
      { path: 'bookings', component: LandlordBookingComponent },
      { path: 'bookings/create', component: CreateDirectContractComponent },
      { path: 'bookings/:id', component: LandlordBookingDetailComponent },
      { path: 'electricity', component: ElectricityManagementComponent },
      { path: 'water', component: WaterManagementComponent },
      { path: 'extra-cost', component: ExtraCostManagementComponent },
    ],
  },

  // TENANT ROUTES (role = 2)
  {
    path: 'tenant',
    canActivate: [authGuard, roleGuard([2])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      //{ path: 'dashboard', component: TenantDashboardComponent }
    ],
  },

  { path: '**', redirectTo: '' },
];
