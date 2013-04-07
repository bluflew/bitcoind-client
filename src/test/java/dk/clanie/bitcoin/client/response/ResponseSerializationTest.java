/**
 * Copyright (C) 2013, Claus Nielsen, cn@cn-consult.dk
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package dk.clanie.bitcoin.client.response;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import dk.clanie.bitcoin.json.JsonExtra;
import dk.clanie.io.IOUtil;

/**
 * Tests json serialization and deserialization of response objects.
 * 
 * @author Claus Nielsen
 */
public class ResponseSerializationTest {

	private static final Logger log = LoggerFactory.getLogger(ResponseSerializationTest.class);

	private ObjectMapper objectMapper = new ObjectMapper();


	/**
	 * Reveals a bug in Jackson.
	 * 
	 * @JsonUnwrapped conflicts with @JsonAnySetter/@JsonAnyGetter.
	 * See https://github.com/FasterXML/jackson-annotations/issues/10
	 * <p>
	 * Some serialization tests have been disabled by prefixing some
	 * of the sample file names with an underscore. 
	 * <p>
	 * When the Jackson bug has been fixed remove this test and it's
	 * resource file "_DOUBLE_UNWRAP.json" and re-enable other tests
	 * by removing the underscore prefix from sample file names.
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	// TODO Remove when Jackson has been fixed.
	public void testDoubleUnwrapped() throws Exception {
		File file = new File("src/test/resources/sampleResponse/_DOUBLE_UNWRAP.json");
		String jsonSample = IOUtils.toString(file.toURI());
		ListUnspentResult obj = objectMapper.readValue(jsonSample, ListUnspentResult.class);
		String roundtrippedJson = objectMapper.writeValueAsString(obj);
		System.out.println(roundtrippedJson);
		assertThat("json -> obj -> json roundtrip serialization failed for " + file.getName() + ".", roundtrippedJson, equalTo(jsonSample));
	}


	/**
	 * Performs serializatio test for all json sample files in
	 * src/test/resources/sampleResponse/.
	 * <p>
	 * Tests that each json sample can be deserialized to the type also given in
	 * the file name, and serialized back to the original.<br>
	 * Also checks that all fields are mapped explicitly (ie. that "otherFilds"
	 * isn't used).
	 * <p>
	 * Skips files with a name beginning with an underscore are skipped, making
	 * it possible to (temporarily) disable serialization tests of some samples.
	 * <p>
	 * Following response samples are currently skipped:
	 * <dl>
	 * <dt>_DOUBLE_UNWRAP.json</dt>
	 * <dd>Not a real response sample, but a constructed json objects which
	 * helps check if an Jackson bug has been fixed. See
	 * {@link #testDoubleUnwrapped()}.</dd>
	 * <dt>_GetAddedNodeInfo</dt>
	 * <dd>The bitcoind getaddednodeinfo method returns an array or an object
	 * depending on the input. I think that is a bug - see
	 * https://github.com/bitcoin/bitcoin/issues/2467. If that is accepted and
	 * fixed the sample should be changed. If the bug report is rejected the
	 * current sample is correct and the BitcoindClient code should be fixed.</dd>
	 * <dt>_ListAccountsResponse.json</dt>
	 * <dd>ListAccountsResult stores account balances in a map which doesn't
	 * preserve order, and so we cannot do roundtrip serialization to the exact
	 * same json. When we try the serialization output IS equivalent to the
	 * sample, so you could argue that it's bug in the test.</dd>
	 * <dt>_ListUnspentResponse.json</dt>
	 * <dd>ListUnspentResult is not yet finished - it sould be shortly.</dd>
	 * <dt>_ListAddressGroupingsResponse.json</dt>
	 * <dd>BigDecimal values are serialized using sientific notation.<br>
	 * When jackson-databind commit 8a8322b493fe67059d8a46718dde8185266c8c0c
	 * "Added serialization feature for writing BigDecimal in plain form" is
	 * included ni a Jackson release this should be fairly easy to fix.</dd>
	 * </dl>
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSerialization() throws Exception {
		List<File> files = IOUtil.listFilesRecursively(new File("src/test/resources/sampleResponse"));
		for (File file : files) {
			if (!file.getName().startsWith("_")) doSerializationRoundtrip(file);
			else log.debug("Serialization test of file " + file + " skipped because file name starts with an underscore.");
		}
	}


	/**
	 * Perform serialization roundtrip testing of the given file.
	 * 
	 * @param file
	 *            - file with sample data in json format. The file name must
	 *            follow a fixed format including the respons class name - see
	 *            {@link #extractResponseClassName(File)}.
	 * @throws Exception
	 */
	private void doSerializationRoundtrip(File file) throws Exception {
		
		log.debug("Testing deserialization of sample file " + file.getName() + ".");

		String className = extractResponseClassName(file);
		String jsonSample = IOUtils.toString(file.toURI());
		@SuppressWarnings("unchecked")
		Class<? extends BitcoindJsonRpcResponse<?>> responseType = (Class<? extends BitcoindJsonRpcResponse<?>>) Class.forName("dk.clanie.bitcoin.client.response." + className);

		// Deserialize and re-serialize
		log.debug("Deserializing " + jsonSample);
		BitcoindJsonRpcResponse<?> response = objectMapper.readValue(jsonSample, responseType);
		String roundtrippedJson = objectMapper.writeValueAsString(response);
		assertThat("json -> obj -> json roundtrip serialization failed for " + file.getName() + ".", roundtrippedJson, equalTo(jsonSample));

		// Check that all fields are explicitly mapped.
		assertThat("Some fields in response not explicitly mapped (see otherFields): " + response.toString(), response.getOtherFields().size(), equalTo(0));
		
		Object resultObject = response.getResult();
		if (resultObject instanceof JsonExtra) {
			JsonExtra result = (JsonExtra) resultObject;
			Map<String, Object> otherFields = result.getOtherFields();
			if (otherFields.size() > 0) {
				StringBuilder sb = new StringBuilder("Some fields in result not explicitly mapped: ");
				int fieldNo = 0;
				for (String field : otherFields.keySet()) {
					if (fieldNo++ > 0) sb.append(", ");
					sb.append(field);
				}
				fail(sb.append(".").toString());
			}
		}
	}


	/**
	 * Extracts the response class name from the name of the sample
	 * file. Sample files must be named
	 * &lt;ResponseClassName&gt;_&lt;test sequence or description&gt;.json,
	 * where the _&lt;test sequence or description&gt; is optional.
	 * It is used when there are more than one sample response of
	 * the same response class.
	 * 
	 * @param file
	 * @return String - response class name
	 */
	private String extractResponseClassName(File file) {
		String fileName = file.getName();
		int endIndex = fileName.indexOf("_");
		if (endIndex == -1) endIndex = fileName.indexOf(".");
		return fileName.substring(0, endIndex);
	}


}
