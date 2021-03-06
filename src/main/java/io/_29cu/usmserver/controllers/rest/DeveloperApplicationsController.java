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

import java.nio.file.Path;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import io._29cu.usmserver.common.utilities.AppConstants;
import io._29cu.usmserver.controllers.rest.resources.ApplicationListResource;
import io._29cu.usmserver.controllers.rest.resources.ApplicationResource;
import io._29cu.usmserver.controllers.rest.resources.ApplicationUpdateResource;
import io._29cu.usmserver.controllers.rest.resources.assemblers.ApplicationListResourceAssembler;
import io._29cu.usmserver.controllers.rest.resources.assemblers.ApplicationResourceAssembler;
import io._29cu.usmserver.controllers.rest.resources.assemblers.ApplicationUpdateResourceAssembler;
import io._29cu.usmserver.core.model.entities.Application;
import io._29cu.usmserver.core.model.entities.ApplicationHistory;
import io._29cu.usmserver.core.model.entities.ApplicationUpdate;
import io._29cu.usmserver.core.model.entities.User;
import io._29cu.usmserver.core.model.enumerations.AppState;
import io._29cu.usmserver.core.service.ApplicationHistoryService;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.ApplicationUpdateService;
import io._29cu.usmserver.core.service.CategoryService;
import io._29cu.usmserver.core.service.StorageService;
import io._29cu.usmserver.core.service.UserService;
import io._29cu.usmserver.core.service.utilities.ApplicationList;

@Controller
@RequestMapping("/api/0/developer")
public class DeveloperApplicationsController {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationHistoryService applicationHistoryService;
    @Autowired
    private ApplicationUpdateService applicationUpdateService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private StorageService storageService;
    private final Log logger = LogFactory.getLog(this.getClass());

    // Skeleton methods
    // Add similar methods for create, update and publish updates
    // And publish application

