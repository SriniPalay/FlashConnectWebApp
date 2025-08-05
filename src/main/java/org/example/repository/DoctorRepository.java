package org.example.repository;

import org.example.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // Marks this interface as a Spring repository component
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Spring Data JPA automatically provides standard CRUD methods:
    // save(), findById(), findAll(), deleteById(), etc.
    // We don't need to write any code here for these basic operations.
    List<Doctor> findBySpecialization(String specialization);
}