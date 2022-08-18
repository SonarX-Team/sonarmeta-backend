package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 模型后处理设置
 * @TableName t_model_postprocessing_settings
 */
@TableName(value ="t_model_postprocessing_settings")
@Data
public class ModelPostprocessingSettingsDO implements Serializable {
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
    private Integer _switch;

    /**
     * 
     */
    private Integer ssaoSwitch;

    /**
     * 
     */
    private String ssaoRadius;

    /**
     * 
     */
    private String ssaoIntensity;

    /**
     * 
     */
    private String ssaoBias;

    /**
     * 
     */
    private Integer chromaticAberrationSwitch;

    /**
     * 
     */
    private String chromaticAberrationOffset;

    /**
     * 
     */
    private Integer vignetteSwitch;

    /**
     * 
     */
    private String vignetteOffset;

    /**
     * 
     */
    private String vignetteDarkness;

    /**
     * 
     */
    private Integer bloomSwitch;

    /**
     * 
     */
    private String bloomIntensity;

    /**
     * 
     */
    private String bloomLuminanceThreshold;

    /**
     * 
     */
    private String bloomRadius;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}