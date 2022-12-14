package com.todayhouse.domain.category.api;

import com.todayhouse.domain.category.application.CategoryService;
import com.todayhouse.domain.category.domain.Category;
import com.todayhouse.domain.category.dto.request.CategorySaveRequest;
import com.todayhouse.domain.category.dto.request.CategoryUpdateRequest;
import com.todayhouse.domain.category.dto.response.CategoryPathResponse;
import com.todayhouse.domain.category.dto.response.CategoryResponse;
import com.todayhouse.domain.category.dto.response.CategorySaveResponse;
import com.todayhouse.domain.category.dto.response.CategoryUpdateResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public BaseResponse<CategorySaveResponse> saveCategory(@Valid @RequestBody CategorySaveRequest request) {
        Category category = categoryService.addCategory(request);
        return new BaseResponse<>(new CategorySaveResponse(category));
    }

    @GetMapping
    public BaseResponse<List<CategoryResponse>> findAll() {
        List<Category> categories = categoryService.findAllWithChildrenAll();
        List<CategoryResponse> responses = CategoryResponse.createCategoryResponsesAll(categories);
        return new BaseResponse<>(responses);
    }

    // 해당 카테고리의 모든 하위 카테고리
    @GetMapping("/{categoryName}")
    public BaseResponse<CategoryResponse> findWithSubAll(@PathVariable String categoryName) {
        List<Category> categories = categoryService.findOneByNameWithChildrenAll(categoryName);
        CategoryResponse response = CategoryResponse.createCategoryResponse(categories);
        return new BaseResponse<>(response);
    }

    @GetMapping("/path/{categoryName}")
    public BaseResponse<CategoryPathResponse> findRootPath(@PathVariable String categoryName) {
        List<Category> categoryPath = categoryService.findRootPath(categoryName);
        return new BaseResponse<>(new CategoryPathResponse(categoryPath));
    }

    @PatchMapping
    public BaseResponse<CategoryUpdateResponse> updateCategoryName(@Valid @RequestBody CategoryUpdateRequest request) {
        Category category = categoryService.updateCategory(request);
        return new BaseResponse<>(new CategoryUpdateResponse(category));
    }

    @DeleteMapping("/{categoryName}")
    public BaseResponse<String> deleteCategory(@PathVariable String categoryName) {
        categoryService.deleteCategory(categoryName);
        return new BaseResponse<>("삭제되었습니다.");
    }
}
