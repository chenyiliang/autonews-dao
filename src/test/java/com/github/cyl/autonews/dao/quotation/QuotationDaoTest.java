package com.github.cyl.autonews.dao.quotation;

import java.util.List;

import org.junit.Test;

public class QuotationDaoTest {
	@Test
	public void testQuotationDao() {
		QuotationDao quotationDao = new QuotationDao();
		List<String> list = quotationDao.getOneQuotations(2015, 8);
		System.out.println(list);
	}
}
