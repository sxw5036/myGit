package com.lwxf.industry4.webapp.facade;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 功能：
 *
 * @author：renzhongshan(d3shan@126.com)
 * @create：2018-08-14 9:31
 * @version：2018 Version：1.0
 * @company：老屋新房 Created with IntelliJ IDEA
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DemoControllerApplicationTest {
	@Autowired
	private MockMvc mvc;
	@Test
	public void testRongRegister() throws Exception{
		mvc.perform(MockMvcRequestBuilders.get("/api/demos/rong/register").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}
}
