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

package io._29cu.usmserver.controllers.rest.resources;

import java.util.ArrayList;
import java.util.List;

import io._29cu.usmserver.core.service.utilities.ApplicationBundleList;

public class ApplicationBundleListResource extends EntityResourceBase<ApplicationBundleList> {

	private List<ApplicationBundleResource> applicationBundles = new ArrayList<>();

    public List<ApplicationBundleResource> getApplicationBundles() {
        return applicationBundles;
    }

    public void setApplicationBundles(List<ApplicationBundleResource> applicationBundles) {
        this.applicationBundles = applicationBundles;
    }

    @Override
    public ApplicationBundleList toEntity() {
        return null;
    }
    
 }
