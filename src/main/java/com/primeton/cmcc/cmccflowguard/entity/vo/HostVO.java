package com.primeton.cmcc.cmccflowguard.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HostVO {
    @ApiModelProperty("当前页数")
    private long pageNum;

    @ApiModelProperty("每页条数")
    private long pageSize;

    private Integer hostId;
    private String hostName;
    private String ipAddress;
    private Integer port;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
