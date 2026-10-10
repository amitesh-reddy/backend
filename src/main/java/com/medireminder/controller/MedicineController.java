package com.medireminder.controller;

import com.medireminder.model.*;
import com.medireminder.repository.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicineController {
    private final MedicineRepository medicineRepository;
    private final UserRepository userRepository;
    private final MedicineHistoryRepository historyRepository;

    public MedicineController(MedicineRepository medicineRepository,
                              UserRepository userRepository,
                              MedicineHistoryRepository historyRepository) {
        this.medicineRepository = medicineRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @GetMapping
    public ResponseEntity<?> getMedicines(@RequestParam Long userId) {
        return ResponseEntity.ok(medicineRepository.findByUserIdOrderByMedicineTimeAsc(userId)
                .stream().map(this::toDto).toList());
    }

    @PostMapping
    public ResponseEntity<?> addMedicine(@RequestParam Long userId, @RequestBody Medicine input) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "User not found"));

        input.setId(null);
        input.setUser(user.get());
        Medicine saved = medicineRepository.save(input);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicine(@PathVariable Long id,
                                            @RequestParam Long userId,
                                            @RequestBody Medicine input) {
        Optional<Medicine> found = medicineRepository.findByIdAndUserId(id, userId);
        if (found.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Medicine not found"));

        Medicine medicine = found.get();
        medicine.setName(input.getName());
        medicine.setDosage(input.getDosage());
        medicine.setFrequency(input.getFrequency());
        medicine.setMedicineTime(input.getMedicineTime());
        medicine.setStartDate(input.getStartDate());
        medicine.setEndDate(input.getEndDate());
        medicine.setNotes(input.getNotes());

        return ResponseEntity.ok(toDto(medicineRepository.save(medicine)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicine(@PathVariable Long id, @RequestParam Long userId) {
        Optional<Medicine> found = medicineRepository.findByIdAndUserId(id, userId);
        if (found.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Medicine not found"));

        medicineRepository.delete(found.get());
        return ResponseEntity.ok(Map.of("message", "Medicine deleted"));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<?> recordStatus(@PathVariable Long id,
                                           @RequestParam Long userId,
                                           @RequestBody Map<String, String> body) {
        Optional<Medicine> found = medicineRepository.findByIdAndUserId(id, userId);
        if (found.isEmpty()) return ResponseEntity.status(404).body(Map.of("message", "Medicine not found"));

        String status = body.getOrDefault("status", "TAKEN");
        if (!status.equals("TAKEN") && !status.equals("SKIPPED")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Status must be TAKEN or SKIPPED"));
        }

        historyRepository.save(new MedicineHistory(found.get(), LocalDateTime.now(), status));
        return ResponseEntity.ok(Map.of("message", "Status recorded"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> history(@RequestParam Long userId) {
        List<Map<String, Object>> result = historyRepository
                .findByMedicineUserIdOrderByTakenAtDesc(userId)
                .stream().map(h -> Map.<String, Object>of(
                        "id", h.getId(),
                        "medicineId", h.getMedicine().getId(),
                        "medicineName", h.getMedicine().getName(),
                        "takenAt", h.getTakenAt(),
                        "status", h.getStatus()
                )).toList();
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toDto(Medicine m) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", m.getId());
        dto.put("name", m.getName());
        dto.put("dosage", m.getDosage());
        dto.put("frequency", m.getFrequency());
        dto.put("medicineTime", m.getMedicineTime());
        dto.put("startDate", m.getStartDate());
        dto.put("endDate", m.getEndDate());
        dto.put("notes", m.getNotes());
        return dto;
    }
}
