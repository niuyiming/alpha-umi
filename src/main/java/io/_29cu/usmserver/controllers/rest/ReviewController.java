package io._29cu.usmserver.controllers.rest;

import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io._29cu.usmserver.controllers.rest.resources.ReviewResource;
import io._29cu.usmserver.controllers.rest.resources.assemblers.ReviewResourceAssembler;
import io._29cu.usmserver.core.model.entities.Application;
import io._29cu.usmserver.core.model.entities.Review;
import io._29cu.usmserver.core.model.entities.User;
import io._29cu.usmserver.core.service.ApplicationService;
import io._29cu.usmserver.core.service.ReviewService;
import io._29cu.usmserver.core.service.UserService;
import io._29cu.usmserver.core.service.exception.ReviewDoesNotExistException;

@Controller
@RequestMapping("/api/0/consumer/{applicationId}/review")
public class ReviewController {
	@Autowired
    private UserService userService;
	@Autowired
	private ReviewService reviewService;
	@Autowired
    private ApplicationService applicationService;
	private final Log logger = LogFactory.getLog(this.getClass());

	/**
	 * Create review for application
	 * @param applicationId The id of the application
	 * @param reviewResource The review to be created
	 * @return
	 * @see ReviewResource
	 */
    @RequestMapping(path = "/create", method = RequestMethod.POST)
    public ResponseEntity<ReviewResource> createReview(
    		@PathVariable String applicationId,
    		@RequestBody ReviewResource reviewResource
    ) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		
        Review review = reviewResource.toEntity();
        review.setCreationDate(Calendar.getInstance().getTime());
        review.setConsumer(user);
        review.setTitle(review.getTitle());
        Application application = applicationService.findApplication(applicationId);
        if(application == null){
        	 return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }       
        review = reviewService.createReview(review,application,user);
        ReviewResource createdReviewResource = new ReviewResourceAssembler().toResource(review);
        return new ResponseEntity<>(createdReviewResource,HttpStatus.CREATED);
    }

	/**
	 * Remove review of application
	 * @param reviewId The id of the review instance
	 * @return
	 * @see ReviewResource
	 */
    @RequestMapping(path = "/remove/{reviewId}", method = RequestMethod.DELETE)
    public ResponseEntity<ReviewResource> removeReview(
    		@PathVariable Long reviewId
    ) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        try {
			reviewService.removeReview(reviewId);
		} catch (ReviewDoesNotExistException ex) {
			logger.error("Review does not exist",ex);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
        return new ResponseEntity<>(HttpStatus.OK);
    }

	/**
	 * Set featured review
	 * @param reviewId The id of the review instance
	 * @return
	 * @see ReviewResource
	 */
    @RequestMapping(path = "/remove/{reviewId}/feature", method = RequestMethod.PUT)
    public ResponseEntity<ReviewResource> featureReview(
    		@PathVariable Long reviewId
    ) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		reviewService.featureReview(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	/**
	 * Unset featured review
	 * @param reviewId The id of the review instance
	 * @return
	 * @see ReviewResource
	 */
    @RequestMapping(path = "/remove/{reviewId}/unfeature", method = RequestMethod.PUT)
    public ResponseEntity<ReviewResource> unFeatureReview(
    		@PathVariable Long reviewId
    ) {
        User user = userService.findAuthenticatedUser();
        if (user == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		reviewService.unfeatureReview(reviewId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
