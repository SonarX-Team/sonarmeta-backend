package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.ModelPostprocessingSettingsDO;
import com.sonarx.sonarmeta.service.ModelPostprocessingSettingsService;
import com.sonarx.sonarmeta.mapper.ModelPostprocessingSettingsMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_model_postprocessing_settings(模型后处理设置)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class ModelPostprocessingSettingsServiceImpl extends ServiceImpl<ModelPostprocessingSettingsMapper, ModelPostprocessingSettingsDO>
    implements ModelPostprocessingSettingsService{

}




