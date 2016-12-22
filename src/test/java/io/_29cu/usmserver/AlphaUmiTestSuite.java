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

package io._29cu.usmserver;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ActiveProfiles;

import io._29cu.usmserver.common.utilities.AppHelperTests;
import io._29cu.usmserver.controllers.rest.ApplicationControllerTests;
import io._29cu.usmserver.controllers.rest.DeveloperApplicationsControllerTests;
import io._29cu.usmserver.controllers.rest.DeveloperProfileControllerTests;
import io._29cu.usmserver.controllers.rest.StoreControllerTests;
import io._29cu.usmserver.controllers.rest.SubscriptionControllerTests;
import io._29cu.usmserver.core.repositories.UserRepositoryTests;
import io._29cu.usmserver.core.service.ApplicationServiceTests;
import io._29cu.usmserver.core.service.SubscriptionServiceTests;
import io._29cu.usmserver.core.service.UserServiceTests;
import io._29cu.usmserver.core.service.utilities.ApplicationListTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    AlphaUmiApplicationTests.class,
    UserServiceTests.class,
    UserRepositoryTests.class,
    ApplicationServiceTests.class,
    AppHelperTests.class,
    ApplicationListTests.class,
    StoreControllerTests.class,
    ApplicationControllerTests.class,
    DeveloperApplicationsControllerTests.class,
    DeveloperProfileControllerTests.class, 
    SubscriptionControllerTests.class,
    SubscriptionServiceTests.class
})
@ActiveProfiles("test")
public class AlphaUmiTestSuite {
}
