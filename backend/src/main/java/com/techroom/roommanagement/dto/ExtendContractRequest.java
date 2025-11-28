package com.techroom.roommanagement.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ExtendContractRequest {

    @NotNull(message = "Ngày kết thúc mới không được để trống")
    private LocalDate newEndDate;

    private String notes;

    // Constructors
    public ExtendContractRequest() {}

    public ExtendContractRequest(LocalDate newEndDate, String notes) {
        this.newEndDate = newEndDate;
        this.notes = notes;
    }

    // Getters and Setters
    public LocalDate getNewEndDate() {
        return newEndDate;
    }

    public void setNewEndDate(LocalDate newEndDate) {
        this.newEndDate = newEndDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ExtendContractRequest{" +
                "newEndDate=" + newEndDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}