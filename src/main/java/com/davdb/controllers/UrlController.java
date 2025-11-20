package com.davdb.controllers;


import com.davdb.models.dto.UrlEntryDTO;
import com.davdb.services.UrlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/url")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping(value = "/health", produces = "application/json")
    public String health() {
        return "{\"status\": \"beating\", \"tmstp\":\""+ LocalDateTime.now() +"\"}";
    }

    @PostMapping
    public ResponseEntity<String> saveUrl(@RequestBody UrlEntryDTO entry) throws Exception {
        this.urlService.saveUrlClick(entry);
        return ResponseEntity.ok("👍");
    }

}
