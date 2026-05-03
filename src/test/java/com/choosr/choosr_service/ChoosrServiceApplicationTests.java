package com.choosr.choosr_service;

import com.choosr.choosr_service.repository.DecisionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ChoosrServiceApplicationTests {

	@MockBean
	@SuppressWarnings("unused")
	private DecisionRepository decisionRepository;

	@Test
	void contextLoads() {
	}

}
