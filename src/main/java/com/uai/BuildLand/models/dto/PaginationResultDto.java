package com.uai.BuildLand.models.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter @Setter
public class PaginationResultDto {
    private List<?> payload;
    private boolean hasPrevious;
    private boolean hasNext;
}
