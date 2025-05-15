package com.example.catalogs.controller;

import com.example.catalogs.jpa.CatalogEntity;
import com.example.catalogs.service.CatalogService;
import com.example.catalogs.util.ResponseUtil;
import com.example.catalogs.vo.ResponseCatalog;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class CatalogController {
    CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/catalogs")
    public ResponseEntity<List<ResponseCatalog>> getUser() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        Iterable<CatalogEntity> catalogsList = catalogService.getAllCatalogs();

        List<ResponseCatalog> result = new ArrayList<>();
        catalogsList.forEach(f -> {
            result.add(mapper.map(f, ResponseCatalog.class));
        });

        return ResponseUtil.OK(result);
    }
}
