package com.company.jmixdatastore.service.dbcon;

import com.company.jmixdatastore.entity.TableGroup;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableGroupingService {

    // Domain keywords mapping
    private static final Map<String, List<String>> DOMAIN_KEYWORDS = Map.of(
        "payment", Arrays.asList("payment", "transaction", "billing", "invoice", "charge", "refund", "wallet"),
        "user", Arrays.asList("user", "account", "profile", "member", "customer", "person", "auth", "login"),
        "booking", Arrays.asList("booking", "reservation", "schedule", "appointment", "order", "ticket"),
        "product", Arrays.asList("product", "item", "catalog", "inventory", "stock", "goods"),
        "content", Arrays.asList("blog", "post", "article", "content", "news", "media", "comment"),
        "system", Arrays.asList("log", "audit", "config", "setting", "system", "admin"),
        "location", Arrays.asList("address", "location", "region", "city", "country", "branch"),
        "cinema", Arrays.asList("cinema", "movie", "film", "actor", "screening", "theater", "combo", "food")
    );

    public List<TableGroup> groupTablesByDomain(List<String> tableNames) {
        Map<String, TableGroup> domainGroups = new HashMap<>();

        // Initialize domain groups
        for (String domain : DOMAIN_KEYWORDS.keySet()) {
            domainGroups.put(domain, new TableGroup(
                capitalizeFirst(domain), 
                domain
            ));
        }

        // Create "Other" group for unmatched tables
        TableGroup otherGroup = new TableGroup("Other", "other");
        domainGroups.put("other", otherGroup);

        // Classify each table
        for (String tableName : tableNames) {
            String domain = classifyTable(tableName.toLowerCase());
            domainGroups.get(domain).addTable(tableName);
        }

        // Filter out empty groups and return
        return domainGroups.values().stream()
                .filter(group -> !group.getTables().isEmpty())
                .sorted(Comparator.comparing(TableGroup::getName))
                .collect(Collectors.toList());
    }

    private String classifyTable(String tableName) {
        for (Map.Entry<String, List<String>> entry : DOMAIN_KEYWORDS.entrySet()) {
            String domain = entry.getKey();
            List<String> keywords = entry.getValue();

            for (String keyword : keywords) {
                if (tableName.contains(keyword)) {
                    return domain;
                }
            }
        }
        return "other";
    }

    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    public TableGroup findGroupContainingTable(List<TableGroup> groups, String tableName) {
        return groups.stream()
                .filter(group -> group.getTables().contains(tableName))
                .findFirst()
                .orElse(null);
    }
}
