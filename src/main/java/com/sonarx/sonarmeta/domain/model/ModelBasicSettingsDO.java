package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 模型基本设置
 * @TableName t_model_basic_settings
 */
@TableName(value ="t_model_basic_settings")
@Data
public class ModelBasicSettingsDO implements Serializable {
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
    private String modelRotationX;

    /**
     * 
     */
    private String modelRotationY;

    /**
     * 
     */
    private String modelRotationZ;

    /**
     * 
     */
    private String cameraPositionX;

    /**
     * 
     */
    private String cameraPositionY;

    /**
     * 
     */
    private String cameraPositionZ;

    /**
     * 
     */
    private String fov;

    /**
     * 
     */
    private Integer backgroundSwitch;

    /**
     * 
     */
    private Integer backgroundChoice;

    /**
     * 
     */
    private String backgroundColor;

    /**
     * 
     */
    private String backgroundImage;

    /**
     * 
     */
    private String backgroundEnv;

    /**
     * 
     */
    private String backgroundEnvBrightness;

    /**
     * 
     */
    private Integer shading;

    /**
     * 
     */
    private String near;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
