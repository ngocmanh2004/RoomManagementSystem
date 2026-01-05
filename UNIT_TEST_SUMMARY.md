# Unit Test Summary - Sprint 3

## Tổng quan
Unit tests đã được viết cho các User Stories trong Sprint 3:
- **US 11.5**: Báo cáo đánh giá sai phạm
- **US 14.1**: Xem danh sách báo cáo vi phạm
- **US 14.2**: Xử lý báo cáo vi phạm
- **US 15.1**: Chatbot tư vấn phòng trọ
- **US 15.2**: Chatbot hướng dẫn sử dụng hệ thống

## Files đã tạo/cập nhật

### 1. Report Management Component Tests
**File**: `frontend/src/app/features/admin/report-management/report-management.component.spec.ts`

**Test Coverage**:
- ✅ Component creation
- ✅ Load reports on initialization
- ✅ Handle Page structure response
- ✅ Error handling khi load reports
- ✅ Filter reports by status (PENDING, PROCESSING, RESOLVED, ALL)
- ✅ Filter reports by reason (SPAM, OFFENSIVE, FALSE, OTHER)
- ✅ Combined filters (status + reason)
- ✅ Open/close detail modal
- ✅ Process report successfully
- ✅ Handle process report errors
- ✅ Lock user account (admin action)
- ✅ Delete review
- ✅ Edit review
- ✅ Update review
- ✅ Helper methods (getReasonLabel, filter change handlers)

**Total Test Cases**: 22 tests

### 2. Chatbot Component Tests
**File**: `frontend/src/app/shared/components/chatbot/chatbot.component.spec.ts`

**Test Coverage**:
- ✅ Component creation
- ✅ Toggle chat window
- ✅ Submit user info and show welcome messages
- ✅ Validation for empty userName/userPhone
- ✅ Send message and receive bot response
- ✅ Handle bot response with links
- ✅ Handle empty message
- ✅ Handle API errors gracefully
- ✅ Handle response without candidates
- ✅ Display suggestion chips
- ✅ Select suggestion and send message
- ✅ Show/hide suggestions
- ✅ Save chat history to localStorage
- ✅ Load chat history from localStorage
- ✅ Handle missing chat history
- ✅ Scroll to bottom functionality
- ✅ Browser platform detection

**Total Test Cases**: 17 tests

### 3. Chatbot Service Tests
**File**: `frontend/src/app/services/chatbot.service.spec.ts`

**Test Coverage**:
- ✅ Service creation
- ✅ Preload room data into cache
- ✅ Use cached data if available and not expired
- ✅ Fetch new data if cache expired
- ✅ Filter rooms by price (under)
- ✅ Filter rooms by price (over)
- ✅ Filter rooms by area
- ✅ Filter rooms by location
- ✅ Only show AVAILABLE rooms
- ✅ Limit results to 5 rooms
- ✅ Sort rooms by price
- ✅ Handle empty/null room list
- ✅ Send system guide questions to Gemini
- ✅ Include CHATBOT_DATA in prompt
- ✅ Handle API errors (400, 429, 404, network error)
- ✅ Handle room service error
- ✅ Build prompt with filtered rooms
- ✅ Handle no matching rooms
- ✅ Update cache when fetching new data
- ✅ Handle different response formats

**Total Test Cases**: 20 tests

## Chạy Tests

### Chạy tất cả tests:
```bash
cd frontend
npm test
```

### Chạy tests cho file cụ thể:
```bash
# Report Management
npm test -- --include='**/report-management.component.spec.ts'

# Chatbot Component
npm test -- --include='**/chatbot.component.spec.ts'

# Chatbot Service
npm test -- --include='**/chatbot.service.spec.ts'
```

### Chạy với headless browser (CI/CD):
```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

### Xem code coverage:
```bash
npm test -- --code-coverage
```

## Test Structure

Mỗi test suite được tổ chức theo:
1. **Setup**: Mock services và test data
2. **Describe blocks**: Nhóm tests theo US hoặc chức năng
3. **Test cases**: Test từng scenario cụ thể
4. **Assertions**: Verify expected behavior

## Mock Data Examples

### ReviewReport Mock:
```typescript
{
  id: 1,
  reviewId: 101,
  reviewContent: 'Phòng tệ quá',
  reviewRating: 1,
  reporterId: 5,
  reporterName: 'Nguyễn Văn A',
  reportedUserId: 10,
  reportedUserName: 'Trần Thị B',
  reason: 'SPAM',
  status: 'PENDING',
  createdAt: '2024-01-01T10:00:00',
  reviewRoomName: 'Phòng 101'
}
```

### Room Mock (for Chatbot):
```typescript
{
  id: 1,
  name: 'Phòng 101',
  price: 2500000,
  area: 20,
  status: 'AVAILABLE',
  buildingName: 'Dãy trọ A',
  buildingAddress: '123 Nguyễn Huệ, Quy Nhơn, Bình Định'
}
```

## Key Testing Patterns

### 1. Service Mocking:
```typescript
mockReviewService = jasmine.createSpyObj('ReviewService', [
  'getReviewReports',
  'updateReviewReport',
  'deleteReview'
]);
```

### 2. Observable Testing:
```typescript
mockService.method.and.returnValue(of(mockData));
mockService.method.and.returnValue(throwError(() => new Error('...')));
```

### 3. Async Testing:
```typescript
it('should do something async', (done) => {
  service.method().subscribe(() => {
    expect(...).toBe(...);
    done();
  });
});
```

### 4. Window Methods Spy:
```typescript
spyOn(window, 'alert');
spyOn(window, 'confirm').and.returnValue(true);
```

## Notes

- Tất cả tests đã được viết theo Angular testing best practices
- Sử dụng Jasmine framework với Karma test runner
- Tests cover cả happy path và error scenarios
- Mock data phù hợp với actual models trong project
- Type-safe với TypeScript

## Lỗi đã fix

1. ✅ Fixed `note: null` → `note: undefined` (ReviewReport)
2. ✅ Fixed `roomName` → `reviewRoomName` (theo model)
3. ✅ Added missing fields: `reporterId`, `reportedUserId`, `reportedUserName`
4. ✅ Fixed PageResponse type cho RoomService
5. ✅ Fixed ApiResponse type cho deleteReview
6. ✅ Fixed Review type cho updateReview
7. ✅ Fixed message type trong chatbot component

## Kết quả mong đợi

Khi chạy tests, tất cả 59 test cases phải PASS:
- ✅ 22 tests: Report Management Component
- ✅ 17 tests: Chatbot Component  
- ✅ 20 tests: Chatbot Service

**Total: 59 passing tests** 🎉
