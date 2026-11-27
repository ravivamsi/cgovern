package com.cgovern.cgovern.web;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cgovern.cgovern.domain.FeatureModule;
import com.cgovern.cgovern.domain.Organization;
import com.cgovern.cgovern.service.CatalogService;

@RestController
@RequestMapping("/api/v2")
public class ApiController {

    private final CatalogService catalog;

    public ApiController(CatalogService catalog) {
        this.catalog = catalog;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("appname", "cgovern", "status", "UP");
    }

    @GetMapping("/features/{module}/orgs")
    public List<Organization> orgs(@PathVariable String module) {
        return catalog.orgs(FeatureModule.fromSlug(module));
    }
}
