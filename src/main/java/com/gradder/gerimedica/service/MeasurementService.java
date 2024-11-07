package com.gradder.gerimedica.service;

import com.gradder.gerimedica.entity.Measurement;
import com.gradder.gerimedica.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeasurementService {

    private final MeasurementCsvMapper measurementCsvMapper;
    private final MeasurementRepository measurementRepository;

    public void addMeasurements(MultipartFile file) {
        List<Measurement> measurements = measurementCsvMapper.parseFile(file);
        measurementRepository.saveAll(measurements);
    }

    public ByteArrayResource exportMeasurements() {
        List<Measurement> measurements = measurementRepository.findAll();
        String measurementsCsvString = measurementCsvMapper.export(measurements);
        return new ByteArrayResource(measurementsCsvString.getBytes(StandardCharsets.UTF_8));
    }

    public ByteArrayResource getMeasurement(String code){
        Optional<Measurement> measurement = measurementRepository.findById(code);
        if(measurement.isEmpty()) {
            return null;
        }

        String measurementsCsvString = measurementCsvMapper.export(List.of(measurement.get()));
        return new ByteArrayResource(measurementsCsvString.getBytes(StandardCharsets.UTF_8));
    }

    public void deleteAllMeasurements(){
        measurementRepository.deleteAll();
    }
}
