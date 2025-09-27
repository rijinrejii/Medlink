package org.migranthealth.Base.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.migranthealth.Base.entity.Patient;
import org.migranthealth.Base.service.PatientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private PatientService patientService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validate input
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return new ResponseEntity<>("Email and password are required", HttpStatus.BAD_REQUEST);
            }

            // Find patient by email
            Optional<Patient> patientOpt = patientService.getPatientByEmail(loginRequest.getEmail());
            
            if (patientOpt.isPresent()) {
                Patient patient = patientOpt.get();
                
                // Simple password comparison (you should use proper hashing in production)
                if (patient.getPassword().equals(loginRequest.getPassword())) {
                    // Create response without password
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "Login successful");
                    response.put("patient", createPatientResponse(patient));
                    
                    logger.info("Successful login for user: {}", patient.getEmail());
                    return new ResponseEntity<>(response, HttpStatus.OK);
                } else {
                    logger.warn("Failed login attempt for email: {} - invalid password", loginRequest.getEmail());
                    return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
                }
            } else {
                logger.warn("Failed login attempt for email: {} - user not found", loginRequest.getEmail());
                return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            logger.error("Login error for email: {}", loginRequest.getEmail(), e);
            return new ResponseEntity<>("Login failed. Please try again.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Map<String, Object> createPatientResponse(Patient patient) {
        Map<String, Object> patientData = new HashMap<>();
        patientData.put("id", patient.getId());
        patientData.put("firstName", patient.getFirstName());
        patientData.put("lastName", patient.getLastName());
        patientData.put("email", patient.getEmail());
        patientData.put("phoneNumber", patient.getPhoneNumber());
        patientData.put("dateOfBirth", patient.getDateOfBirth());
        patientData.put("address", patient.getAddress());
        patientData.put("nationality", patient.getNationality());
        patientData.put("gender", patient.getGender());
        patientData.put("createdAt", patient.getCreatedAt());
        patientData.put("updatedAt", patient.getUpdatedAt());
        // Explicitly exclude password
        return patientData;
    }

    // Inner class for login request
    public static class LoginRequest {
        private String email;
        private String password;

        // Constructors
        public LoginRequest() {}

        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}