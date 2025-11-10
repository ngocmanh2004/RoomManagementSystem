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
import { TenantManagementComponent } from './features/admin/tenant-management/tenant-management.component';

export const routes: Routes = [
  {
    path: '', component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'rooms', component: RoomsComponent },
      { path: 'rooms/:id', component: RoomDetailComponent },
      { path: 'about', component: AboutComponent },
      { path: 'contact', component: ContactComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
    ]
  },
  // ADMIN ROUTES (role = 0)
  {
    path: 'admin',
    component: AdminLayoutComponent,
    canActivate: [authGuard, roleGuard([0])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardAdminComponent },
      { path: 'tenants', component: TenantManagementComponent }
    ]
  },

  // LANDLORD ROUTES (role = 1)
  {
    path: 'landlord',
    canActivate: [authGuard, roleGuard([1])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      //{ path: 'dashboard', component: LandlordDashboardComponent }
    ]
  },

  // TENANT ROUTES (role = 2)
  {
    path: 'tenant',
    canActivate: [authGuard, roleGuard([2])],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      //{ path: 'dashboard', component: TenantDashboardComponent }
    ]
  },

  { path: '**', redirectTo: '' }
];