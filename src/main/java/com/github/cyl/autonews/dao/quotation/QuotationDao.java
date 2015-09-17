package com.github.cyl.autonews.dao.quotation;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.github.cyl.autonews.dao.cpi.CPIDao;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

public class QuotationDao {
	private static final Properties props;

	static {
		InputStream inputStream = CPIDao.class.getClassLoader().getResourceAsStream("mongodb.properties");
		props = new Properties();
		try {
			props.load(inputStream);
		} catch (IOException e) {
			throw new RuntimeException("mongodb.properties read failly!");
		}
	}

	private static final String MONGO_HOST = props.getProperty("mongo.host");
	private static final int MONGO_PORT = Integer.valueOf(props.getProperty("mongo.port"));
	private static MongoClient client = new MongoClient(MONGO_HOST, MONGO_PORT);

	@SuppressWarnings("unchecked")
	public List<String> getOneQuotations(int year, int month) {
		Document doc = client.getDatabase("autonews").getCollection("quotation")
				.find(Filters.and(Filters.eq("year", year), Filters.eq("month", month))).first();
		return doc.get("quotations", List.class);
	}
}
