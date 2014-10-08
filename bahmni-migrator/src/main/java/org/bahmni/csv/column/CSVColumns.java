package org.bahmni.csv.column;

import org.bahmni.csv.CSVEntity;
import org.bahmni.csv.annotation.CSVHeader;
import org.bahmni.csv.annotation.CSVRegexHeader;
import org.bahmni.csv.annotation.CSVRepeatingHeaders;
import org.bahmni.csv.annotation.CSVRepeatingRegexHeaders;
import org.bahmni.csv.exception.MigrationException;

import java.lang.reflect.Field;

public class CSVColumns<T extends CSVEntity> {
    private final String[] headerNames;

    public CSVColumns(String[] headerNames) {
        this.headerNames = headerNames;
    }

    public void setValue(Object entity, Field field, String[] aRow) throws IllegalAccessException, InstantiationException {
        if (field.getAnnotation(CSVRepeatingHeaders.class) != null) {
            new RepeatingCSVColumns(headerNames).setValue(entity, field, aRow);
        } else if (field.getAnnotation(CSVHeader.class) != null) {
            addColumnValue(entity, field, aRow);
        } else if (field.getAnnotation(CSVRegexHeader.class) != null) {
            new RegexCSVColumns(headerNames).setValue(entity, field, aRow);
        } else if (field.getAnnotation(CSVRepeatingRegexHeaders.class) != null) {
            new RepeatingRegexCSVColumns(headerNames).setValue(entity, field, aRow);
        }
    }

    private void addColumnValue(Object entity, Field field, String[] aRow) throws IllegalAccessException {
        CSVHeader headerAnnotation = field.getAnnotation(CSVHeader.class);
        field.setAccessible(true);
        field.set(entity, aRow[getPosition(headerAnnotation.name(), 0)]);
    }

    protected int getPosition(String headerValueInClass, int startIndex) {
        for (int i = startIndex; i < headerNames.length; i++) {
            String headerName = headerNames[i];
            if (headerName.toLowerCase().startsWith(headerValueInClass.toLowerCase()))
                return i;
        }
        throw new MigrationException("No Column found in the csv file. " + headerValueInClass);
    }
}