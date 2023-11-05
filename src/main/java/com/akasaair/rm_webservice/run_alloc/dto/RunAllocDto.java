package com.akasaair.rm_webservice.run_alloc.dto;


import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RunAllocDto {
    private String runId;
    private String type;
    private String dtd;
    private String count;
    private String start_time;
    private String end_time;
    private String user;
    private String runTime;
    private String allocationPer;
    private String updatePer;
    private String marketListId;
    private String curvesId;
    private String strategyId;
    private String qpFaresId;
    private String tableName;
}
