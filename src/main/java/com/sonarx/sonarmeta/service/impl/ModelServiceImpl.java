package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.ModelDO;
import com.sonarx.sonarmeta.service.ModelService;
import com.sonarx.sonarmeta.mapper.ModelMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_model(模型信息)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class ModelServiceImpl extends ServiceImpl<ModelMapper, ModelDO>
    implements ModelService{

}




