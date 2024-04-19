package com.rockbitegames;

import com.rockbitegames.service.MaterialService;
import com.rockbitegames.service.PlayerService;
import com.rockbitegames.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("dev")
class RockbitegamesApplicationTests {

	@Autowired
	private WarehouseService warehouseService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private MaterialService materialService;

	@Test
	void contextLoads() {
		assertNotNull(warehouseService);
		assertNotNull(playerService);
		assertNotNull(materialService);
	}

}
