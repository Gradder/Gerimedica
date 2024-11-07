package com.gradder.csv.controller;

import com.gradder.csv.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadCsv(@RequestParam("file") MultipartFile file) {
        measurementService.addMeasurements(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Resource> getMeasurements() {
        ByteArrayResource csvFile = measurementService.exportMeasurements();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=measurements.csv")
                .body(csvFile);
    }

    @GetMapping("/{code}")
    public ResponseEntity<Resource> getMeasurementByCode(@PathVariable String code) {
        ByteArrayResource csvFile = measurementService.getMeasurement(code);
        return csvFile == null ?
                ResponseEntity.notFound().build() :
                ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=measurements.csv")
                        .body(csvFile);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMeasurements() {
        measurementService.deleteAllMeasurements();
        return ResponseEntity.noContent().build();
    }

}
