package com.sonarx.sonarmeta.domain.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户和模型对应关系信息
 * @TableName t_user_model_ownership_relation
 */
@TableName(value ="t_user_model_ownership_relation")
@Data
public class UserModelOwnershipRelationDO implements Serializable {
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
     * 模型ID
     */
    private Long modelId;

    /**
     * 拥有权类型
     */
    private Integer ownershipType;

    /**
     * 创建时间
     */
    private Date createTime;

    public UserModelOwnershipRelationDO(String address, Long modelId, Integer ownershipType) {
        this.address = address;
        this.modelId = modelId;
        this.ownershipType = ownershipType;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
