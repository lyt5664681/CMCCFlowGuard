package com.primeton.cmcc.cmccflowguard.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.primeton.cmcc.cmccflowguard.entity.HostEntity;
import com.primeton.cmcc.cmccflowguard.entity.vo.HostVO;

import java.util.List;

public interface IHostService {
    /**
     * 查询主机列表
     *
     * @param hostVO 主机查询条件
     * @return 主机分页结果
     */
    IPage<HostEntity> list(HostVO hostVO);

    /**
     * 查询主机详情
     *
     * @param hostId 主机ID
     * @return 主机详情
     */
    HostEntity get(String hostId);

    /**
     * 添加主机
     *
     * @param hostVO 主机信息
     * @throws Exception 添加失败时抛出异常
     */
    void insert(HostVO hostVO) throws Exception;

    /**
     * 修改主机
     *
     * @param hostVO 主机信息
     * @throws Exception 修改失败时抛出异常
     */
    void update(HostVO hostVO) throws Exception;

    /**
     * 删除主机
     *
     * @param hostId 主机ID
     * @throws Exception 删除失败时抛出异常
     */
    void delete(String hostId) throws Exception;

    /**
     * 批量删除主机
     *
     * @param hostIds 主机ID列表，逗号分隔
     * @throws Exception 删除失败时抛出异常
     */
    void batchDelete(String hostIds) throws Exception;
}
