package com.example.service;

import com.example.model.Recipe;
import com.example.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final RecipeRepository recipeRepository;

    @Transactional(readOnly = true)
    public List<Recipe> search(String text) {
        return recipeRepository.searchByNameOrCuisine(text);
    }

    @Transactional
    public void reindex() {
        log.info("No reindexing needed with standard JPA search");
    }
}