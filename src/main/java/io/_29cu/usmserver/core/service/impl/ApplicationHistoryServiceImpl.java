/**
 * Copyright 2016 - 29cu.io and the authors of alpha-umi open source project

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package io._29cu.usmserver.core.service.impl;

import java.util.List;

import io._29cu.usmserver.core.repositories.ApplicationHistoryRepository;
import io._29cu.usmserver.core.repositories.UserRepository;
import io._29cu.usmserver.core.service.ApplicationHistoryService;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import io._29cu.usmserver.core.model.entities.ApplicationHistory;

import io._29cu.usmserver.common.utilities.AppHelper;
import io._29cu.usmserver.core.model.entities.Application;
import io._29cu.usmserver.core.repositories.ApplicationRepository;
import io._29cu.usmserver.core.repositories.CategoryRepository;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.utilities.ApplicationList;
import io._29cu.usmserver.core.service.utilities.DummyData;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Component
public class ApplicationHistoryServiceImpl implements ApplicationHistoryService {

    @Autowired
    ApplicationHistoryRepository applicationHistoryRepository;

    @Override
    public ApplicationHistory createApplicationHistory(ApplicationHistory applicationHistory) {
        return applicationHistoryRepository.save(applicationHistory);
    }
}
