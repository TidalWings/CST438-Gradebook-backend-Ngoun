package com.cst438.controllers;

import java.sql.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;

@RestController
public class AssignmentController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    AssignmentRepository assignmentRepository;
    @Autowired
    AssignmentGradeRepository assignmentGradeRepository;

    // addAssignment takes a course_id in the URL alongwith the name and due date of the assignment in the URL and then gets processed here to be submitted into the database.
    // TODO: Finalize conversion of input data.
    // @PostMapping("/{course_id}/assignment/new")
    @PostMapping("/{course_id}/assignment/new/{name}/{due_date}/")
    @Transactional
    // public void addAssignment(@RequestBody AssignmentDTO inputAssignment, @PathVariable("course_id") int courseID) {
    public void addAssignment(@PathVariable("name") String assignmentName, @PathVariable("due_date") Date dueDate, @PathVariable("course_id") int courseID) {
        
        // Taken from Gradebook Controller. Ensures that the course that the assignment is being added to is by the same instructor doing the action.
        // check that this request is from the course instructor
        String email = "dwisneski@csumb.edu"; // user name (should be instructor's email)
        Course course1 = courseRepository.findById(courseID).orElse(null);
        if (!course1.getInstructor().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
        }

        // Use the details from the front-end to create a new assignment, then save it with the repo we imported (injected) earlier.
        // TODO: Preparing to utilize body/DTOs.
        // String assignmentName = inputAssignment.name;
        // Date dueDate = inputAssignment.dueDate;
        // Create the assignment from those details.
        Assignment tempAssign = new Assignment();
        tempAssign.setName(assignmentName);
        tempAssign.setDueDate(dueDate);
        tempAssign.setNeedsGrading(1);
        tempAssign.setCourse(course1);
        
        assignmentRepository.save(tempAssign);
    }

    @PutMapping("{course_id}/assignment/edit/{assignment_id}/{new_name}")
    @Transactional
    // public AssignmentDTO updateAssignmentName(@PathVariable("course_id") int courseID, @PathVariable("assignment_id") int assignmentID, @PathVariable("new_name") String newName) {
    public void updateAssignmentName(@PathVariable("course_id") int courseID, @PathVariable("assignment_id") int assignmentID, @PathVariable("new_name") String newName) {

        // Taken from Gradebook Controller again.
        // Ensures only the course instructor can do this.
        String email = "dwisneski@csumb.edu"; // user name (should be instructor's email)
        Course c = courseRepository.findById(courseID).orElse(null);
        if (!c.getInstructor().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
        }

        Assignment tempAssignment = assignmentRepository.findById(assignmentID).orElse(null);
        if (tempAssignment != null) {
            tempAssignment.setName(newName);
            assignmentRepository.save(tempAssignment);
            // TODO: Large JSON Packages somehow?
            // Assignment forConversion = assignmentRepository.findById(assignmentID).orElse(null);
            // AssignmentDTO tempDTO = convertToDTO(forConversion);
            // return tempDTO;
        } else {
            // From Gradebook Controller.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + assignmentID);
        }
    }

    @DeleteMapping("{course_id}/assignment/delete/{assignment_id}")
    @Transactional
    public void deleteAssignment(@PathVariable("course_id") int courseID, @PathVariable("assignment_id") int assignmentID) {

        // Taken from Gradebook Controller once again.
        // Ensures only the course instructor can do this.
        String email = "dwisneski@csumb.edu"; // user name (should be instructor's email)
        Course c = courseRepository.findById(courseID).orElse(null);
        if (!c.getInstructor().equals(email)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not Authorized. ");
        }

        // Taken from previous function.
        Assignment tempAssignment = assignmentRepository.findById(assignmentID).orElse(null);
        if (tempAssignment != null) {
            // If NeedsGrading == 1 (true) means the assignment is current ungraded.
            if (tempAssignment.getNeedsGrading() == 1) {
                assignmentRepository.delete(tempAssignment);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not valid for deletion. " + assignmentID);
            }
        } else {
            // From Gradebook Controller.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignment not found. " + assignmentID);
        }
    }

    // Helper function for Story where we need to display the updated name to the front-end.
    private AssignmentDTO convertToDTO(Assignment toConvert) {
        AssignmentDTO toReturn = new AssignmentDTO();
        toReturn.id = toConvert.getId();
        toReturn.name = toConvert.getName();
        toReturn.needsGrading = toConvert.getNeedsGrading();
        toReturn.dueDate = toConvert.getDueDate();
        toReturn.course = toConvert.getCourse();

        return toReturn;
    }
}