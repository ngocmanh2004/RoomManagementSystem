import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { PLATFORM_ID } from '@angular/core';
import { of, throwError } from 'rxjs';
import { ChatbotComponent } from './chatbot.component';
import { ChatbotService } from '../../../services/chatbot.service';

describe('ChatbotComponent - Sprint 3', () => {
  let component: ChatbotComponent;
  let fixture: ComponentFixture<ChatbotComponent>;
  let mockChatbotService: jasmine.SpyObj<ChatbotService>;

  beforeEach(async () => {
    mockChatbotService = jasmine.createSpyObj('ChatbotService', ['sendMessage']);

    await TestBed.configureTestingModule({
      imports: [ChatbotComponent, FormsModule],
      providers: [
        { provide: ChatbotService, useValue: mockChatbotService },
        { provide: PLATFORM_ID, useValue: 'browser' }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ChatbotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // ========== US 15.1: Chatbot tư vấn phòng trọ ==========
  describe('US 15.1: Chatbot tư vấn phòng trọ', () => {
    
    // TEST 1: Mở/đóng cửa sổ chat
    it('should toggle chat window', () => {
      expect(component.showChat).toBe(false);
      
      component.toggleChat();
      expect(component.showChat).toBe(true);
      
      component.toggleChat();
      expect(component.showChat).toBe(false);
    });

    // TEST 2: Hiển thị form nhập thông tin user
    it('should show form when opening chat', () => {
      component.toggleChat();
      
      expect(component.showForm).toBe(true);
    });

    // TEST 3: Submit thông tin user và hiển thị welcome message
    it('should submit user info and show welcome message', (done) => {
      component.userName = 'Nguyễn Văn A';
      component.userPhone = '0912345678';
      
      component.submitUserInfo();
      
      expect(component.showForm).toBe(false);
      
      setTimeout(() => {
        expect(component.messages.length).toBeGreaterThan(0);
        expect(component.messages[0].text).toContain('Xin chào');
        done();
      }, 1100);
    });

    // TEST 4: Gửi tin nhắn tư vấn phòng trọ và nhận response từ bot
    it('should send message and receive bot response', () => {
      const mockResponse = {
        candidates: [{
          content: {
            parts: [{
              text: 'Dưới đây là các phòng dưới 3 triệu...'
            }]
          }
        }]
      };
      
      mockChatbotService.sendMessage.and.returnValue(of(mockResponse));
      component.userName = 'Test User';
      component.userPhone = '0912345678';
      component.inputText = 'Tìm phòng dưới 3 triệu';
      
      component.sendMessage();
      
      expect(component.messages[0].from).toBe('user');
      expect(component.messages[0].text).toBe('Tìm phòng dưới 3 triệu');
      expect(mockChatbotService.sendMessage).toHaveBeenCalled();
    });

    // TEST 5: Không gửi tin nhắn rỗng
    it('should not send empty message', () => {
      component.inputText = '   ';
      const initialLength = component.messages.length;
      
      component.sendMessage();
      
      expect(component.messages.length).toBe(initialLength);
      expect(mockChatbotService.sendMessage).not.toHaveBeenCalled();
    });

    // TEST 6: Xử lý lỗi khi API chatbot fail
    it('should handle API error gracefully', (done) => {
      mockChatbotService.sendMessage.and.returnValue(
        throwError(() => new Error('Network error'))
      );
      
      component.userName = 'Test';
      component.userPhone = '0912345678';
      component.inputText = 'Test message';
      
      component.sendMessage();
      
      setTimeout(() => {
        const errorMessage = component.messages.find(m => 
          m.from === 'bot' && m.text.includes('lỗi')
        );
        expect(errorMessage).toBeDefined();
        done();
      }, 100);
    });
  });

  // ========== US 15.2: Chatbot hướng dẫn sử dụng hệ thống ==========
  describe('US 15.2: Chatbot hướng dẫn sử dụng', () => {
    
    // TEST 7: Hiển thị các gợi ý câu hỏi thường gặp
    it('should display suggestion chips for common questions', () => {
      expect(component.suggestionChips.length).toBeGreaterThan(0);
      expect(component.suggestionChips).toContain('Làm sao để đăng ký chủ trọ?');
      expect(component.suggestionChips).toContain('Cách thanh toán hóa đơn?');
    });

    // TEST 8: Click chọn gợi ý và tự động gửi tin nhắn
    it('should auto-send message when selecting suggestion', () => {
      spyOn(component, 'sendMessage');
      const suggestion = 'Làm sao để đăng ký chủ trọ?';
      
      component.selectSuggestion(suggestion);
      
      expect(component.inputText).toBe(suggestion);
      expect(component.sendMessage).toHaveBeenCalled();
    });

    // TEST 9: Hiển thị gợi ý sau khi nhận response
    it('should show suggestions after receiving bot response', (done) => {
      mockChatbotService.sendMessage.and.returnValue(of({
        candidates: [{ content: { parts: [{ text: 'Answer' }] } }]
      }));
      
      component.userName = 'Test';
      component.userPhone = '0912345678';
      component.inputText = 'Question';
      component.sendMessage();
      
      setTimeout(() => {
        expect(component.showSuggestions).toBe(true);
        done();
      }, 100);
    });

    // TEST 10: Ẩn gợi ý khi user đang gửi tin nhắn
    it('should hide suggestions when sending message', () => {
      component.showSuggestions = true;
      component.inputText = 'Test question';
      mockChatbotService.sendMessage.and.returnValue(of({
        candidates: [{ content: { parts: [{ text: 'Answer' }] } }]
      }));
      
      component.userName = 'Test';
      component.userPhone = '0912345678';
      component.sendMessage();
      
      expect(component.showSuggestions).toBe(false);
    });
  });

  // ========== Lưu trữ lịch sử chat ==========
  describe('Chat History', () => {
    
    beforeEach(() => {
      spyOn(localStorage, 'getItem').and.returnValue(null);
      spyOn(localStorage, 'setItem');
    });

    // TEST 11: Lưu lịch sử chat vào localStorage
    it('should save chat history to localStorage', () => {
      component.userPhone = '0912345678';
      component.messages = [
        { from: 'user', text: 'Hello' },
        { from: 'bot', text: 'Hi' }
      ];
      
      component.saveChatHistory();
      
      expect(localStorage.setItem).toHaveBeenCalled();
    });

    // TEST 12: Load lịch sử chat từ localStorage
    it('should load chat history from localStorage', () => {
      const savedMessages: { from: 'user' | 'bot'; text: string }[] = [
        { from: 'user', text: 'Previous message' }
      ];
      (localStorage.getItem as jasmine.Spy).and.returnValue(
        JSON.stringify(savedMessages)
      );
      
      component.userPhone = '0912345678';
      component.loadChatHistory();
      
      expect(component.messages).toEqual(savedMessages);
    });
  });

  // ========== Additional Edge Cases ==========
  describe('Additional Tests: Error Recovery & UX', () => {
    


    // TEST 14: Retry sending message after error
    it('should retry sending message after error', () => {
      let attemptCount = 0;
      mockChatbotService.sendMessage.and.callFake(() => {
        attemptCount++;
        if (attemptCount === 1) {
          return throwError(() => new Error('Network error'));
        }
        return of({
          candidates: [{ content: { parts: [{ text: 'Success!' }] } }]
        });
      });
      
      component.userName = 'Test';
      component.userPhone = '0912345678';
      component.inputText = 'Test';
      
      component.sendMessage(); // First attempt fails
      component.sendMessage(); // Retry succeeds
      
      expect(attemptCount).toBe(2);
    });

    // TEST 15: Validate phone number format before submitting user info
    it('should validate phone number format', () => {
      component.userName = 'Test User';
      component.userPhone = '123'; // Invalid format
      
      component.submitUserInfo();
      
      // Should show error or not proceed
      expect(component.showForm).toBe(true); // Still showing form
    });
  });
});

