package org.migranthealth.Base.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.migranthealth.Base.entity.MedicalRecord;
import org.migranthealth.Base.entity.Patient;
import org.migranthealth.Base.repository.MedicalRecordRepository;
import org.migranthealth.Base.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    // Create
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        return medicalRecordRepository.save(medicalRecord);
    }
    
    // Create medical record for patient ID
    public MedicalRecord createMedicalRecordForPatient(Long patientId, MedicalRecord medicalRecord) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));
        medicalRecord.setPatient(patient);
        return medicalRecordRepository.save(medicalRecord);
    }
    
    // Read - Get all medical records
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }
    
    // Read - Get medical record by ID
    public Optional<MedicalRecord> getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id);
    }
    
    // Read - Get medical records by patient ID
    public List<MedicalRecord> getMedicalRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId);
    }
    
    // Read - Get medical records by doctor
    public List<MedicalRecord> getMedicalRecordsByDoctor(String doctorName) {
        return medicalRecordRepository.findByDoctorName(doctorName);
    }
    
    // Read - Search medical records
    public List<MedicalRecord> searchMedicalRecords(String searchTerm) {
        return medicalRecordRepository.searchMedicalRecords(searchTerm);
    }
    
    // Update
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord updatedRecord) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setDiagnosis(updatedRecord.getDiagnosis());
                    record.setSymptoms(updatedRecord.getSymptoms());
                    record.setTreatment(updatedRecord.getTreatment());
                    record.setDoctorName(updatedRecord.getDoctorName());
                    record.setVisitDate(updatedRecord.getVisitDate());
                    return medicalRecordRepository.save(record);
                })
                .orElseThrow(() -> new RuntimeException("Medical record not found with id: " + id));
    }
    
    // Delete
    public void deleteMedicalRecord(Long id) {
        if (medicalRecordRepository.existsById(id)) {
            medicalRecordRepository.deleteById(id);
        } else {
            throw new RuntimeException("Medical record not found with id: " + id);
        }
    }
    
    // Get records by date range
    public List<MedicalRecord> getMedicalRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalRecordRepository.findByVisitDateBetween(startDate, endDate);
    }
}