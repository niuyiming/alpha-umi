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

package io._29cu.usmserver.controllers.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io._29cu.usmserver.core.model.entities.Application;
import io._29cu.usmserver.core.model.entities.Category;
import io._29cu.usmserver.core.model.entities.Subscription;
import io._29cu.usmserver.core.model.entities.User;
import io._29cu.usmserver.core.model.enumerations.AppState;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.SubscriptionService;
import io._29cu.usmserver.core.service.UserService;
import io._29cu.usmserver.core.service.utilities.ApplicationList;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class SubscriptionControllerTests {
    @InjectMocks
    private SubscriptionController subscriptionController;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private UserService userService;
    @Mock
    private ApplicationService applicationService;



    // @Mock not used here since they are mocked in setup()
    // Spring boot does not allow mocking with annotation.
    SecurityContext securityContextMocked;
    Authentication authenticationMocked;

    private MockMvc mockMvc;

    private User user;
    private Application application;
    private String uuid;
    private Subscription subscription;
    private Subscription unsubscription;


    private ApplicationList appList;
    private String appUUID2;


    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();

        uuid = UUID.randomUUID().toString();
        appUUID2 = UUID.randomUUID().toString();

        user = new User();
        user.setId(1L);
        user.setEmail("owner@test.com");
        user.setUsername("Test Owner");
        user.setEnabled(true);

        application = new Application();
        application.setId(uuid);
        application.setCategory(new Category("Productivity"));
        application.setName("Dreamweaver");
        application.setState(AppState.Active);
        application.setVersion("1.0");
        application.setDeveloper(user);
        application.setDescription("test description");

        List<Application> list = new ArrayList<>();
        appList = new ApplicationList();

        Application appA = new Application();
        appA.setName("Application A");
        appA.setId(uuid);
        appA.setCategory(new Category("Productivity"));
        appA.setState(AppState.Active);
        list.add(appA);

        Application appB = new Application();
        appB.setName("Application B");
        appB.setId(appUUID2);
        appB.setCategory(new Category("Development"));
        appB.setState(AppState.Active);
        list.add(appB);
        
        appList.setApplications(list);

        subscription = new Subscription();
        subscription.setId(1L);
        subscription.setApplication(application);
        subscription.setActive(true);
        subscription.setUser(user);

        unsubscription = new Subscription();
        unsubscription.setId(1L);
        unsubscription.setApplication(application);
        unsubscription.setActive(false);
        unsubscription.setUser(user);

        // Let's mock the security context
        authenticationMocked = Mockito.mock(Authentication.class);
        securityContextMocked = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContextMocked.getAuthentication()).thenReturn(authenticationMocked);
        SecurityContextHolder.setContext(securityContextMocked);
    }

    @Test
    public void  testSubscribeApplicationIsForbidden() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(post("/api/0/store/applications/22/subscribe"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void  testFindSubscriptionByUserIdAndApplicationIdIsForbidden() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/api/0/store/applications/22/checkAppIsSubscribed"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void  testFindSubscriptionByUserIdAndApplicationIdNoContent() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(get("/api/0/store/applications/222/checkAppIsSubscribed"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void  testFindSubscriptionByUserIdAndApplicationIdOk() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.findSubscriptionByUserIdAndApplicationId(user.getId(),uuid)).thenReturn(subscription);

        mockMvc.perform(get("/api/0/store/applications/"+uuid+"/checkAppIsSubscribed"))
                .andExpect(status().isOk());
    }

    @Test
    public void  testSubscribeApplication() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.subscribeApplication(uuid,user)).thenReturn(subscription);
        mockMvc.perform(post("/api/0/store/applications/"+uuid+"/subscribe"))
                .andExpect(status().isOk());
    }

    @Test
    public void  testUnsubscribeApplicationIsForbidden() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(post("/api/0/store/applications/22/unsubscribe"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void  testUnsubscribeApplication() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.unsubscribeApplication(uuid,user)).thenReturn(unsubscription);
        mockMvc.perform(post("/api/0/store/applications/"+uuid+"/unsubscribe"))
                .andExpect(status().isOk());
    }

    @Test
    public void  testGetSubscribedApplications() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(user);
        when(subscriptionService.getSubscribedApplications(user.getId())).thenReturn(appList);

        mockMvc.perform(get("/api/0/store/subscriptions"))
                .andExpect(status().isOk());
    }

    @Test
    public void  testGetSubscribedApplicationsForForbidden() throws Exception {
        when(userService.findAuthenticatedUser()).thenReturn(null);

        mockMvc.perform(get("/api/0/store/subscriptions"))
                .andExpect(status().isForbidden());
    }
}
