package com.example.globus;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GlobusApplicationTests {

	@Test
	void contextLoads() {
		Assertions.assertThat(40 + 2).isEqualTo(42);
	}
}
