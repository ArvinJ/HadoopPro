package com.ah.hf.lj.wenjin.amy;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.ah.hf.lj.wenjin.amy.entity.Author;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Author.class)
public class AmyApplicationTests {

	@Autowired
	Author author;

	@Test
	public void contextLoads() {
		Assert.assertEquals(author.getName(), "wenjin.zhu");
	}

}
