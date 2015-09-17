package com.github.cyl.autonews.dao.analysis;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.github.cyl.autonews.dao.cpi.CPIDao;
import com.github.cyl.autonews.pojo.analysis.Article;
import com.github.cyl.autonews.pojo.analysis.Clause;
import com.github.cyl.autonews.pojo.analysis.Paragraph;
import com.github.cyl.autonews.pojo.analysis.Section;
import com.github.cyl.autonews.pojo.analysis.Sentence;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

public class AnalysisDao {
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

	private List<String> SUBTITLE_MARKS = Arrays.asList("一、", "二、");
	private String CLAUSE_SPLITER = ",|，|;|；|:|：";

	@SuppressWarnings("unchecked")
	public Article getOneAnalysisArticle(int year, int month) {
		Document doc = client.getDatabase("autonews").getCollection("analysis")
				.find(Filters.and(Filters.eq("year", year), Filters.eq("month", month))).first();
		if (doc == null) {
			return null;
		}
		Article article = new Article(year, month, doc.getString("title"));
		List<Object> contents = doc.get("contents", List.class);
		return assembleArticle(article, contents);
	}

	private Article assembleArticle(Article article, List<Object> contents) {
		List<Section> sections = new ArrayList<Section>();
		List<Paragraph> paragraphs = new ArrayList<Paragraph>();
		for (int i = 0; i < contents.size(); i++) {
			String content = contents.get(i).toString();
			List<Sentence> sentences = new ArrayList<Sentence>();
			if (content.contains("。")) {
				String[] splits = content.split("。");
				for (String split : splits) {
					if (!split.trim().isEmpty()) {
						sentences.add(new Sentence(split.trim() + "。", assembleClauses(split.split(CLAUSE_SPLITER))));
					}
				}
			} else {
				sentences.add(new Sentence(content));
			}
			paragraphs.add(new Paragraph(sentences));
		}
		// System.out.println(paragraphs);
		List<Integer> sectionSplitIndexs = new ArrayList<Integer>();

		sectionSplitIndexs.add(0);
		for (int i = 0; i < paragraphs.size(); i++) {
			Paragraph paragraph = paragraphs.get(i);
			String sentence = paragraph.getSentences().get(0).getSentence();
			if (isSubTitle(sentence)) {
				sectionSplitIndexs.add((i));
			}
		}
		sectionSplitIndexs.add(paragraphs.size());

		// System.out.println(sectionSplitIndexs);

		for (int i = 0; i < sectionSplitIndexs.size() - 1; i++) {
			int from = sectionSplitIndexs.get(i);
			int to = sectionSplitIndexs.get(i + 1);
			List<Paragraph> subList = paragraphs.subList(from, to);
			if (i == 0) {
				sections.add(new Section(null, subList));
			} else {
				sections.add(new Section(subList.get(0).getSentences().get(0).getSentence(),
						subList.subList(1, subList.size())));
			}
		}
		article.setSectinos(sections);
		return article;
	}

	private List<Clause> assembleClauses(String[] arr) {
		List<Clause> clauses = new ArrayList<Clause>();
		for (String str : arr) {
			clauses.add(new Clause(str));
		}
		return clauses;
	}

	private boolean isSubTitle(String sentence) {
		for (String mark : SUBTITLE_MARKS) {
			if (sentence.startsWith(mark)) {
				return true;
			}
		}
		return false;
	}
}
