package com.akasaair.rm_webservice.file_generation.dto;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity()
@Table(name = "market_list")
@EntityListeners(AuditingEntityListener.class)
public class MarketListEntity {
    @Column(name = "UUID")
    private String uuid;
    @Column(name = "Origin")
    private String origin;
    @Column(name = "Destin")
    private String destination;
    @Column(name = "FlightNumber")
    private String flightNo;
    @Column(name = "PerType")
    private String perType;
    @Column(name = "PerStart")
    private String startDate;
    @Column(name = "PerEnd")
    private String endDate;
    @Column(name = "DOW")
    private String dayOfWeek;
    @Column(name = "strategyFlag")
    private String strategyFlag;
    @Column(name = "TimeWindowStart")
    private String startTime;
    @Column(name = "TimeWindowEnd")
    private String endTime;
    @Column(name = "CurveID")
    private String curveId;
    @Column(name = "CarrExlusion")
    private String carrExclusion;
    @Column(name = "fareAnchor")
    private String fareAnchor;
    @Column(name = "fareOffset")
    private String fareOffset;
    @Column(name = "FirstRBDAlloc")
    private String firstAllocation;
    @Column(name = "OtherRBDAlloc")
    private String otherAllocation;
    @Column(name = "B2BBackstop")
    private String b2bBackstop;
    @Column(name = "B2CBackstop")
    private String b2cBackstop;
    @Column(name = "B2BFactor")
    private String b2bFactor;
    @Column(name = "SkippingFactor")
    private String skippingFactor;
    @Column(name = "analystName")
    private String analystName;
}
