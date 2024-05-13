package com.luv2code.springmvc;


import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    StudentAndGradeService studentService;

    @Autowired
    StudentDao studentDao;

    @Test
    public void createStudentService(){

        studentService.createStudent("Dhruvil","Shah","dshah@gmail.com");

        CollegeStudent student=studentDao.findByEmailAddress("dshah@gmail.com");
        assertEquals("dshah@gmail.com",student.getEmailAddress(),"FInd by email");
    }


    @BeforeEach
    public void setupDatabase(){
        jdbc.execute("insert into student(id,firstname,lastname,email_address) "+ "values (1,'dds','shah','shha@gmail.com')");
    }

    @Test
    public void isStudentNullCheck(){

        //Here below method checks if given id is present or not in Database table
        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }


    @Test
    public void deleteStudentService(){

        //Get student delete it and again check it
        Optional<CollegeStudent> deletedCollegeStudent=studentDao.findById(1);
        assertTrue(deletedCollegeStudent.isPresent(),"Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent=studentDao.findById(1);
        assertFalse(deletedCollegeStudent.isPresent(),"Return False");
    }

    //Below will execute scripts from insertData before running test,but @BeforeEach will be executed first
    @Sql("/insertData.sql")
    @Test
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegeStudents=studentService.getGradebook();

        List<CollegeStudent> collegeStudents=new ArrayList<>();


        //convert Iterable to List
        for(CollegeStudent collegeStudent:iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }
        assertEquals(5,collegeStudents.size());
    }





    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute("DELETE FROM student");
    }







}
