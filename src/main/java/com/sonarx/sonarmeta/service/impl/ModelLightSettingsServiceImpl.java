package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.ModelLightSettingsDO;
import com.sonarx.sonarmeta.service.ModelLightSettingsService;
import com.sonarx.sonarmeta.mapper.ModelLightSettingsMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_model_light_settings(模型光线设置)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class ModelLightSettingsServiceImpl extends ServiceImpl<ModelLightSettingsMapper, ModelLightSettingsDO>
    implements ModelLightSettingsService{

}




