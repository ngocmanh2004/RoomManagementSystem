package com.techroom.roommanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "provinces")
@Getter
@Setter
public class Province {

    @Id
    private Integer code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "division_type")
    private String divisionType;
}