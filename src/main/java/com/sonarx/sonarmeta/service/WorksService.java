package com.sonarx.sonarmeta.service;

import com.sonarx.sonarmeta.domain.common.PageWrapper;
import com.sonarx.sonarmeta.domain.query.AllWorksListQuery;
import com.sonarx.sonarmeta.domain.query.SearchWorksListQuery;
import com.sonarx.sonarmeta.domain.view.WorksView;

public interface WorksService {
    PageWrapper<WorksView> getWorksList(AllWorksListQuery query);

    PageWrapper<WorksView> searchWorksList(SearchWorksListQuery query);
}
