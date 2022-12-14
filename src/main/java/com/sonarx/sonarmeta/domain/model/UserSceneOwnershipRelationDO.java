package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户和场景对应关系信息
 * @TableName t_user_scene_ownership_relation
 */
@TableName(value ="t_user_scene_ownership_relation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSceneOwnershipRelationDO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户地址
     */
    private String address;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 拥有权类型
     */
    private Integer ownershipType;

    /**
     * 创建时间
     */
    private Date createTime;


    public UserSceneOwnershipRelationDO(String address, Long sceneId, Integer ownershipType) {
        this.address = address;
        this.sceneId = sceneId;
        this.ownershipType = ownershipType;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
