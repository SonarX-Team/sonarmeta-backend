package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.common.BusinessException;
import com.sonarx.sonarmeta.domain.common.PageWrapper;
import com.sonarx.sonarmeta.domain.query.AllWorksListQuery;
import com.sonarx.sonarmeta.domain.query.SearchWorksListQuery;
import com.sonarx.sonarmeta.domain.view.WorksView;

import java.util.List;
import java.util.Map;

public interface WorksService {
    PageWrapper<WorksView> getWorksList(AllWorksListQuery query);

    PageWrapper<WorksView> searchWorksList(SearchWorksListQuery query);

    Map<Integer, List<WorksView>> getWorksByUserAddress(String userAddress) throws BusinessException;
}
