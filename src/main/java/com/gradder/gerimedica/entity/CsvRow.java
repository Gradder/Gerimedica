package com.gradder.gerimedica.entity;

import java.util.List;

public interface CsvRow {
    List<Object> toExportValues();
}
