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

import io._29cu.usmserver.common.utility.AppHelperTests;
import io._29cu.usmserver.core.repository.UserRepositoryTests;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.ApplicationServiceTests;
import io._29cu.usmserver.core.service.UserServiceTests;
import io._29cu.usmserver.core.service.utility.ApplicationListTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.springframework.test.context.ActiveProfiles;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AlphaUmiApplicationTests.class,
        UserServiceTests.class,
        UserRepositoryTests.class,
        ApplicationServiceTests.class,
        AppHelperTests.class,
        ApplicationListTests.class
        })
@ActiveProfiles("test")
public class AlphaUmiTestSuite {
}