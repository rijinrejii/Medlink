package org.migranthealth.Base.service;

import java.util.List;
import java.util.Optional;

import org.migranthealth.Base.entity.Patient;
import org.migranthealth.Base.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    // Create
    public Patient createPatient(Patient patient) {
        if (patientRepository.existsByEmail(patient.getEmail())) {
            throw new RuntimeException("Patient with email " + patient.getEmail() + " already exists");
        }
        return patientRepository.save(patient);
    }
    
    // Read - Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    // Read - Get patient by ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    // Read - Get patient by email
    public Optional<Patient> getPatientByEmail(String email) {
        return patientRepository.findByEmail(email);
    }
    
    // Read - Search patients
    public List<Patient> searchPatients(String searchTerm) {
        return patientRepository.searchPatients(searchTerm);
    }
    
    // Update
    public Patient updatePatient(Long id, Patient updatedPatient) {
        return patientRepository.findById(id)
                .map(patient -> {
                    patient.setFirstName(updatedPatient.getFirstName());
                    patient.setLastName(updatedPatient.getLastName());
                    patient.setEmail(updatedPatient.getEmail());
                    patient.setPhoneNumber(updatedPatient.getPhoneNumber());
                    patient.setDateOfBirth(updatedPatient.getDateOfBirth());
                    patient.setAddress(updatedPatient.getAddress());
                    patient.setNationality(updatedPatient.getNationality());
                    patient.setGender(updatedPatient.getGender());
                    return patientRepository.save(patient);
                })
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + id));
    }
    
    // Delete
    public void deletePatient(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
        } else {
            throw new RuntimeException("Patient not found with id: " + id);
        }
    }
    
    // Get patients by nationality
    public List<Patient> getPatientsByNationality(String nationality) {
        return patientRepository.findByNationality(nationality);
    }
}