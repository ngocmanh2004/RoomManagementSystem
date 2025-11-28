package com.techroom.roommanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDTO<T> {
    private List<T> content;
    private int number;           // Current page number
    private int size;             // Page size
    private int totalPages;       // Total number of pages
    private long totalElements;   // Total number of elements
    private boolean first;        // Is first page
    private boolean last;         // Is last page
    private boolean empty;        // Is empty

    // Constructor từ Page
    public PageResponseDTO(Page<T> page) {
        this.content = page.getContent();
        this.number = page.getNumber();
        this.size = page.getSize();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.first = page.isFirst();
        this.last = page.isLast();
        this.empty = page.isEmpty();
    }

    // Static factory method
    public static <T> PageResponseDTO<T> of(Page<T> page) {
        return new PageResponseDTO<>(page);
    }
}