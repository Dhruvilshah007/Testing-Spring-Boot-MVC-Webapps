package com.luv2code.springmvc;


import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradebookControllerTest {

    private static MockHttpServletRequest request;


    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private StudentAndGradeService studentCreateServiceMock;


    @Autowired
    private StudentDao studentDao;

    @BeforeAll //BeforeAll is static because as per Junit docs and if any fields is there inside it, it also must be static
    public static void setup(){
        request=new MockHttpServletRequest();
        request.setParameter("firstname","Dhruvil");
        request.setParameter("lastname","Shah");
        request.setParameter("emailAddress","shah@gmail.com");
    }



    @BeforeEach
    public void beforeEach(){
        jdbc.execute("insert into student(id,firstname,lastname,email_address) "+ "values (1,'dds','shah','shha@gmail.com')");
    }

    @Test
    public void getStudentHttRequest() throws Exception{
        CollegeStudent studentOne=new GradebookCollegeStudent("Dhruvil","SHah","shah@gmail.com");

        CollegeStudent studentTwo=new GradebookCollegeStudent("Raj","SHah","raj@gmail.com");

        List<CollegeStudent> collegeStudentList=new ArrayList<>(Arrays.asList(studentOne,studentTwo));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList,studentCreateServiceMock.getGradebook());


        MvcResult mvcResult=mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"index");
    }

    @Test
    public void createStudentHttpRequest()throws Exception{
        CollegeStudent studentOne=new CollegeStudent("Raj","Patel","raj@gmail.com");

        List<CollegeStudent> collegeStudentList=new ArrayList<>(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList,studentCreateServiceMock.getGradebook());


        MvcResult mvcResult=this.mockMvc.perform(post("/")
                .param("firstname",request.getParameterValues("firstname"))
                .param("lastname",request.getParameterValues("lastname"))
                .param("emailAddress",request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav,"index");

        CollegeStudent verifyStudent=studentDao.findByEmailAddress("shah@gmail.com");

        assertNotNull(verifyStudent,"Student Should be found");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception{
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult= mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();


        ModelAndViewAssert.assertViewName(mav,"index");
        assertFalse(studentDao.findById(1).isPresent());
    }


    @Test
    public void deleteStudentHttpRequestErrorPage() throws Exception{
        MvcResult mvcResult= mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav=mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");
    }




    @AfterEach
    public void setupAfterTransaction(){
        jdbc.execute("DELETE FROM student");
    }




}
