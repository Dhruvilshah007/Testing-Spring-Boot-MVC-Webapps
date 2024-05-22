package com.luv2code.springmvc;


import com.luv2code.springmvc.models.*;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
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

    @Autowired
    MathGradesDao mathGradeDao;

    @Autowired
    ScienceGradesDao scienceGradeDao;

    @Autowired
    HistoryGradeDao historyGradeDao;



    @Value("${sql.script.create.student}")
    private String sqlAddStudent;

    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;

    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @Test
    public void createStudentService(){

        studentService.createStudent("Dhruvil","Shah","dshah@gmail.com");

        CollegeStudent student=studentDao.findByEmailAddress("dshah@gmail.com");
        assertEquals("dshah@gmail.com",student.getEmailAddress(),"FInd by email");
    }


    @BeforeEach
    public void setupDatabase(){
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
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
        Optional<MathGrade> deletedMathGrade=mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade=scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade=historyGradeDao.findById(1);


        assertTrue(deletedCollegeStudent.isPresent(),"Return true");
        assertTrue(deletedMathGrade.isPresent(),"Return true");
        assertTrue(deletedScienceGrade.isPresent(),"Return true");
        assertTrue(deletedHistoryGrade.isPresent(),"Return true");

        studentService.deleteStudent(1);

        deletedCollegeStudent=studentDao.findById(1);
        deletedMathGrade=mathGradeDao.findById(1);
        deletedScienceGrade=scienceGradeDao.findById(1);deletedHistoryGrade=historyGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(),"Return False");
        assertFalse(deletedMathGrade.isPresent(),"Return False");
        assertFalse(deletedScienceGrade.isPresent(),"Return False");
        assertFalse(deletedHistoryGrade.isPresent(),"Return False");

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

    @Test
    public void createGradeService(){

        //create the grade
        assertTrue(studentService.createGrade(80.50,1,"math"));

        assertTrue(studentService.createGrade(80.50,1,"science"));


        assertTrue(studentService.createGrade(80.50,1,"history"));
        //Get all grade the studentId

        Iterable<MathGrade> mathGrades=mathGradeDao.findGradeByStudentId(1);

        Iterable<ScienceGrade> scienceGrades=scienceGradeDao.findGradeByStudentId(1);

        Iterable<HistoryGrade> historyGrades=historyGradeDao.findGradeByStudentId(1);

//        //verify there is grades
//        assertTrue(mathGrades.iterator().hasNext(),"Student has math grades");
//
//        assertTrue(scienceGrades.iterator().hasNext(),"Student has science grades");
//
//        assertTrue(historyGrades.iterator().hasNext(),"Student has history grades");

        //verify there is grade
        //We have added size=2 because one Grade we add at beforeTest and one here in test.

        assertTrue(((Collection<MathGrade>) mathGrades).size()==2,"Student has math grade");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size()==2,"Student has Science grade");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size()==2,"Student has History grade");
    }


    @Test
    public void deletegradeService(){
        assertEquals(1,studentService.deleteGrade(1,"math"),"Returns student id after delete");

        assertEquals(1,studentService.deleteGrade(1,"science"),"Returns student id after delete");

        assertEquals(1,studentService.deleteGrade(1,"history"),"Returns student id after delete");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdOrZero(){

        assertEquals(0,studentService.deleteGrade(0,"science"),"No student should have Id 0, Invalid Student Id");

        assertEquals(0,studentService.deleteGrade(1,"literature"),"No student should have literature class");
    }




    @Test
    public void createGradeSwerviceReturnFalse(){


        //Grade is greater than 100
        assertFalse(studentService.createGrade(105,1,"math"));

        //negative grade
        assertFalse(studentService.createGrade(-5,1,"math"));


        //Invalid studentId
        assertFalse(studentService.createGrade(80.50,2,"math"));


        //invalid subject
        assertFalse(studentService.createGrade(80/5,1,"lietrature"));
    }




    @Test

    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegeStudentTest = studentService.studentInformation(1);


        //assertNotNull(gradebookCollegeStudentTest);
        assertEquals(1, gradebookCollegeStudentTest.getId());
        assertNotNull(gradebookCollegeStudentTest.getFirstname());
        assertNotNull(gradebookCollegeStudentTest.getLastname());
        assertNotNull(gradebookCollegeStudentTest.getEmailAddress());
        assertNotNull(gradebookCollegeStudentTest.getStudentGrades().getMathGradeResults());
        assertNotNull(gradebookCollegeStudentTest.getStudentGrades().getScienceGradeResults());
        assertNotNull(gradebookCollegeStudentTest.getStudentGrades().getHistoryGradeResults());

        assertEquals("dds", gradebookCollegeStudentTest.getFirstname());
        assertEquals("shah", gradebookCollegeStudentTest.getLastname());
        assertEquals("shah@gmail.com", gradebookCollegeStudentTest.getEmailAddress());


    }


    @Test
    public void studentInformationServiceReturnNull(){
        GradebookCollegeStudent gradebookCollegeStudent=studentService.studentInformation(0);

        assertNull(gradebookCollegeStudent);

    }



    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);


    }







}
