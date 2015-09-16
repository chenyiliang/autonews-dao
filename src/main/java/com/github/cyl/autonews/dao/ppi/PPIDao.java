package com.github.cyl.autonews.dao.ppi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.bson.Document;

import com.github.cyl.autonews.dao.cpi.CPIDao;
import com.github.cyl.autonews.pojo.indicator.Indicator;
import com.github.cyl.autonews.pojo.ppi.MonthPPI;
import com.github.cyl.autonews.pojo.ppi.category.ConsumerGoods;
import com.github.cyl.autonews.pojo.ppi.category.MeansOfProduction;
import com.mongodb.MongoClient;
import com.mongodb.client.model.Filters;

public class PPIDao {
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

	public MonthPPI getOneMonthPPI(int year, int month) {
		Document doc = client.getDatabase("autonews").getCollection("ppi")
				.find(Filters.and(Filters.eq("year", year), Filters.eq("month", month))).first();
		if (doc == null) {
			return null;
		}

		MonthPPI monthPPI = new MonthPPI(year, month);

		monthPPI.setIndicator(extractOneFieldToIndicator("ppi", doc));

		monthPPI.setMiningAndWashingOfCoal(extractOneFieldToIndicator("miningAndWashingOfCoal", doc));
		monthPPI.setExtractionOfPetroleumAndNaturalGas(
				extractOneFieldToIndicator("extractionOfPetroleumAndNaturalGas", doc));
		monthPPI.setMiningAndProcessingOfFerrousMetalOres(
				extractOneFieldToIndicator("miningAndProcessingOfFerrousMetalOres", doc));
		monthPPI.setMiningAndProcessingOfNonFerrousMetalOres(
				extractOneFieldToIndicator("miningAndProcessingOfNonFerrousMetalOres", doc));
		monthPPI.setMiningAndProcessingOfNonmetalOres(
				extractOneFieldToIndicator("miningAndProcessingOfNonmetalOres", doc));
		monthPPI.setProcessingOfFoodFromAgriculturalProducts(
				extractOneFieldToIndicator("processingOfFoodFromAgriculturalProducts", doc));
		monthPPI.setProcessingOfFoodstuff(extractOneFieldToIndicator("processingOfFoodstuff", doc));
		monthPPI.setManufactureOfBeverages(extractOneFieldToIndicator("manufactureOfBeverages", doc));
		monthPPI.setManufactureOfTobacco(extractOneFieldToIndicator("manufactureOfTobacco", doc));
		monthPPI.setManufactureOfTextile(extractOneFieldToIndicator("manufactureOfTextile", doc));
		monthPPI.setManufactureOfTextileWearingApparelFootwareAndCaps(
				extractOneFieldToIndicator("manufactureOfTextileWearingApparelFootwareAndCaps", doc));
		monthPPI.setProcessingOfTimberManufactureOfWoodBamboo(
				extractOneFieldToIndicator("processingOfTimberManufactureOfWoodBamboo", doc));
		monthPPI.setManufactureOfPaperAndPaperProducts(
				extractOneFieldToIndicator("manufactureOfPaperAndPaperProducts", doc));
		monthPPI.setPrintingReproductionOfRecordingMedia(
				extractOneFieldToIndicator("printingReproductionOfRecordingMedia", doc));
		monthPPI.setProcessingOfPetroleumCokingProcessingOfNuclearFuel(
				extractOneFieldToIndicator("processingOfPetroleumCokingProcessingOfNuclearFuel", doc));
		monthPPI.setManufactureOfRawChemicalMaterialsAndChemicalProducts(
				extractOneFieldToIndicator("manufactureOfRawChemicalMaterialsAndChemicalProducts", doc));
		monthPPI.setManufactureOfMedicines(extractOneFieldToIndicator("manufactureOfMedicines", doc));
		monthPPI.setManufactureOfChemicalFibers(extractOneFieldToIndicator("manufactureOfChemicalFibers", doc));
		monthPPI.setManufactureOfRubberAndPlastics(extractOneFieldToIndicator("manufactureOfRubberAndPlastics", doc));
		monthPPI.setManufactureOfNonMetallicMineralProducts(
				extractOneFieldToIndicator("manufactureOfNonMetallicMineralProducts", doc));
		monthPPI.setSmeltingAndPressingOfFerrousMetals(
				extractOneFieldToIndicator("smeltingAndPressingOfFerrousMetals", doc));
		monthPPI.setSmeltingAndPressingOfNonFerrousMetals(
				extractOneFieldToIndicator("smeltingAndPressingOfNonFerrousMetals", doc));
		monthPPI.setManufactureOfMetalProducts(extractOneFieldToIndicator("manufactureOfMetalProducts", doc));
		monthPPI.setManufactureOfGeneralPurposeMachinery(
				extractOneFieldToIndicator("manufactureOfGeneralPurposeMachinery", doc));
		monthPPI.setManufactureOfAutomotive(extractOneFieldToIndicator("manufactureOfAutomotive", doc));
		monthPPI.setManufactureOfTransportEquipment(extractOneFieldToIndicator("manufactureOfTransportEquipment", doc));
		monthPPI.setManufactureOfCommunicationEquipmentComputersAndOtherElectronicEquipment(extractOneFieldToIndicator(
				"manufactureOfCommunicationEquipmentComputersAndOtherElectronicEquipment", doc));
		monthPPI.setProductionAndSupplyOfElectricPowerAndHeatPower(
				extractOneFieldToIndicator("productionAndSupplyOfElectricPowerAndHeatPower", doc));
		monthPPI.setProductionAndSupplyOfGas(extractOneFieldToIndicator("productionAndSupplyOfGas", doc));
		monthPPI.setProductionAndSupplyOfWater(extractOneFieldToIndicator("productionAndSupplyOfWater", doc));

		monthPPI.setMeansOfProduction(assembleMeansOfProduction(doc));
		monthPPI.setConsumerGoods(assembleConsumerGoods(doc));

		return monthPPI;
	}

	private MeansOfProduction assembleMeansOfProduction(Document doc) {
		MeansOfProduction meansOfProduction = new MeansOfProduction();
		meansOfProduction.setIndicator(extractOneFieldToIndicator("meansOfProduction", doc));
		meansOfProduction.setMiningAndQuarryingIndustry(extractOneFieldToIndicator("miningAndQuarryingIndustry", doc));
		meansOfProduction.setRawMaterialsIndustry(extractOneFieldToIndicator("rawMaterialsIndustry", doc));
		meansOfProduction.setProcessingIndustry(extractOneFieldToIndicator("processingIndustry", doc));
		return meansOfProduction;
	}

	private ConsumerGoods assembleConsumerGoods(Document doc) {
		ConsumerGoods consumerGoods = new ConsumerGoods();
		consumerGoods.setIndicator(extractOneFieldToIndicator("consumerGoods", doc));
		consumerGoods.setFood(extractOneFieldToIndicator("food", doc));
		consumerGoods.setClothing(extractOneFieldToIndicator("clothing", doc));
		consumerGoods.setArticlesForDailyUse(extractOneFieldToIndicator("articlesForDailyUse", doc));
		consumerGoods.setDurableConsumerGoods(extractOneFieldToIndicator("durableConsumerGoods", doc));
		return consumerGoods;
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
