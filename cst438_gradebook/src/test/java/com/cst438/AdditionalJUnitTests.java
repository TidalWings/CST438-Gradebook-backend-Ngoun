package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Optional;

import com.cst438.controllers.AssignmentController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = { AssignmentController.class })
// @ContextConfiguration(classes = { GradeBookController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class AdditionalJUnitTests {

    static final String URL = "http://localhost:8081";
    public static final int TEST_COURSE_ID = 40442;
    public static final String TEST_STUDENT_EMAIL = "test@csumb.edu";
    public static final String TEST_STUDENT_NAME = "test";
    public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
    public static final int TEST_YEAR = 2021;
    public static final String TEST_SEMESTER = "Fall";
    // New vars for testing
    public static final String TEST_ASSIGNMENT_NAME = "hw11 - some stuff";
    public static final String TEST_ASSIGNMENT_NAME_ALT = "hw20 - less stuff";
    public static final String TEST_DATE_ALT = "2023-3-14";

    @MockBean
    AssignmentRepository assignmentRepository;
    @MockBean
    AssignmentGradeRepository assignmentGradeRepository;
    @MockBean
    CourseRepository courseRepository; // must have this to keep Spring test happy
    @MockBean
    RegistrationService registrationService; // must have this to keep Spring test happy
    @Autowired
    private MockMvc mvc;

    // We'll test our addAssignment method in our controller by adding a new item and then checking if the DB was saved in the process.
    @Test
    public void createAssignmentTest() throws Exception {
        MockHttpServletResponse response;
        
        // mock database data
        Course course = new Course();
        course.setCourse_id(TEST_COURSE_ID);
        course.setSemester(TEST_SEMESTER);
        course.setYear(TEST_YEAR);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setEnrollments(new java.util.ArrayList<Enrollment>());
        course.setAssignments(new java.util.ArrayList<Assignment>());

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        course.getEnrollments().add(enrollment);
        enrollment.setId(TEST_COURSE_ID);
        enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
        enrollment.setStudentName(TEST_STUDENT_NAME);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        course.getAssignments().add(assignment);
        // set dueDate to 1 week before now.
        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment.setId(1);
        assignment.setName("Assignment 1");
        assignment.setNeedsGrading(1);
        // Another assignment.
        Assignment assignment2 = new Assignment();
        assignment2.setCourse(course);
        course.getAssignments().add(assignment2);
        // set dueDate to 1 week before now.
        assignment2.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment2.setId(2);
        assignment2.setName(TEST_ASSIGNMENT_NAME);
        assignment2.setNeedsGrading(1);

        AssignmentGrade ag = new AssignmentGrade();
        ag.setAssignment(assignment);
        ag.setId(1);
        ag.setScore("");
        ag.setStudentEnrollment(enrollment);

        // given -- stubs for database repositories that return test data
        given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
        given(assignmentRepository.findById(2)).willReturn(Optional.of(assignment2));
        given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
        given(assignmentRepository.save(any())).willReturn(assignment);
        // end of mock data

        // TODO: Preparing body data/DTOs to be used here.
        // AssignmentDTO tempDto = convertToDTO(assignment);
        // response = mvc.perform(
        //     MockMvcRequestBuilders
        //     .post("/" + TEST_COURSE_ID + "/assignment/new/")
        //     .content(asJsonString(tempDto))
        //     .contentType(MediaType.APPLICATION_JSON)
        //     .accept(MediaType.APPLICATION_JSON))
        //     .andReturn().getResponse();

        response = mvc.perform(
            MockMvcRequestBuilders
            .post("/" + TEST_COURSE_ID + "/assignment/new/" + TEST_ASSIGNMENT_NAME + "/" + TEST_DATE_ALT + "/")
            .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

        // Check that we got an OK HTTP status response
        assertEquals(200, response.getStatus());

        // verify that a save was called on repository showing a new item was added.
        verify(assignmentRepository, times(1)).save(any());

        // Check that our added assignment is the same as the values we passed in via POST.
        Assignment tempAssign = assignmentRepository.findById(2).orElseThrow();
        // DEBUG:
        // System.out.println(tempAssign);
        assertEquals(tempAssign.getId(), 2);
        assertEquals(tempAssign.getName(), TEST_ASSIGNMENT_NAME);
        assertEquals(tempAssign.getNeedsGrading(), 1);
        assertEquals(tempAssign.getCourse(), course);
    }

    // Similar test showing that the repo will be saved to signify an updated assignment.
    @Test
    public void updateAssignmentName() throws Exception {
        MockHttpServletResponse response;

        // mock database data
        Course course = new Course();
        course.setCourse_id(TEST_COURSE_ID);
        course.setSemester(TEST_SEMESTER);
        course.setYear(TEST_YEAR);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setEnrollments(new java.util.ArrayList<Enrollment>());
        course.setAssignments(new java.util.ArrayList<Assignment>());

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        course.getEnrollments().add(enrollment);
        enrollment.setId(TEST_COURSE_ID);
        enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
        enrollment.setStudentName(TEST_STUDENT_NAME);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        course.getAssignments().add(assignment);
        // set dueDate to 1 week before now.
        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment.setId(1);
        assignment.setName("Assignment 1");
        assignment.setNeedsGrading(1);

        AssignmentGrade ag = new AssignmentGrade();
        ag.setAssignment(assignment);
        ag.setId(1);
        ag.setScore("80");
        ag.setStudentEnrollment(enrollment);

        // given -- stubs for database repositories that return test data
        given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
        given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
        // end of mock data

        response = mvc.perform(MockMvcRequestBuilders
            .put("/" + TEST_COURSE_ID + "/assignment/edit/" + 1 + "/" + TEST_ASSIGNMENT_NAME_ALT )
            .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();

        assertEquals(200, response.getStatus());
        // Verify that we changed the name of the initial assignment by checking if it saved in our controller method.
        verify(assignmentRepository, times(1)).save(any());

        // Checking out the values of the changed assignment is the new name using the same ID as we did earlier.
        Assignment tempAssign = assignmentRepository.findById(1).orElseThrow();
        // DEBUG:
        // System.out.println(tempAssign);
        assertEquals(tempAssign.getName(), TEST_ASSIGNMENT_NAME_ALT);
    }

    // Similar test as prev method, but this one SHOULD return a 400 HTTP status as we'll try to access an invalid assignment.
    @Test
    public void updateAssignmentNameInvalid() throws Exception {
        MockHttpServletResponse response;

        // mock database data
        Course course = new Course();
        course.setCourse_id(TEST_COURSE_ID);
        course.setSemester(TEST_SEMESTER);
        course.setYear(TEST_YEAR);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setEnrollments(new java.util.ArrayList<Enrollment>());
        course.setAssignments(new java.util.ArrayList<Assignment>());

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        course.getEnrollments().add(enrollment);
        enrollment.setId(TEST_COURSE_ID);
        enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
        enrollment.setStudentName(TEST_STUDENT_NAME);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        course.getAssignments().add(assignment);
        // set dueDate to 1 week before now.
        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment.setId(1);
        assignment.setName("Assignment 1");
        assignment.setNeedsGrading(1);

        AssignmentGrade ag = new AssignmentGrade();
        ag.setAssignment(assignment);
        ag.setId(1);
        ag.setScore("80");
        ag.setStudentEnrollment(enrollment);

        // given -- stubs for database repositories that return test data
        given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
        given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
        // end of mock data

        response = mvc.perform(MockMvcRequestBuilders
                .put("/" + TEST_COURSE_ID + "/assignment/edit/" + 12 + "/" + TEST_ASSIGNMENT_NAME_ALT)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Should be 400 because there ISN'T AN ASSIGNMENT with the above ID.
        assertEquals(400, response.getStatus());
    }

    @Test
    public void deleteAssignmentTest() throws Exception {
        MockHttpServletResponse response;

        // mock database data
        Course course = new Course();
        course.setCourse_id(TEST_COURSE_ID);
        course.setSemester(TEST_SEMESTER);
        course.setYear(TEST_YEAR);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setEnrollments(new java.util.ArrayList<Enrollment>());
        course.setAssignments(new java.util.ArrayList<Assignment>());

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        course.getEnrollments().add(enrollment);
        enrollment.setId(TEST_COURSE_ID);
        enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
        enrollment.setStudentName(TEST_STUDENT_NAME);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        course.getAssignments().add(assignment);
        // set dueDate to 1 week before now.
        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment.setId(1);
        assignment.setName("Assignment 1");
        assignment.setNeedsGrading(1);

        AssignmentGrade ag = new AssignmentGrade();
        ag.setAssignment(assignment);
        ag.setId(1);
        ag.setScore("80");
        ag.setStudentEnrollment(enrollment);

        // given -- stubs for database repositories that return test data
        given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
        given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
        // end of mock data

        response = mvc.perform(MockMvcRequestBuilders
                .delete("/" + TEST_COURSE_ID + "/assignment/delete/" + 1 )
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Once again ensure we have a good response alongside the fact that the DB wasn't (explicitly) updated and saved as a starter.
        assertEquals(200, response.getStatus());
        verify(assignmentRepository, times(0)).save(any());
        // Now we can check the count of items inside of our repo (should be 0).
        assertEquals(assignmentRepository.count(), 0);
    }

    @Test
    public void deleteAssignmentTestInvalidAssignment() throws Exception {
        MockHttpServletResponse response;

        // mock database data
        Course course = new Course();
        course.setCourse_id(TEST_COURSE_ID);
        course.setSemester(TEST_SEMESTER);
        course.setYear(TEST_YEAR);
        course.setInstructor(TEST_INSTRUCTOR_EMAIL);
        course.setEnrollments(new java.util.ArrayList<Enrollment>());
        course.setAssignments(new java.util.ArrayList<Assignment>());

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        course.getEnrollments().add(enrollment);
        enrollment.setId(TEST_COURSE_ID);
        enrollment.setStudentEmail(TEST_STUDENT_EMAIL);
        enrollment.setStudentName(TEST_STUDENT_NAME);

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        course.getAssignments().add(assignment);
        // set dueDate to 1 week before now.
        assignment.setDueDate(new java.sql.Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        assignment.setId(1);
        assignment.setName("Assignment 1");
        // This time we make sure the Assignment WONT be deleted, as the controller method should ignore assignments with a 0 for the needsGrading field.
        assignment.setNeedsGrading(0);

        AssignmentGrade ag = new AssignmentGrade();
        ag.setAssignment(assignment);
        ag.setId(1);
        ag.setScore("80");
        ag.setStudentEnrollment(enrollment);

        // given -- stubs for database repositories that return test data
        given(assignmentRepository.findById(1)).willReturn(Optional.of(assignment));
        given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));
        // end of mock data

        response = mvc.perform(MockMvcRequestBuilders
                .delete("/" + TEST_COURSE_ID + "/assignment/delete/" + 1)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Considering the assignment is invalid, this SHOULD THROW A 400 HTTP status error at us.
        assertEquals(400, response.getStatus());
    }

    // Helper function for Story where we need to display the updated name to the
    // front-end.
    private AssignmentDTO convertToDTO(Assignment toConvert) {
        AssignmentDTO toReturn = new AssignmentDTO();
        toReturn.id = toConvert.getId();
        toReturn.name = toConvert.getName();
        toReturn.needsGrading = toConvert.getNeedsGrading();
        toReturn.dueDate = toConvert.getDueDate();
        toReturn.course = toConvert.getCourse();

        return toReturn;
    }

    // Helper functions taken from other Unit Test to allow DTOs/Body data.
    private static String asJsonString(final Object obj) {
        try {

            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> T fromJsonString(String str, Class<T> valueType) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
