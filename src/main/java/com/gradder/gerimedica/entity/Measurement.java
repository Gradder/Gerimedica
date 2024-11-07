package com.gradder.gerimedica.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Measurement implements CsvRow{

    private String source;
    private String codeListCode;
    @Id
    private String code;
    private String displayValue;
    private String longDescription;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer sortingPriority;

    @Override
    public List<Object> toExportValues() {
        return Arrays.asList(
                source,
                codeListCode,
                code,
                displayValue,
                longDescription,
                fromDate,
                toDate,
                sortingPriority
        );
    }
}
