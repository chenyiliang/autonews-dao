package com.github.cyl.autonews.dao.cpi;

import org.junit.Test;

import com.github.cyl.autonews.pojo.cpi.MonthCPI;

public class CPIDaoTest {
	@Test
	public void testCPIDao() {
		CPIDao cpiDao = new CPIDao();
		MonthCPI monthCPI = cpiDao.getOneMonthCPI(2015, 5);
		System.out.println(monthCPI.getConsumerProducts().getMom());
	}
}