    /**
     * Get all applications
     * @return The ApplicationListResource found
     * @see ApplicationListResource
     */
    @RequestMapping(path = "/applications", method = RequestMethod.GET)
    public ResponseEntity<ApplicationListResource> getApplications(){
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            ApplicationList appList = applicationService.findApplicationsByDeveloper(user.getId());
            ApplicationListResource appListResource = new ApplicationListResourceAssembler().toResource(appList);
            return new ResponseEntity<>(appListResource, HttpStatus.OK);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Get application by application id
	 * @param appId The application id to be search by
	 * @return The ApplicationResource created
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/{appId}", method = RequestMethod.GET)
    public ResponseEntity<ApplicationResource> getApplication(
            @PathVariable String appId
    ){
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
            ApplicationResource applicationResource = new ApplicationResourceAssembler().toResource(application);
            return new ResponseEntity<>(applicationResource, HttpStatus.OK);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Upload application logo
	 * @param appId The id of the application
	 * @param file The image file for the logo
	 * @return
	 * @see MultipartFile
	 */
    @RequestMapping(path = "/applications/{appId}/image", method = RequestMethod.POST)
    public ResponseEntity<String> uploadApplicationLogo(
            @PathVariable String appId,
            @RequestParam("file") MultipartFile file) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
            Path generatedFile = storageService.storeApplicationLogo(file, user.getId(), appId);
            String response = "{'originalName': '" + file.getOriginalFilename() + "', 'generatedName': '" + generatedFile.getFileName() + "'}";
            response = response.replace("'", "\"");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Get application logo
	 * @param appId The id of the application
	 * @return
	 * @see Resource
	 */
    @RequestMapping(path = "/applications/{appId}/image", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> getApplicationLogo(
            @PathVariable String appId) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
        Resource file = storageService.loadApplicationLogoAsResource(user.getId(), appId);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+file.getFilename()+"\"")
                .header(HttpHeaders.CONTENT_TYPE, "image/png")
                .body(file);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            // Maybe the image is not yet uploaded, so let's just return ok.
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

	/**
	 * Create Application
	 * @param applicationResource The application to be created
	 * @return The application created
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/create", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> createDeveloperApplication(
            @RequestBody ApplicationResource applicationResource
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Application receivedApplication = applicationResource.toEntity();
        receivedApplication.setDeveloper(user);
        receivedApplication.setCategory(categoryService.findCategory(Long.valueOf(receivedApplication.getCategory().getName())));
        receivedApplication.setState(AppState.Staging);
        Application application = applicationService.createApplication(receivedApplication);
        ApplicationResource createdApplicationResource = new ApplicationResourceAssembler().toResource(application); 
        return new ResponseEntity<>(createdApplicationResource, HttpStatus.OK);
    }

	/**
	 * Check whether the application name already exists under the same developer
	 * @param name The application name to be checked
	 * @return The application with the bundle name under the same developer
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/check", method = RequestMethod.GET)
    public ResponseEntity<ApplicationResource> checkApplicationNameExistsForDeveloper(
            @RequestParam String name
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        //Let's check whether the application is already registered.
        Application existingApp = applicationService.findApplicationByDeveloperIdAndAppName(user.getId(), name);
        if (null == existingApp) { //We can't find the application in our database for the developer.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            // Application with same name already exists 
        	return new ResponseEntity<>(HttpStatus.OK);
        }
    }

	/**
	 * Create and publish application
	 * @param applicationResource The application to be created and published
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/publish", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> createAndPublishDeveloperApplication(
            @RequestBody ApplicationResource applicationResource
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Application receivedApplication = applicationResource.toEntity();
        receivedApplication.setDeveloper(user);
        receivedApplication.setCategory(categoryService.findCategory(Long.valueOf(receivedApplication.getCategory().getName())));
        receivedApplication.setState(AppState.Active);
        receivedApplication.setApplicationPublishDate(Calendar.getInstance().getTime());
        Application application = applicationService.createApplication(receivedApplication);
        ApplicationResource createdApplicationResource = new ApplicationResourceAssembler().toResource(application); 
        return new ResponseEntity<>(createdApplicationResource, HttpStatus.OK);
    }

	/**
	 * Update developer application
	 * @param appId The id of the application to be updated
	 * @param applicationResource The updated application instance
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/{appId}/update", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> updateDeveloperApplication(
            @PathVariable String appId,
            @RequestBody ApplicationResource applicationResource
    ) {
	    // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	    Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
	    if (application == null)
		    return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);

	    Application receivedApplication = applicationResource.toEntity();
	    receivedApplication.setDeveloper(user);
	    receivedApplication.setId(appId);
        String  categoryName = receivedApplication.getCategory().getName();
        receivedApplication.setCategory(categoryService.findCategoryByName(categoryName));
	    application = applicationService.updateApplication(receivedApplication);
	    ApplicationResource createdApplicationResource = new ApplicationResourceAssembler().toResource(application);
	    return new ResponseEntity<>(createdApplicationResource, HttpStatus.OK);
    }

	/**
	 * Update and publish application
	 * @param appId The id of the application to be updated and published
	 * @param applicationResource The application to be created and published
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/{appId}/updateAndPublish", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> updateAndPublishDeveloperApplication(
            @PathVariable String appId,
            @RequestBody ApplicationResource applicationResource
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
        if (application == null)
            return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);

        Application receivedApplication = applicationResource.toEntity();
        receivedApplication.setDeveloper(user);
        receivedApplication.setId(appId);
        receivedApplication.setState(AppState.Active);
        String  categoryName = receivedApplication.getCategory().getName();
        receivedApplication.setCategory(categoryService.findCategoryByName(categoryName));
        application = applicationService.updateApplication(receivedApplication);
        ApplicationResource createdApplicationResource = new ApplicationResourceAssembler().toResource(application);
        return new ResponseEntity<>(createdApplicationResource, HttpStatus.OK);
    }

	/**
	 * Publish developer application
	 * @param appId The id of the application to be published
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/{appId}/publish", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> publishDeveloperApplication(
            @PathVariable String appId
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try{
	        Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
	        // If the application state is not 'blocked' 
	        if(application!=null && !application.getState().equals(AppState.Blocked)) {
                application.setState(AppState.Active);
                application.setApplicationPublishDate(Calendar.getInstance().getTime());
                //push the app to history
                ApplicationHistory applicationHistory = new ApplicationHistory();
                applicationHistory.setApplication(application);
                applicationHistory.setName(application.getName());
                applicationHistory.setVersion(application.getVersion());
                applicationHistory.setWhatsNew(application.getWhatsNew());
                applicationHistoryService.createApplicationHistory(applicationHistory);
                application = applicationService.updateApplication(application);
                ApplicationResource newApplicationResource = new ApplicationResourceAssembler().toResource(application);
                return new ResponseEntity<>(newApplicationResource, HttpStatus.OK);
	        }else{
	        	return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
	        }
        }catch(Exception ex){
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
        	return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Recall developer application
	 * @param appId The id of the application to be recalled
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/applications/{appId}/recall", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> recallDeveloperApplication(
            @PathVariable String appId
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try{
            Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
            if (application == null)
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            // If the application state is 'Active'
            if(application.getState().equals(AppState.Active)) {
                //get top 1 application from history based on date (desc).

                //set the history application value to application

                //create new history app

                //return the application

                //if there is no history app, then set the state to "Recalled"
                application.setState(AppState.Recalled);
                application = applicationService.recallApplication(application);
                ApplicationResource updateApplicationResource = new ApplicationResourceAssembler().toResource(application);
                return new ResponseEntity<>(updateApplicationResource, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception ex){
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Created application update
	 * @param appId The id of the application
	 * @param applicationUpdateResource The application update instance to be created
	 * @return
	 * @see ApplicationResource
	 */
    @RequestMapping(path = "/{userId}/applications/{appId}/createUpdate", method = RequestMethod.POST)
    public ResponseEntity<ApplicationResource> createUpdateDeveloperApplication(
            @PathVariable String userId,
            @PathVariable String appId,
            @RequestBody ApplicationUpdateResource applicationUpdateResource
    ) {
        // Let's get the user from principal and validate the userId against it.
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            ApplicationUpdate applicationUpdate = applicationUpdateResource.toEntity();
            Application application = applicationService.findApplicationByDeveloperIdAndAppId(user.getId(), appId);
            // If the application state is not 'blocked' and should be active/RECALLED for the update to take place
            if(null != application && !AppState.Blocked.equals(application.getState()) && AppState.Active.equals(application.getState()) || (null != application && AppState.Recalled.equals(application.getState()))) {
                applicationUpdate.setTarget(application);
                applicationUpdateService.createApplicationUpdateByDeveloper(user.getId(),applicationUpdate);
                application.setId(applicationUpdate.getTarget().getId());
                application.setVersion(applicationUpdate.getVersion());
                application.setWhatsNew(applicationUpdate.getWhatsNew());
                application.setDescription(applicationUpdate.getDescription());
                application.setName(applicationUpdate.getName());
                ApplicationResource newApplicationUpdateResource = new ApplicationResourceAssembler().toResource(application);
                return new ResponseEntity<>(newApplicationUpdateResource, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.PRECONDITION_FAILED);
            }
        }catch(Exception ex){
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


	/**
	 * Publish Application Update
	 * @param appUpdate
	 * @param applicationUpdateResource
	 * @return
	 */
    @RequestMapping(path = "/{userId}/applications/{appId}/publishUpdate", method = RequestMethod.POST)
    public ResponseEntity<ApplicationUpdateResource> PublishDeveloperApplicationUpdate(
            @PathVariable String userId,
            @PathVariable ApplicationUpdate appUpdate,
            @RequestBody ApplicationUpdateResource applicationUpdateResource) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try{
            ApplicationUpdateResource newAppUpdateResource = new ApplicationUpdateResourceAssembler().toResource(appUpdate);
            return new ResponseEntity<>(newAppUpdateResource, HttpStatus.OK);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

	/**
	 * Get all active applications
	 * @return
	 * @see ApplicationListResource
	 */
    @RequestMapping(value = "/activeapplications", method = RequestMethod.GET)
    public ResponseEntity<ApplicationListResource> getAllActiveApplications(){
        try {
            User user = userService.findAuthenticatedUser();
            if (user == null)
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            ApplicationList appList = applicationService.findAllActiveApplicationsByDeveloper(user.getId());
            ApplicationListResource appListResource = new ApplicationListResourceAssembler().toResource(appList);
            return new ResponseEntity<>(appListResource, HttpStatus.OK);
        } catch (Exception ex) {
        	logger.error(AppConstants.REQUEST_PROCCESS_ERROR,ex);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
