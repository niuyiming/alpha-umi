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

import io._29cu.usmserver.common.utility.AppHelper;
import io._29cu.usmserver.core.model.entity.Application;
import io._29cu.usmserver.core.model.entity.User;
import io._29cu.usmserver.core.repository.ApplicationRepository;
import io._29cu.usmserver.core.repository.UserRepository;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.utility.ApplicationList;
import io._29cu.usmserver.core.service.utility.DummyData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApplicationServiceImpl implements ApplicationService{

    @Autowired
    UserRepository userRepository;

    @Autowired
    ApplicationRepository applicationRepository;

    @Override
    public ApplicationList getAllApplications() {
        DummyData.createDummyData(userRepository, applicationRepository);
        ApplicationList appList = new ApplicationList();
        appList.setApplications(AppHelper.getInstance().convertIterableToList(applicationRepository.findAll()));
        return appList;
    }



    @Override
    public Application createApplication(Application application) {
        return applicationRepository.save(application);
    }

    @Override
    public Application findApplication(Long id) {
        return applicationRepository.findOne(id);
    }

    @Override
    public ApplicationList findApplicationsByDeveloper(Long developerId) {
        List<Application> appList = applicationRepository.findApplicationsByDeveloper(developerId);
        ApplicationList applicationList = new ApplicationList();
        applicationList.setApplications(appList);
        return  applicationList;
    }

}