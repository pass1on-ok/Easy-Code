package com.easycode.backend.dto.response;

import com.easycode.backend.entity.CourseMaterial;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseMaterialVO {

    private Long id;
    private String title;
    @JsonProperty("file")
    private String fileUrl;
    private String description;

    public static CourseMaterialVO from(CourseMaterial m) {
        return CourseMaterialVO.builder()
                .id(m.getId())
                .title(m.getTitle())
                .fileUrl(m.getFileUrl())
                .description(m.getDescription())
                .build();
    }
}
