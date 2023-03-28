package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course. From walkthrough w/ vars & comments changed/added for clarity.
	 */
	@PostMapping("/enrollment")
	@Transactional
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
        // Create new potential enrollment.
		Enrollment newEnroll = new Enrollment();
		newEnroll.setStudentName(enrollmentDTO.studentName);
		newEnroll.setStudentEmail(enrollmentDTO.studentEmail);
        // Using the course ID, find a potential course.
		Course potentialCourse = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
		if (potentialCourse != null) {
            // If exists, add it to the DTO, save and return it for Register to get via HTTP Req.
			newEnroll.setCourse(potentialCourse);
			newEnroll = enrollmentRepository.save(newEnroll);
			enrollmentDTO.id = newEnroll.getId();
			return enrollmentDTO;
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error. Course not found.");
		}
	}
}
