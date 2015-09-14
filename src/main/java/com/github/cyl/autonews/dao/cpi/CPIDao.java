package com.github.cyl.autonews.dao.cpi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.github.cyl.autonews.pojo.cpi.MonthCPI;
import com.github.cyl.autonews.pojo.cpi.category.Clothing;
import com.github.cyl.autonews.pojo.cpi.category.Food;
import com.github.cyl.autonews.pojo.cpi.category.HealthCareAndPersonalArticles;
import com.github.cyl.autonews.pojo.cpi.category.HouseholdFacilitiesArticlesAndServices;
import com.github.cyl.autonews.pojo.cpi.category.MeatPoultryAndProcessedProducts;
import com.github.cyl.autonews.pojo.cpi.category.RecreationEducationAndCultureArticles;
import com.github.cyl.autonews.pojo.cpi.category.Residence;
import com.github.cyl.autonews.pojo.cpi.category.TobaccoLiquorAndArticles;
import com.github.cyl.autonews.pojo.cpi.category.TransportationAndCommunication;
import com.github.cyl.autonews.pojo.indicator.Indicator;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

public class CPIDao {
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

	public MonthCPI getOneMonthCPI(int year, int month) {
		Document doc = client.getDatabase("autonews").getCollection("cpi")
				.find(Filters.and(Filters.eq("year", year), Filters.eq("month", month))).first();
		if (doc == null) {
			return null;
		}
		MonthCPI monthCPI = new MonthCPI(year, month);

		monthCPI.setIndicator(extractOneFieldToIndicator("cpi", doc));

		monthCPI.setUrbanHousehold(extractOneFieldToIndicator("urbanHousehold", doc));
		monthCPI.setRuralHousehold(extractOneFieldToIndicator("ruralHousehold", doc));
		monthCPI.setNonFood(extractOneFieldToIndicator("nonFood", doc));
		monthCPI.setConsumerProducts(extractOneFieldToIndicator("consumerProducts", doc));
		monthCPI.setService(extractOneFieldToIndicator("service", doc));
		monthCPI.setNonFoodAndEnergy(extractOneFieldToIndicator("nonFoodAndEnergy", doc));
		monthCPI.setNonVegetableAndFruit(extractOneFieldToIndicator("nonVegetableAndFruit", doc));

		monthCPI.setFood(assembleFood(doc));
		monthCPI.setTobaccoLiquorAndArticles(assembleTobaccoLiquorAndArticles(doc));
		monthCPI.setClothing(assembleClothing(doc));
		monthCPI.setHouseholdFacilitiesArticlesAndServices(assembleHouseholdFacilitiesArticlesAndServices(doc));
		monthCPI.setHealthCareAndPersonalArticles(assembleHealthCareAndPersonalArticles(doc));
		monthCPI.setTransportationAndCommunication(assembleTransportationAndCommunication(doc));
		monthCPI.setRecreationEducationAndCultureArticles(assembleRecreationEducationAndCultureArticles(doc));
		monthCPI.setResidence(assembleResidence(doc));

		return monthCPI;
	}

	private Food assembleFood(Document doc) {
		Food food = new Food();

		food.setIndicator(extractOneFieldToIndicator("food", doc));

		food.setGrain(extractOneFieldToIndicator("grain", doc));
		food.setOilOrFat(extractOneFieldToIndicator("oilOrFat", doc));
		food.setEggs(extractOneFieldToIndicator("eggs", doc));
		food.setAquaticProducts(extractOneFieldToIndicator("aquaticProducts", doc));
		food.setFreshVegetables(extractOneFieldToIndicator("freshVegetables", doc));
		food.setFreshFruits(extractOneFieldToIndicator("freshFruits", doc));
		food.setMilkAndIsProducts(extractOneFieldToIndicator("milkAndIsProducts", doc));

		MeatPoultryAndProcessedProducts meatPoultryAndProcessedProducts = new MeatPoultryAndProcessedProducts();
		meatPoultryAndProcessedProducts
				.setIndicator(extractOneFieldToIndicator("meatPoultryAndProcessedProducts", doc));
		meatPoultryAndProcessedProducts.setPork(extractOneFieldToIndicator("pork", doc));
		meatPoultryAndProcessedProducts.setBeef(extractOneFieldToIndicator("beef", doc));
		meatPoultryAndProcessedProducts.setLamb(extractOneFieldToIndicator("lamb", doc));

		food.setMeatPoultryAndProcessedProducts(meatPoultryAndProcessedProducts);

		return food;
	}

	private TobaccoLiquorAndArticles assembleTobaccoLiquorAndArticles(Document doc) {
		TobaccoLiquorAndArticles tobaccoLiquorAndArticles = new TobaccoLiquorAndArticles();
		tobaccoLiquorAndArticles.setIndicator(extractOneFieldToIndicator("tobaccoLiquorAndArticles", doc));
		tobaccoLiquorAndArticles.setTobacco(extractOneFieldToIndicator("tobacco", doc));
		tobaccoLiquorAndArticles.setLiquor(extractOneFieldToIndicator("liquor", doc));
		return tobaccoLiquorAndArticles;
	}

