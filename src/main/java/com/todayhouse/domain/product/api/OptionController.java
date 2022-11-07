package com.todayhouse.domain.product.api;

import com.todayhouse.domain.product.application.OptionService;
import com.todayhouse.domain.product.domain.ChildOption;
import com.todayhouse.domain.product.domain.ParentOption;
import com.todayhouse.domain.product.domain.SelectionOption;
import com.todayhouse.domain.product.dto.request.*;
import com.todayhouse.domain.product.dto.response.ChildOptionResponse;
import com.todayhouse.domain.product.dto.response.ParentOptionResponse;
import com.todayhouse.domain.product.dto.response.SelectionOptionResponse;
import com.todayhouse.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionController {
    private final OptionService optionService;

    @PostMapping("/parent")
    public BaseResponse<ParentOptionResponse> saveParentOption(@Valid @RequestBody ParentOptionSaveRequest request) {
        ParentOption parentOption = optionService.saveParentOption(request);
        return new BaseResponse<>(new ParentOptionResponse(parentOption, false));
    }

    @PostMapping("/child")
    public BaseResponse<ChildOptionResponse> saveChildOption(@Valid @RequestBody ChildOptionSaveRequest request) {
        ChildOption childOption = optionService.saveChildOption(request);
        return new BaseResponse<>(new ChildOptionResponse(childOption));
    }

    @PostMapping("/selection")
    public BaseResponse<SelectionOptionResponse> saveSelectionOption(@Valid @RequestBody SelectionOptionSaveRequest request) {
        SelectionOption selectionOption = optionService.saveSelectionOption(request);
        return new BaseResponse<>(new SelectionOptionResponse(selectionOption));
    }

    @GetMapping("/parents")
    public BaseResponse<Set<ParentOptionResponse>> findParentOptions(@RequestParam Long productId) {
        Set<ParentOption> parents = optionService.findParentOptionsByProductId(productId);
        Set<ParentOptionResponse> response = Optional.ofNullable(parents)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(parentOption -> new ParentOptionResponse(parentOption, false)).collect(Collectors.toSet());
        return new BaseResponse<>(response);
    }

    @GetMapping("/children")
    public BaseResponse<Set<ChildOptionResponse>> findChildOptions(@RequestParam Long parentId) {
        Set<ChildOption> parents = optionService.findChildOptionsByParentId(parentId);
        Set<ChildOptionResponse> response = Optional.ofNullable(parents)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(ChildOptionResponse::new).collect(Collectors.toSet());
        return new BaseResponse<>(response);
    }

    @GetMapping("/selections")
    public BaseResponse<Set<SelectionOptionResponse>> findSelectionOptions(@RequestParam Long productId) {
        Set<SelectionOption> parents = optionService.findSelectionOptionsByProductId(productId);
        Set<SelectionOptionResponse> response = Optional.ofNullable(parents)
                .orElseGet(Collections::emptySet).stream().filter(Objects::nonNull)
                .map(SelectionOptionResponse::new).collect(Collectors.toSet());
        return new BaseResponse<>(response);
    }

    @PutMapping("/parent")
    public BaseResponse<ParentOptionResponse> updateParentOption(@Valid @RequestBody ParentOptionUpdateRequest request) {
        ParentOption parentOption = optionService.updateParentOption(request);
        return new BaseResponse<>(new ParentOptionResponse(parentOption, false));
    }

    @PutMapping("/child")
    public BaseResponse<ChildOptionResponse> updateChildOption(@Valid @RequestBody ChildOptionUpdateRequest request) {
        ChildOption childOption = optionService.updateChildOption(request);
        return new BaseResponse<>(new ChildOptionResponse(childOption));
    }

    @PutMapping("/selection")
    public BaseResponse<SelectionOptionResponse> updateSelectionOption(@Valid @RequestBody SelectionOptionUpdateRequest request) {
        SelectionOption selectionOption = optionService.updateSelectionOption(request);
        return new BaseResponse<>(new SelectionOptionResponse(selectionOption));
    }

    @DeleteMapping("/parent/{id}")
    public BaseResponse<String> deleteParentOption(@PathVariable Long id) {
        optionService.deleteParentOptionById(id);
        return new BaseResponse<>("삭제되었습니다.");
    }

    @DeleteMapping("/child/{id}")
    public BaseResponse<String> deleteChildOption(@PathVariable Long id) {
        optionService.deleteChildOptionById(id);
        return new BaseResponse<>("삭제되었습니다.");
    }

    @DeleteMapping("/selection/{id}")
    public BaseResponse<String> deleteSelectionOption(@PathVariable Long id) {
        optionService.deleteSelectionOptionById(id);
        return new BaseResponse<>("삭제되었습니다.");
    }
}
