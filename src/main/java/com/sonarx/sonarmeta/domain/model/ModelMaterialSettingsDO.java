package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 模型材料设置
 * @TableName t_model_material_settings
 */
@TableName(value ="t_model_material_settings")
@Data
public class ModelMaterialSettingsDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    private Long modelId;

    /**
     * 
     */
    private String textures;

    /**
     * 
     */
    private Integer currentPbrWorkflow;

    /**
     * 
     */
    private String parameters;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}