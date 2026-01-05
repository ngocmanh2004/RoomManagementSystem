import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { RoomsComponent } from './features/rooms/rooms.component';
import { BuildingRoomsComponent } from './features/building-rooms/building-rooms.component';
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
import { BuildingManagementComponent } from './features/landlord/building-management/building-management.component';
import { BuildingRoomManagementComponent } from './features/landlord/building-room-management/building-room-management.component';
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
import { SendNotificationComponent } from './features/landlord/send-notification/send-notification.component';
import { TenantFeedbackComponent } from './features/review/tenant-feedback/tenant-feedback.component';
import { LandlordFeedbackComponent } from './features/review/landlord-feedback/landlord-feedback.component';
import { TenantNotificationComponent } from './features/tenant-notification/tenant-notification.component';
import { ElectricityManagementComponent } from './features/landlord/electricity-management/electricity-management.component';
import { WaterManagementComponent } from './features/landlord/water-management/water-management.component';
import { ExtraCostManagementComponent } from './features/landlord/extra-cost-management/extra-cost-management.component';
import { InvoiceManagementComponent } from './features/landlord/invoice-management/invoice-management.component';
import { InvoiceViewComponent } from './features/tenant/invoice-view/invoice-view.component';
import { VnpayReturnComponent } from './features/tenant/vnpay-return/vnpay-return.component';
import { TenantLayoutComponent } from './shared/layout-tenant/tenant-layout/tenant-layout.component';

import { ReportManagementComponent } from './features/admin/report-management/report-management.component';

export const routes: Routes = [
  {
    path: '', component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'buildings/:id', component: BuildingRoomsComponent },
      { path: 'rooms', component: RoomsComponent },
      { path: 'rooms/:id', component: RoomDetailComponent },
      { path: 'about', component: AboutComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      { path: 'register-landlord', component: RegisterLandlordComponent},
      { path: 'tenant-profile', component: TenantProfileComponent },
      { path: 'contract-detail', component: ContractDetailComponent },
      { path: 'tenant-feedback', component: TenantFeedbackComponent},
      { path: 'notification', component: TenantNotificationComponent}
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
    { path: 'buildings', component: BuildingManagementComponent },
    { path: 'buildings/:id/rooms', component: BuildingRoomManagementComponent },
    { path: 'rooms', component: RoomManagementComponent },
    { path: 'bookings', component: LandlordBookingComponent },
    { path: 'bookings/create', component: CreateDirectContractComponent },
    { path: 'bookings/:id', component: LandlordBookingDetailComponent },
    { path: 'electricity', component: ElectricityManagementComponent },
    { path: 'water', component: WaterManagementComponent },
    { path: 'extra-cost', component: ExtraCostManagementComponent },
    { path: 'invoices', component: InvoiceManagementComponent },
    { path: 'notification', component: SendNotificationComponent},
    { path: 'landlord-feedback', component: LandlordFeedbackComponent},
  ],
},

// TENANT ROUTES (role = 2)
{
  path: 'tenant',
  component: TenantLayoutComponent,
  canActivate: [authGuard, roleGuard([2])],
  children: [
    { path: '', redirectTo: 'invoices', pathMatch: 'full' },
    { path: 'invoices', component: InvoiceViewComponent },
    { path: 'vnpay-return', component: VnpayReturnComponent },
    { path: 'notification', component: TenantNotificationComponent },
    { path: 'tenant-feedback', component: TenantFeedbackComponent }
  ],
},


  { path: '**', redirectTo: '' }
];