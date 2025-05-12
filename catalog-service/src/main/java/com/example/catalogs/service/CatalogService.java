package com.example.catalogs.service;

import com.example.catalogs.jpa.CatalogEntity;

public interface CatalogService {
    Iterable<CatalogEntity> getAllCatalogs();
}
