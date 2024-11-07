package com.gradder.csv.entity;

import java.util.List;

public interface CsvRow {
    List<Object> toExportValues();
}
