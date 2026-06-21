package com.techroom.roommanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 255)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"building"})
    private List<Room> rooms;

    @Column(name = "province_code")
    private Integer provinceCode;

    @Column(name = "district_code")
    private Integer districtCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "landlord_id")
    @JsonIgnoreProperties({"user", "building", "hibernateLazyInitializer", "handler"})
    private Landlord landlord;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}