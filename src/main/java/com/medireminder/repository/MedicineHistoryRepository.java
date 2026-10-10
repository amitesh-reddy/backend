package com.medireminder.repository;

import com.medireminder.model.MedicineHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicineHistoryRepository extends JpaRepository<MedicineHistory, Long> {
    List<MedicineHistory> findByMedicineUserIdOrderByTakenAtDesc(Long userId);
}
