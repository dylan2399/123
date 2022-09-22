package com.nt.rookies.asset.controller;

import com.nt.rookies.asset.dto.ReportDTO;
import com.nt.rookies.asset.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "*")
public class ReportController {
    @Autowired
    private ReportService reportService;
    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("")
    ResponseEntity<List<ReportDTO>> getAll(){
        List<ReportDTO> reports = reportService.getReport();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }
}
