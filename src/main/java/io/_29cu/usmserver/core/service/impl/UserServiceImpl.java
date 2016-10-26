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

import io._29cu.usmserver.core.model.entities.AuUser;
import io._29cu.usmserver.core.model.entities.Authority;
import io._29cu.usmserver.core.model.entities.User;
import io._29cu.usmserver.core.model.enumerations.AuthorityName;
import io._29cu.usmserver.core.repositories.AuUserRepository;
import io._29cu.usmserver.core.repositories.AuthorityRepository;
import io._29cu.usmserver.core.service.SecurityContextService;
import io._29cu.usmserver.core.service.UserService;
import io._29cu.usmserver.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.*;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuUserRepository auUserRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private SecurityContextService securityContextService;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUser(String id) {
        return userRepository.findOne(id);
    }

    @Override
    public User findUserByPrincipal(String principal){
        return userRepository.findUserByPrincipal(principal);
    }

    @Override
    public User createUser(Principal principal) {
        OAuth2Authentication oAuth2Authentication = (OAuth2Authentication) principal;
        User user = new User();
        HashMap details = (LinkedHashMap)oAuth2Authentication.getUserAuthentication().getDetails();
        user.setName(details.get("name").toString());
        user.setPrincipal(principal.getName());
        return userRepository.save(user);
    }

    @Override
    public User validateUserIdWithPrincipal(String userId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof String)) return null;

        User user = findUserByPrincipal(principal.toString());
        return (userId.equals(user.getId())) ? user : null;
    }

    @Override
    public AuUser createUser(AuUser auUser) {
        AuUser newUser = auUser;
        // Grant User role by default.
        Authority authority = new Authority();
        authority.setName(AuthorityName.ROLE_USER);
        authorityRepository.save(authority);
        List<Authority> authorities = new ArrayList<>();
        authorities.add(authority);

        authority = new Authority();
        authority.setName(AuthorityName.ROLE_DEVELOPER);
        authorityRepository.save(authority);
        authorities.add(authority);

        newUser.setAuthorities(authorities);

        return auUserRepository.save(newUser);
    }

    @Override
    public AuUser findUser() {
        final AuUser currentUser = securityContextService.getLoggedInUser();
        return auUserRepository.findOne(currentUser.getId());
    }
}
