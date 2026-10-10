package com.medireminder.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medicine_history")
public class MedicineHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    private LocalDateTime takenAt;

    @Column(nullable = false)
    private String status;

    public MedicineHistory() {}

    public MedicineHistory(Medicine medicine, LocalDateTime takenAt, String status) {
        this.medicine = medicine;
        this.takenAt = takenAt;
        this.status = status;
    }

    public Long getId() { return id; }
    public Medicine getMedicine() { return medicine; }
    public LocalDateTime getTakenAt() { return takenAt; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }
    public void setTakenAt(LocalDateTime takenAt) { this.takenAt = takenAt; }
    public void setStatus(String status) { this.status = status; }
}
