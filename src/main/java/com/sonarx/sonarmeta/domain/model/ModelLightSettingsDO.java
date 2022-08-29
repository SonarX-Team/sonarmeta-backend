package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 模型光线设置
 * @TableName t_model_light_settings
 */
@TableName(value ="t_model_light_settings")
@Data
public class ModelLightSettingsDO implements Serializable {
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
    private Integer lightSwitch;

    /**
     * 
     */
    private Integer lightOneType;

    /**
     * 
     */
    private String lightOneColor;

    /**
     * 
     */
    private String lightOnePositionX;

    /**
     * 
     */
    private String lightOnePositionY;

    /**
     * 
     */
    private String lightOnePositionZ;

    /**
     * 
     */
    private String lightOneIntensity;

    /**
     * 
     */
    private Integer lightOneCastShadow;

    /**
     * 
     */
    private String lightOneShadowBias;

    /**
     * 
     */
    private Integer lightOneAttachedToCamera;

    /**
     * 
     */
    private String lightOneDecay;

    /**
     * 
     */
    private String lightOneAngle;

    /**
     * 
     */
    private String lightOnePenumbra;

    /**
     * 
     */
    private Integer lightTwoType;

    /**
     * 
     */
    private String lightTwoColor;

    /**
     * 
     */
    private String lightTwoPositionX;

    /**
     * 
     */
    private String lightTwoPositionY;

    /**
     * 
     */
    private String lightTwoPositionZ;

    /**
     * 
     */
    private String lightTwoIntensity;

    /**
     * 
     */
    private Integer lightTwoCastShadow;

    /**
     * 
     */
    private String lightTwoShadowBias;

    /**
     * 
     */
    private Integer lightTwoAttachedToCamera;

    /**
     * 
     */
    private String lightTwoDecay;

    /**
     * 
     */
    private String lightTwoAngle;

    /**
     * 
     */
    private String lightTwoPenumbra;

    /**
     * 
     */
    private Integer lightThreeType;

    /**
     * 
     */
    private String lightThreeColor;

    /**
     * 
     */
    private String lightThreePositionX;

    /**
     * 
     */
    private String lightThreePositionY;

    /**
     * 
     */
    private String lightThreePositionZ;

    /**
     * 
     */
    private String lightThreeIntensity;

    /**
     * 
     */
    private Integer lightThreeCastShadow;

    /**
     * 
     */
    private String lightThreeShadowBias;

    /**
     * 
     */
    private Integer lightThreeAttachedToCamera;

    /**
     * 
     */
    private String lightThreeDecay;

    /**
     * 
     */
    private String lightThreeAngle;

    /**
     * 
     */
    private String lightThreePenumbra;

    /**
     * 
     */
    private Integer envSwitch;

    /**
     * 
     */
    private String envTexture;

    /**
     * 
     */
    private String envOrientation;

    /**
     * 
     */
    private String envBrightness;

    /**
     * 
     */
    private String ambientDirectionalLightBias;

    /**
     * 
     */
    private String ambientDirectionalLightIntensity;

    /**
     * 
     */
    private Integer ambientDirectionalLightSwitch;

    /**
     * 
     */
    private String lightOneGroundColor;

    /**
     * 
     */
    private String lightThreeGroundColor;

    /**
     * 
     */
    private String lightTwoGroundColor;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
