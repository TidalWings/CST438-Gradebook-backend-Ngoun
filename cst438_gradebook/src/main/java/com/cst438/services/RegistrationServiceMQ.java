package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		// Majority of code/logic taken from EnrollmentController.
		Enrollment newEnroll = new Enrollment();
		newEnroll.setStudentName(enrollmentDTO.studentName);
		newEnroll.setStudentEmail(enrollmentDTO.studentEmail);
		
		Course potentialCourse = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
		if (potentialCourse != null) {
			newEnroll.setCourse(potentialCourse);
			enrollmentRepository.save(newEnroll);
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error. Course not found.");
		}
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
        courseDTO.course_id = course_id;
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
	}

}
