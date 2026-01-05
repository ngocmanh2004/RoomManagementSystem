package com.techroom.roommanagement.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private int number;           // Current page number
    private int size;             // Page size
    private long totalElements;   // Total number of elements
    private int totalPages;       // Total number of pages
    private boolean last;         // Is last page

    // Constructor with 6 params (used in Controller)
    public PageResponseDTO(List<T> content, int number, int size, long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }

    // Constructor từ Page
    public PageResponseDTO(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }

    // Static factory method
    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return new PageResponseDTO<>(page);
    }
}