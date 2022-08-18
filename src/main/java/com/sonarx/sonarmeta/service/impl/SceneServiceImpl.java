package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.SceneDO;
import com.sonarx.sonarmeta.service.SceneService;
import com.sonarx.sonarmeta.mapper.SceneMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_scene(场景信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class SceneServiceImpl extends ServiceImpl<SceneMapper, SceneDO>
    implements SceneService{

}




