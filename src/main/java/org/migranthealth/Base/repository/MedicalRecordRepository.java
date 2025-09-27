package org.migranthealth.Base.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.migranthealth.Base.entity.MedicalRecord;
import org.migranthealth.Base.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    List<MedicalRecord> findByPatient(Patient patient);
    
    List<MedicalRecord> findByPatientId(Long patientId);
    
    List<MedicalRecord> findByDoctorName(String doctorName);
    
    List<MedicalRecord> findByVisitDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE " +
           "LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mr.symptoms) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(mr.treatment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<MedicalRecord> searchMedicalRecords(@Param("searchTerm") String searchTerm);
}