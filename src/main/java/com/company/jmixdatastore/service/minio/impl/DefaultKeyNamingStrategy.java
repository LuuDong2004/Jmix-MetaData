package com.company.jmixdatastore.service.minio.impl;

import com.company.jmixdatastore.service.minio.KeyNamingStrategy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DefaultKeyNamingStrategy implements KeyNamingStrategy {
    @Override
    public String buildKey(String tenantId, String userId, String originalFilename) {
        String safe = (originalFilename == null ? "file" : originalFilename)
                .replaceAll("[\\\\/\\s]+", "-");
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return "tenant/%s/user/%s/%s/%s".formatted(
                nullOrBlank(tenantId) ? "defaultTenant" : tenantId,
                nullOrBlank(userId)  ? "unknown"       : userId,
                date,
                safe
        );
    }

    private boolean nullOrBlank(String s) { return s == null || s.isBlank(); }

}
