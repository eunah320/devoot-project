package com.gamee.devoot_backend.common.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.spi.JsonProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamee.devoot_backend.lecture.document.LectureDocument;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.json.JsonpMapper;

@Configuration
@Profile("!test")
public class ElasticIndexSetting {
	@Autowired
	private ElasticsearchOperations elasticsearchOperations;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Autowired
	private JsonpMapper jsonpMapper;

	@PostConstruct
	public void createIndexWithMapping() throws Exception {
		IndexOperations indexOperations = elasticsearchOperations.indexOps(LectureDocument.class);

		if (!indexOperations.exists()) {
			// File file = ResourceUtils.getFile("classpath:es-setting.json");
			// var data = objectMapper.readTree(file);
			InputStream inputStream = getClass().getClassLoader().getResourceAsStream("es-setting.json");
			if (inputStream == null) {
				throw new FileNotFoundException("es-setting.json not found in classpath");
			}
			JsonProvider provider = jsonpMapper.jsonProvider();
			try (JsonReader reader = provider.createReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
				JsonObject jsonObject = reader.readObject();
				createIndex(indexOperations.getIndexCoordinates().getIndexName(), jsonObject.toString());
				System.out.println("Index created with settings and mappings!");
			}
		} else {
			System.out.println("Index already exists, skipping creation");
		}
	}

	public void createIndex(String indexName, String jsonMapping) throws Exception {
		CreateIndexRequest createIndexRequest = CreateIndexRequest.of(b -> b
			.index(indexName)
			.withJson(new StringReader(jsonMapping))
		);
		CreateIndexResponse response = elasticsearchClient.indices().create(createIndexRequest);
	}
}
