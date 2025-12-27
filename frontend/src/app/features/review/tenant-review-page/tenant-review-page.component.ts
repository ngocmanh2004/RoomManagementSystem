import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// COMPONENT CON
import { TenantFeedbackComponent } from '../tenant-feedback/tenant-feedback.component';
import { TenantNotificationComponent } from '../../tenant-notification/tenant-notification.component';

@Component({
  selector: 'app-tenant-review-page',
  standalone: true,
  imports: [
    CommonModule,               
    TenantFeedbackComponent,   
    TenantNotificationComponent 
  ],
  templateUrl: './tenant-review-page.component.html',
  styleUrls: ['./tenant-review-page.component.css']
})
export class TenantReviewPageComponent {
  activeTab: 'notification' | 'feedback' = 'notification';
  notificationCount = 0;
}
