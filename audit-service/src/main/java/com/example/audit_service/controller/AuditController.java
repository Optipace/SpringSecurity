package com.example.audit_service.controller;

import com.example.audit_service.dto.AuditRequest;
import com.example.audit_service.entity.AuditLog;
import com.example.audit_service.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;
    @PostMapping("/save")
    public void save(@RequestBody AuditRequest auditRequest){
        auditService.save(auditRequest.getUsername(),auditRequest.getAction(), auditRequest.getDescription());
    }
}
