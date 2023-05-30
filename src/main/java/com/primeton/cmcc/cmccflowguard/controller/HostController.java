package com.primeton.cmcc.cmccflowguard.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.primeton.cmcc.cmccflowguard.entity.HostEntity;
import com.primeton.cmcc.cmccflowguard.entity.vo.HostVO;
import com.primeton.cmcc.cmccflowguard.service.IHostService;
import com.primeton.cmcc.cmccflowguard.util.Result;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/hosts")
public class HostController {

    @Autowired
    private IHostService hostService;

    /**
     * 查询主机列表
     *
     * @param hostVO : 主机查询条件
     * @return : Result
     */
    @ApiOperation(value = "主机管理》查询主机列表", httpMethod = "POST")
    @ResponseBody
    @PostMapping("/host/list")
    public Result list(@RequestBody HostVO hostVO) {
        IPage<HostEntity> hosts = hostService.list(hostVO);
        return Result.succeed(hosts);
    }

    /**
     * 查询主机详情
     *
     * @param hostId : 主机ID
     * @return : Result
     */
    @ApiOperation(value = "主机管理》查询主机详情", httpMethod = "GET")
    @ResponseBody
    @GetMapping("/host/get/{hostId}")
    public Result detail(@PathVariable String hostId) {
        HostEntity host = hostService.get(hostId);
        return Result.succeed(host);
    }

    /**
     * 添加主机
     *
     * @param hostVO : 主机信息
     * @return : Result
     * @throws Exception
     */
    @ApiOperation(value = "主机管理》添加主机", httpMethod = "POST")
    @ResponseBody
    @PostMapping("/host/add")
    public Result add(@RequestBody HostVO hostVO) throws Exception {
        hostService.insert(hostVO);
        return Result.succeed();
    }

    /**
     * 修改主机
     *
     * @param hostVO : 主机信息
     * @return : Result
     * @throws Exception
     */
    @ApiOperation(value = "主机管理》修改主机", httpMethod = "POST")
    @ResponseBody
    @PostMapping("/host/edit")
    public Result update(@RequestBody HostVO hostVO) throws Exception {
        hostService.update(hostVO);
        return Result.succeed();
    }

    /**
     * 删除主机
     *
     * @param hostId : 主机ID
     * @return : Result
     * @throws Exception
     */
    @ApiOperation(value = "主机管理》删除主机", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostId", value = "主机ID", paramType = "query", dataType = "String")
    })
    @ResponseBody
    @DeleteMapping("/host/delete/{hostId}")
    public Result delete(@PathVariable String hostId) throws Exception {
        hostService.delete(hostId);
        return Result.succeed();
    }

    /**
     * 批量删除主机
     *
     * @param hostIds : 主机ID列表，逗号分隔
     * @return : Result
     * @throws Exception
     */
    @ApiOperation(value = "主机管理》批量删除主机", httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hostIds", value = "主机ID列表，逗号分隔", paramType = "query", dataType = "String")
    })
    @ResponseBody
    @DeleteMapping("/host/batchDelete/{hostIds}")
    public Result batchDelete(@PathVariable String hostIds) throws Exception {
        hostService.batchDelete(hostIds);
        return Result.succeed();
    }

    // Other methods for host management (e.g., count, export, etc.) can be added here.

}

