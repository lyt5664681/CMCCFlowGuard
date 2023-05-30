package com.primeton.cmcc.cmccflowguard.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.primeton.cmcc.cmccflowguard.entity.HostEntity;
import com.primeton.cmcc.cmccflowguard.entity.vo.HostVO;
import com.primeton.cmcc.cmccflowguard.mapper.HostMapper;
import com.primeton.cmcc.cmccflowguard.service.IHostService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Primary
@Service
public class HostServiceImpl implements IHostService {

    @Resource
    private HostMapper hostMapper;

    @Override
    public IPage<HostEntity> list(HostVO hostVO) {
        // 将HostVO转换为QueryWrapper或其他所需格式
        Long currentPage = hostVO.getPageNum();
        Long currentPageSize = hostVO.getPageSize();
        IPage<HostEntity> page = new Page(currentPage, currentPageSize);

        QueryWrapper<HostEntity> queryWrapper = new QueryWrapper<>();
        // 根据HostVO的属性设置查询条件
        // queryWrapper.eq("name", hostVO.getName());
        // queryWrapper.ge("age", hostVO.getAge());

        // 使用queryWrapper执行查询
        // 例如：hostMapper.selectPage(new Page<>(hostVO.getCurrent(), hostVO.getSize()), queryWrapper);
        // 下面的代码仅为示例，请替换为实际的查询执行代码
        IPage<HostEntity> host = hostMapper.selectPage(page,queryWrapper);
        return host;
    }

    @Override
    public HostEntity get(String hostId) {
        // 替换下面的代码为实际的查询执行代码
        return hostMapper.selectById(hostId);
    }

    @Override
    public void insert(HostVO hostVO) throws Exception {
        // 将HostVO转换为HostEntity
        HostEntity hostEntity = new HostEntity();
        // 设置hostEntity的属性值
        // hostEntity.setName(hostVO.getName());
        // hostEntity.setAge(hostVO.getAge());

        // 执行插入操作
        // 下面的代码仅为示例，请替换为实际的插入执行代码
        hostMapper.insert(hostEntity);
    }

    @Override
    public void update(HostVO hostVO) throws Exception {
        // 将HostVO转换为HostEntity
        HostEntity hostEntity = new HostEntity();
        // 设置hostEntity的属性值
        // hostEntity.setId(hostVO.getId());
        // hostEntity.setName(hostVO.getName());
        // hostEntity.setAge(hostVO.getAge());

        // 执行更新操作
        // 下面的代码仅为示例，请替换为实际的更新执行代码
        hostMapper.updateById(hostEntity);
    }

    @Override
    public void delete(String hostId) throws Exception {
        // 执行删除操作
        // 下面的代码仅为示例，请替换为实际的删除执行代码
        hostMapper.deleteById(hostId);
    }

    @Override
    public void batchDelete(String hostIds) throws Exception {
        // 将hostIds转换为主机ID列表
        List<String> hostIdList = Arrays.asList(hostIds.split(","));

        // 执行批量删除操作
        // 下面的代码仅为示例，请替换为实际的批量删除执行代码
        hostMapper.deleteBatchIds(hostIdList);
    }
}