	private Clothing assembleClothing(Document doc) {
		Clothing clothing = new Clothing();
		clothing.setIndicator(extractOneFieldToIndicator("clothing", doc));
		clothing.setGarments(extractOneFieldToIndicator("garments", doc));
		clothing.setFootgear(extractOneFieldToIndicator("footgear", doc));
		clothing.setClothingManufacturingServices(extractOneFieldToIndicator("clothingManufacturingServices", doc));
		return clothing;
	}

	private HouseholdFacilitiesArticlesAndServices assembleHouseholdFacilitiesArticlesAndServices(Document doc) {
		HouseholdFacilitiesArticlesAndServices householdFacilitiesArticlesAndServices = new HouseholdFacilitiesArticlesAndServices();
		householdFacilitiesArticlesAndServices
				.setIndicator(extractOneFieldToIndicator("householdFacilitiesArticlesAndServices", doc));
		householdFacilitiesArticlesAndServices
				.setDurableConsumerGoods(extractOneFieldToIndicator("durableConsumerGoods", doc));
		householdFacilitiesArticlesAndServices.setHouseholdServicesAndMaintenanceAndRenovation(
				extractOneFieldToIndicator("householdServicesAndMaintenanceAndRenovation", doc));
		return householdFacilitiesArticlesAndServices;
	}

	private HealthCareAndPersonalArticles assembleHealthCareAndPersonalArticles(Document doc) {
		HealthCareAndPersonalArticles healthCareAndPersonalArticles = new HealthCareAndPersonalArticles();
		healthCareAndPersonalArticles.setIndicator(extractOneFieldToIndicator("healthCareAndPersonalArticles", doc));
		healthCareAndPersonalArticles
				.setTraditionalChineseMedicine(extractOneFieldToIndicator("traditionalChineseMedicine", doc));
		healthCareAndPersonalArticles.setWesternMedicine(extractOneFieldToIndicator("westernMedicine", doc));
		healthCareAndPersonalArticles.setHealthCareServices(extractOneFieldToIndicator("healthCareServices", doc));
		return healthCareAndPersonalArticles;
	}

	private TransportationAndCommunication assembleTransportationAndCommunication(Document doc) {
		TransportationAndCommunication transportationAndCommunication = new TransportationAndCommunication();
		transportationAndCommunication.setIndicator(extractOneFieldToIndicator("transportationAndCommunication", doc));
		transportationAndCommunication
				.setTransportationFacility(extractOneFieldToIndicator("transportationFacility", doc));
		transportationAndCommunication.setFuelsAndParts(extractOneFieldToIndicator("fuelsAndParts", doc));
		transportationAndCommunication
				.setFeesForVehicleUseAndMaintenance(extractOneFieldToIndicator("feesForVehicleUseAndMaintenance", doc));
		transportationAndCommunication
				.setCommunicationFacility(extractOneFieldToIndicator("communicationFacility", doc));
		transportationAndCommunication.setCommunicationService(extractOneFieldToIndicator("communicationService", doc));
		return transportationAndCommunication;
	}

	private RecreationEducationAndCultureArticles assembleRecreationEducationAndCultureArticles(Document doc) {
		RecreationEducationAndCultureArticles recreationEducationAndCultureArticles = new RecreationEducationAndCultureArticles();
		recreationEducationAndCultureArticles
				.setIndicator(extractOneFieldToIndicator("recreationEducationAndCultureArticles", doc));
		recreationEducationAndCultureArticles
				.setEducationServices(extractOneFieldToIndicator("educationServices", doc));
		recreationEducationAndCultureArticles.setTouringAndOuting(extractOneFieldToIndicator("touringAndOuting", doc));
		return recreationEducationAndCultureArticles;
	}

	private Residence assembleResidence(Document doc) {
		Residence residence = new Residence();
		residence.setIndicator(extractOneFieldToIndicator("residence", doc));
		residence.setBuildingAndBuildingDecorationMaterials(
				extractOneFieldToIndicator("buildingAndBuildingDecorationMaterials", doc));
		residence.setRenting(extractOneFieldToIndicator("renting", doc));
		residence.setWaterElectricityAndFuels(extractOneFieldToIndicator("waterElectricityAndFuels", doc));
		return residence;
	}

	@SuppressWarnings("unchecked")
	private Indicator extractOneFieldToIndicator(String fieldName, Document doc) {
		return convertFromFieldAndList(fieldName, doc.get(fieldName, List.class));
	}

	private Indicator convertFromFieldAndList(String fieldName, List<Double> list) {
		if (list.size() == 3) {
			return new Indicator(fieldName, list.get(0), list.get(1), list.get(2));
		} else if (list.size() == 2) {
			return new Indicator(fieldName, list.get(0), list.get(1));
		} else {
			return null;
		}
	}
}
