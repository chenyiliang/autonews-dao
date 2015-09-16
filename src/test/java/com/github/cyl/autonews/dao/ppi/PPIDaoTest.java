package com.github.cyl.autonews.dao.ppi;

import org.junit.Test;

import com.github.cyl.autonews.pojo.ppi.MonthPPI;

public class PPIDaoTest {
	@Test
	public void testPPIDao() {
		PPIDao ppiDao = new PPIDao();
		MonthPPI monthPPI = ppiDao.getOneMonthPPI(2015, 4);
		System.out.println(monthPPI.getManufactureOfAutomotive().getMom());
	}
}
