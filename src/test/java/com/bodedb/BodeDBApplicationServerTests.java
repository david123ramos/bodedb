package com.bodedb;

import com.bodedb.infra.persistance.SStable;
import com.bodedb.infra.persistance.sstable.Header;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

import static org.springframework.test.util.AssertionErrors.*;

@SpringBootTest
class BodeDBApplicationServerTests {

	@Test
	void contextLoads() {
	}

	@Test
	void itShouldValidateBinarySstableFile() throws IOException {

		String sstablename = "table/sstable_2025-11-26T17:11:28.561684Z.sst";

		String finalName = new StringBuilder()
				.append(SStable.ROOT_DATA_PATH)
				.append("/")
				.append(sstablename)
				.toString();

		DataInputStream dis = getInputStream(finalName);
		assertTrue("", dis.available() > 0);

		int result = dis.readInt();
		assertTrue("", Header.MAGIC_NUMBER == result);
	}

	private DataInputStream getInputStream(String fileName) throws FileNotFoundException {
		File file = new File(fileName);
		return new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
	}

}
