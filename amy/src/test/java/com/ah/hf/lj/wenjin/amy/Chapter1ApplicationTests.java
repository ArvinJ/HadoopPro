package com.ah.hf.lj.wenjin.amy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.ah.hf.lj.wenjin.amy.controller.UserController;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class Chapter1ApplicationTests {
 
    private MockMvc mvc;
 
    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }
 
    @Test
    public void getHello() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/user/hi").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("hi")));
    }
 
}