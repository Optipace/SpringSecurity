package com.example.auth_service.client;

import com.example.auth_service.dto.AuditRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name="audit-service")
public interface AuditClient {
    @PostMapping("/audit/save")
    void save(@RequestBody AuditRequest auditRequest);
}
