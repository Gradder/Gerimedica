package com.gradder.gerimedica.repository;

import com.gradder.gerimedica.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, String> {

}
