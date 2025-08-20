package com.company.jmixdatastore.service.minio.impl;

import com.company.jmixdatastore.service.minio.KeyNamingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.apache.commons.compress.utils.ArchiveUtils.sanitize;

@Component
public class DefaultKeyNamingStrategy implements KeyNamingStrategy {
    @Override
    public String buildKey(String tenantId, String userId, String originalFilename) {
        String safe = sanitize(originalFilename);
        //String date = java.time.LocalDate.now().toString(); // YYYY-MM-DD
        return "Upload/%s".formatted(safe);
    }
    private boolean nullOrBlank(String s) { return s == null || s.isBlank(); }

}
