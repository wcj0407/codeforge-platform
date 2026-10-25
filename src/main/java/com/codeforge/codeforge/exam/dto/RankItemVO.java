package com.codeforge.codeforge.exam.dto;

import lombok.Data;

@Data
public class RankItemVO {
    private Integer rank;
    private Long userId;
    private String username;
    private Integer solvedCount;
}