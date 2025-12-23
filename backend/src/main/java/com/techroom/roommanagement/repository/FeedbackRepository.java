package com.techroom.roommanagement.repository;

import com.techroom.roommanagement.model.Contract;
import com.techroom.roommanagement.model.ContractStatus;
import com.techroom.roommanagement.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    // Chủ trọ xem tất cả phản hồi của mình
    @Query("SELECT f FROM Feedback f WHERE f.receiver.id = :landlordUserId ORDER BY f.createdAt DESC")
    Page<Feedback> findByLandlordUserId(@Param("landlordUserId") Integer landlordUserId, Pageable pageable);

    // Khách xem phản hồi của mình (danh sách đầy đủ)
    List<Feedback> findByTenantIdOrderByCreatedAtDesc(Integer tenantId);

    // Khách xem phản hồi của mình (có phân trang)
    Page<Feedback> findByTenantId(Integer tenantId, Pageable pageable);

    // Các phương thức kiểm tra quyền sửa/xóa
    Optional<Feedback> findByIdAndRoom_Landlord_User_Id(Integer id, Integer landlordUserId);
    Optional<Feedback> findByIdAndReceiverId(Integer id, Integer receiverId);
    Optional<Feedback> findByIdAndTenantId(Integer id, Integer tenantId);
}
