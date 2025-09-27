package org.migranthealth.Base.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.migranthealth.Base.entity.MedicalRecord;
import org.migranthealth.Base.service.MedicalRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    private static final Logger logger = LoggerFactory.getLogger(MedicalRecordController.class);

    @Autowired
    private MedicalRecordService medicalRecordService;

    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        MedicalRecord savedRecord = medicalRecordService.createMedicalRecord(medicalRecord);
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    @PostMapping("/patient/{patientId}")
    public ResponseEntity<?> createMedicalRecordForPatient(
            @PathVariable Long patientId,
            @RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord savedRecord = medicalRecordService.createMedicalRecordForPatient(patientId, medicalRecord);
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            logger.error("Error creating medical record for patient {}: {}", patientId, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/patient/{patientId}/with-file")
    public ResponseEntity<?> createMedicalRecordWithFile(
            @PathVariable Long patientId,
            @RequestParam("diagnosis") String diagnosis,
            @RequestParam(value = "symptoms", required = false) String symptoms,
            @RequestParam(value = "treatment", required = false) String treatment,
            @RequestParam("doctorName") String doctorName,
            @RequestParam("visitDate") String visitDate,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        try {
            MedicalRecord medicalRecord = new MedicalRecord();
            medicalRecord.setDiagnosis(diagnosis);
            medicalRecord.setSymptoms(symptoms);
            medicalRecord.setTreatment(treatment);
            medicalRecord.setDoctorName(doctorName);
            medicalRecord.setVisitDate(LocalDateTime.parse(visitDate));

            if (file != null && !file.isEmpty()) {
                medicalRecord.setFileData(file.getBytes());
                medicalRecord.setFileName(file.getOriginalFilename());
                medicalRecord.setFileType(file.getContentType());
                medicalRecord.setFileSize(file.getSize());
            }

            MedicalRecord savedRecord = medicalRecordService.createMedicalRecordForPatient(patientId, medicalRecord);
            return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("Failed to read file for patient {}: {}", patientId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to read file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            logger.error("Failed to create medical record with file for patient {}: {}", patientId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to create record: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecords();
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMedicalRecordById(@PathVariable Long id) {
        Optional<MedicalRecord> record = medicalRecordService.getMedicalRecordById(id);
        if (record.isPresent()) {
            return new ResponseEntity<>(record.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
        }
    }

    // ✅ FIXED: replaced printStackTrace with proper logging
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getMedicalRecordsByPatientId(@PathVariable Long patientId) {
        try {
            List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByPatientId(patientId);
            return new ResponseEntity<>(records, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching records for patient {}: {}", patientId, e.getMessage(), e);
            return new ResponseEntity<>("Error fetching medical records: " + e.getMessage(),
                                      HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/doctor/{doctorName}")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordsByDoctor(@PathVariable String doctorName) {
        List<MedicalRecord> records = medicalRecordService.getMedicalRecordsByDoctor(doctorName);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<MedicalRecord>> searchMedicalRecords(@RequestParam String term) {
        List<MedicalRecord> records = medicalRecordService.searchMedicalRecords(term);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord medicalRecord) {
        try {
            MedicalRecord updatedRecord = medicalRecordService.updateMedicalRecord(id, medicalRecord);
            return new ResponseEntity<>(updatedRecord, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error updating medical record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadFile(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>("Please select a file to upload", HttpStatus.BAD_REQUEST);
            }

            Optional<MedicalRecord> recordOpt = medicalRecordService.getMedicalRecordById(id);
            if (!recordOpt.isPresent()) {
                return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
            }

            MedicalRecord record = recordOpt.get();
            record.setFileData(file.getBytes());
            record.setFileName(file.getOriginalFilename());
            record.setFileType(file.getContentType());
            record.setFileSize(file.getSize());

            medicalRecordService.updateMedicalRecord(id, record);
            return new ResponseEntity<>("File uploaded successfully", HttpStatus.OK);
        } catch (IOException e) {
            logger.error("Failed to read file for record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Failed to read file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            logger.error("Failed to upload file for record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Failed to upload file: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        try {
            Optional<MedicalRecord> recordOpt = medicalRecordService.getMedicalRecordById(id);
            if (!recordOpt.isPresent()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            MedicalRecord record = recordOpt.get();
            if (record.getFileData() == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(record.getFileType()));
            headers.setContentDispositionFormData("attachment", record.getFileName());
            headers.setContentLength(record.getFileSize());

            return new ResponseEntity<>(record.getFileData(), headers, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error downloading file for record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/file-info")
    public ResponseEntity<?> getFileInfo(@PathVariable Long id) {
        try {
            Optional<MedicalRecord> recordOpt = medicalRecordService.getMedicalRecordById(id);
            if (!recordOpt.isPresent()) {
                return new ResponseEntity<>("Medical record not found", HttpStatus.NOT_FOUND);
            }

            MedicalRecord record = recordOpt.get();
            if (record.getFileData() == null) {
                return new ResponseEntity<>("No file attached to this record", HttpStatus.NOT_FOUND);
            }

            String fileInfo = String.format("{\"fileName\":\"%s\",\"fileType\":\"%s\",\"fileSize\":%d}",
                    record.getFileName(), record.getFileType(), record.getFileSize());
            return new ResponseEntity<>(fileInfo, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error retrieving file info for record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>("Error retrieving file info", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long id) {
        try {
            medicalRecordService.deleteMedicalRecord(id);
            return new ResponseEntity<>("Medical record deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error deleting medical record {}: {}", id, e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
