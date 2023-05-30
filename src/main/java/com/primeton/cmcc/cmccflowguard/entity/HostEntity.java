package com.primeton.cmcc.cmccflowguard.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fg_hosts")
public class HostEntity {
    @TableId(type = IdType.AUTO)
    private Integer hostId;

    @TableField("host_name")
    private String hostName;

    @TableField("ip_address")
    private String ipAddress;

    private Integer port;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;
}
