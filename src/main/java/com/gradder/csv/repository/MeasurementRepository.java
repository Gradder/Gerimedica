package com.gradder.csv.repository;

import com.gradder.csv.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, String> {

}
