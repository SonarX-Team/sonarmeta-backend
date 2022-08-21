package com.sonarx.sonarmeta.domain.view;

import com.sonarx.sonarmeta.domain.model.ModelDO;
import com.sonarx.sonarmeta.domain.model.SceneDO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SceneView extends SceneDO {

    private List<ModelDO> modelList;

    public SceneView(SceneDO sceneDO) {
        super(sceneDO);
    }
}
