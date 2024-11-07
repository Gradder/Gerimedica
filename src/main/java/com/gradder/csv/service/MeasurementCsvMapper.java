package com.gradder.csv.service;

import com.gradder.csv.entity.Measurement;
import com.gradder.csv.exception.InvalidFIleFormatException;
import com.gradder.csv.exception.InvalidFIleHeaderException;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MeasurementCsvMapper {

    public static final String SOURCE_TITLE = "source";
    public static final String CODE_LIST_CODE_TITLE = "codeListCode";
    public static final String CODE_TITLE = "code";
    public static final String DISPLAY_VALUE_TITLE = "displayValue";
    public static final String LONG_DESCRIPTION_TITLE = "longDescription";
    public static final String FROM_DATE_TITLE = "fromDate";
    public static final String TO_DATE_TITLE = "toDate";
    public static final String SORTING_PRIORITY_TITLE = "sortingPriority";
    private static final List<String> HEADERS = List.of(SOURCE_TITLE, CODE_LIST_CODE_TITLE, CODE_TITLE,
            DISPLAY_VALUE_TITLE, LONG_DESCRIPTION_TITLE, FROM_DATE_TITLE, TO_DATE_TITLE, SORTING_PRIORITY_TITLE);

    public static final String CSV_MEDIA_TYPE = "text/csv";
    private static final String CSV_FILE_EXTENSION = ".csv";
    private static final char CSV_COMMA_DELIMITER = ',';
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static final String UNABLE_TO_READ_EXCEPTION_MESSAGE = "Unable to read the file.";
    public static final String HEADERS_IS_MISSING_EXCEPTION_MESSAGE = "A header is missing.";

    public List<Measurement> parseFile(MultipartFile file) {
        validateFileFormat(file);
        Iterable<CSVRecord> records = getCsvRecords(file);
        List<Measurement> result = new ArrayList<>();
        for (CSVRecord csvRecord : records) {
            Measurement discountCardModel = getMeasurement(csvRecord);
            result.add(discountCardModel);
        }
        return result;
    }

    private void validateFileFormat(MultipartFile file) {
        if (!isCorrectFileFormat(file) || !isCorrectFileExtension(file)) {
            throw new InvalidFIleFormatException("Expected a file with .csv extension");
        }
    }

    private boolean isCorrectFileFormat(MultipartFile file) {
        return CSV_MEDIA_TYPE.equals(Optional
                .ofNullable(file.getContentType())
                .orElse(""));
    }

    private boolean isCorrectFileExtension(MultipartFile file) {
        return Optional.ofNullable(file.getOriginalFilename())
                .map(x -> x.endsWith(CSV_FILE_EXTENSION))
                .orElse(false);
    }

    private Iterable<CSVRecord> getCsvRecords(MultipartFile file) {
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setDelimiter(CSV_COMMA_DELIMITER)
                    .build();
            return readCSV(file, csvFormat);
        } catch (IllegalArgumentException exception) {
            handleMissingHeader(exception);
            throw exception;
        }
    }

    private Iterable<CSVRecord> readCSV(MultipartFile file, CSVFormat csvFormat) {
        try {
            return csvFormat.parse(new InputStreamReader(file.getInputStream()));
        } catch (IOException e) {
            throw new InvalidFIleFormatException(UNABLE_TO_READ_EXCEPTION_MESSAGE);
        }
    }

    private Measurement getMeasurement(CSVRecord csvRecord) {
        return Measurement.builder()
                .source(csvRecord.get(SOURCE_TITLE))
                .codeListCode(csvRecord.get(CODE_LIST_CODE_TITLE))
                .code(csvRecord.get(CODE_TITLE))
                .displayValue(csvRecord.get(DISPLAY_VALUE_TITLE))
                .longDescription(csvRecord.get(LONG_DESCRIPTION_TITLE))
                .fromDate(parseTheDate(csvRecord, FROM_DATE_TITLE))
                .toDate(parseTheDate(csvRecord, TO_DATE_TITLE))
                .sortingPriority(parseSortingPriority(csvRecord))
                .build();
    }


    private void handleMissingHeader(IllegalArgumentException exception) {
        if (isHeaderMissing(exception)) {
            throw new InvalidFIleHeaderException(MeasurementCsvMapper.HEADERS_IS_MISSING_EXCEPTION_MESSAGE);
        }
    }

    private boolean isHeaderMissing(IllegalArgumentException exception) {
        return exception.getMessage().startsWith("A header name is missing") || exception.getMessage().startsWith("Mapping for");
    }

    public String export(List<Measurement> measurements) {
        return CsvExportPrinter.export(HEADERS, measurements);
    }

    private LocalDate parseTheDate(CSVRecord csvRecord, String title) {
        String date = csvRecord.get(title);
        return date.isBlank() ? null : LocalDate.parse(csvRecord.get(title), formatter);
    }
    private Integer parseSortingPriority(CSVRecord csvRecord){
        return csvRecord.get(MeasurementCsvMapper.SORTING_PRIORITY_TITLE).isBlank() ? null : Integer.valueOf(csvRecord.get(SORTING_PRIORITY_TITLE));
    }
}