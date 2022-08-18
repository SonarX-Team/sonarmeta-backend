package com.sonarx.sonarmeta.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sonarx.sonarmeta.domain.model.ModelMaterialSettingsDO;
import com.sonarx.sonarmeta.service.ModelMaterialSettingsService;
import com.sonarx.sonarmeta.mapper.ModelMaterialSettingsMapper;
import org.springframework.stereotype.Service;

/**
* @author hinsliu
* @description 针对表【t_model_material_settings(模型材料设置)】的数据库操作Service实现
* @createDate 2022-08-18 21:40:29
*/
@Service
public class ModelMaterialSettingsServiceImpl extends ServiceImpl<ModelMaterialSettingsMapper, ModelMaterialSettingsDO>
    implements ModelMaterialSettingsService{

}




