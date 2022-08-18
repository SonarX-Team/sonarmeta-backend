package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.ModelBasicSettingsDO;
import com.sonarx.sonarmeta.service.ModelBasicSettingsService;
import com.sonarx.sonarmeta.mapper.ModelBasicSettingsMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_model_basic_settings(模型基本设置)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class ModelBasicSettingsServiceImpl extends ServiceImpl<ModelBasicSettingsMapper, ModelBasicSettingsDO>
    implements ModelBasicSettingsService{

}




