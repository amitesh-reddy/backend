package com.medireminder.repository;

import com.medireminder.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findByUserIdOrderByMedicineTimeAsc(Long userId);
    Optional<Medicine> findByIdAndUserId(Long id, Long userId);
}
