package com.github.cyl.autonews.dao.analysis;

import org.junit.Test;

import com.github.cyl.autonews.pojo.analysis.Article;

public class AnalysisDaoTest {
	@Test
	public void testAnalysisDao() {
		AnalysisDao analysisDao = new AnalysisDao();
		Article article = analysisDao.getOneAnalysisArticle(2015, 5);
		System.out.println(article);
	}
}
