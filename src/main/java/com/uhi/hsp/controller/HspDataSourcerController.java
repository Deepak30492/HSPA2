package com.uhi.hsp.controller;

import com.uhi.hsp.model.Categories;
import com.uhi.hsp.model.Provider;
import com.uhi.hsp.repository.CategoriesRepository;
import com.uhi.hsp.repository.ProviderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/data")
public class HspDataSourcerController {

    private final ProviderRepository _providerRepo;
    private final CategoriesRepository _categoryRepo;

    public HspDataSourcerController(ProviderRepository _providerRepo, CategoriesRepository categoryRepo) {
        this._providerRepo = _providerRepo;
        this._categoryRepo = categoryRepo;
    }

    @PostMapping("/addProvider")
    public ResponseEntity<String> addProvider(@RequestBody Provider provider) {
        _providerRepo.save(provider);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/addCagtegories")
    public ResponseEntity<String> addProvider(@RequestBody Categories categories) {
        _categoryRepo.save(categories);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
