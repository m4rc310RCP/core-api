package br.com.m4rc310.core.gtim.services;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.core.gtim.constants.IConsts;
import br.com.m4rc310.core.gtim.properties.MGtimProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(name = MGtimProperty.PROPS_GTIM_ENABLE, havingValue = "true", matchIfMissing = false)
public class GtimService implements IConsts {

	@Value(PROPS_GTIM_API_URI)
	private String apiUri;

	public <T> T getProductFromEan(Long ean, Class<T> type) throws Exception {
		String suri = String.format("%s/api/desc/%d", apiUri, ean);

	    URL uri = new URI(suri).toURL();
	    HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
	    connection.setRequestProperty("accept", "application/json");

	    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
	        ObjectMapper mapper = new ObjectMapper();
	        return mapper.readValue(reader, type);
	    }
	}

	public String getImageUrlFrom(Long ean) {
		String uri = apiUri;
		return String.format("%s/api/gtin/%d", uri, ean);
	}
	
	
	public String getImageStreamFrom(Long ean) {
		try {

			String uri = getImageUrlFrom(ean);

			java.net.URL url = new URI(uri).toURL();
			InputStream is = url.openStream();
			byte[] bytes = IOUtils.toByteArray(is);
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {

			return null;
		}
	}
}
