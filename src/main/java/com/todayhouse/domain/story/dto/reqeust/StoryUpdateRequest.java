package com.todayhouse.domain.story.dto.reqeust;

import com.todayhouse.domain.story.domain.FamilyType;
import com.todayhouse.domain.story.domain.ResiType;
import com.todayhouse.domain.story.domain.Story;
import com.todayhouse.domain.story.domain.StyleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoryUpdateRequest {
    @Length(min = 1, max = 50, message = "제목은 1자 이상 50자 이하로 입력해주세요.")
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private Story.Category category;
    @NotNull
    private ResiType resiType;
    @Positive
    private Integer floorSpace;
    @NotNull
    private FamilyType familyType;
    @NotNull
    private StyleType styleType;
}
