package com.gradder.gerimedica.service;

import com.gradder.gerimedica.entity.CsvRow;
import com.gradder.gerimedica.exception.CsvGenerationException;
import lombok.experimental.UtilityClass;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class CsvExportPrinter {
    private static final String CSV_GENERATION_EXCEPTION_MESSAGE = "CSV generation failed";
    private static final CSVFormat DEFAULT_FORMAT = CSVFormat.DEFAULT.
            builder()
            .setHeader()
            .setQuote('"')
            .setQuoteMode(QuoteMode.ALL_NON_NULL)
            .setSkipHeaderRecord(true)
            .setDelimiter(',')
            .setTrim(true)
            .setIgnoreEmptyLines(true)
            .build();

    public static String export(List<String> headers, List<? extends CsvRow> rows) {
        StringWriter writer = new StringWriter();
        try (CSVPrinter csv = new CSVPrinter(writer, DEFAULT_FORMAT)) {
            csv.printRecord(headers);
            for (CsvRow row : rows) {
                csv.printRecord(row.toExportValues().stream().map(value -> value == null ? "" : value).collect(Collectors.toList()));
            }
            csv.flush();
        } catch (IOException e) {
            throw new CsvGenerationException(CSV_GENERATION_EXCEPTION_MESSAGE, e);
        }
        return writer.toString();
    }
}