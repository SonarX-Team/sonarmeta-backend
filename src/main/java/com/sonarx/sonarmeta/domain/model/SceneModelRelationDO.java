package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 场景和模型对应关系信息
 * @TableName t_scene_model_relation
 */
@TableName(value ="t_scene_model_relation")
@Data
public class SceneModelRelationDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 模型ID
     */
    private Long modelId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}