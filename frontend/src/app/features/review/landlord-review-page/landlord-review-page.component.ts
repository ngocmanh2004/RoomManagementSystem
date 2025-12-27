import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// IMPORT COMPONENT CON
import { SendNotificationComponent } from '../../landlord/send-notification/send-notification.component';
import { LandlordFeedbackComponent } from '../landlord-feedback/landlord-feedback.component';

@Component({
  selector: 'app-landlord-review-page',
  standalone: true,
  imports: [
    CommonModule,             
    SendNotificationComponent, 
    LandlordFeedbackComponent  
  ],
  templateUrl: './landlord-review-page.component.html',
  styleUrls: ['./landlord-review-page.component.css']
})
export class LandlordReviewPageComponent {
  activeTab: 'notification' | 'feedback' = 'notification';
}
