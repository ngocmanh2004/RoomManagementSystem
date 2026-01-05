import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterOutlet } from '@angular/router'; 
import { ChatbotComponent } from './shared/components/chatbot/chatbot.component';
import { ChatbotService } from './services/chatbot.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterModule, ChatbotComponent], 
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'RoomManagement_FE';

  constructor(private chatbotService: ChatbotService) {}

  ngOnInit(): void {
    // Preload dữ liệu chatbot ngay khi app khởi động
    console.log('🚀 App khởi động - bắt đầu preload dữ liệu phòng...');
    this.chatbotService.preloadCache();
  }
}
  