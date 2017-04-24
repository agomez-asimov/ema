package ar.asimov.acumar.ema.model.helper;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherSummary;
import ar.asimov.acumar.ema.model.WindDirection;
import ar.asimov.acumar.ema.model.WindDistributionEntry;
import ar.asimov.acumar.ema.wlk.data.DailySummaryData;

public class WeatherSummaryMapper {
	
	public static final WeatherSummary map(DailySummaryData summary, Station station){
		final WeatherSummary result = new WeatherSummary();
		result.setDate(summary.getDate());
		result.setStation(station);
		result.setDataSpan(summary.getDataSpan());
		result.setMaxOutTemperature(summary.getMaxOutTemperature());
		result.setMaxOutTemperatureTime(summary.getMaxOutTemperatureTime());
		result.setMinOutTemperature(summary.getMinOutTemperature());
		result.setMinOutTemperatureTime(summary.getMinOutTemperatureTime());
		result.setAvgOutTemperature(summary.getAvgOutTemperature());
		result.setMaxInTemperature(summary.getMaxInTemperature());
		result.setMaxInTemperatureTime(summary.getMaxInTemperatureTime());
		result.setMinInTemperature(summary.getMinInTemperature());
		result.setMinInTemperatureTime(summary.getMinInTemperatureTime());
		result.setAvgInTemperature(summary.getAvgInTemperature());
		result.setMaxWindChill(summary.getMaxWindChill());
		result.setMaxWindChillTime(summary.getMaxWindChillTime());
		result.setMinWindChill(summary.getMinWindChill());
		result.setMinWindChillTime(summary.getMinWindChillTime());
		result.setAvgWindChill(summary.getAvgWindChill());
		result.setMaxDewPoint(summary.getMaxDewPoint());
		result.setMaxDewPointTime(summary.getMaxDewPointTime());
		result.setMinDewPoint(summary.getMinDewPoint());
		result.setMinDewPointTime(summary.getMinDewPointTime());
		result.setAvgDewPoint(summary.getAvgDewPoint());
		result.setMaxOutHumidity(summary.getMaxOutHumidity());
		result.setMaxOutHumidityTime(summary.getMaxOutHumidityTime());
		result.setMinOutHumidity(summary.getMinOutHumidity());
		result.setMinOutHumidityTime(summary.getMinOutHumidityTime());
		result.setAvgOutHumidity(summary.getAvgOutHumidity());
		result.setMaxInHumidity(summary.getMaxInHumidity());
		result.setMaxInHumidityTime(summary.getMaxInHumidityTime());
		result.setMinInHumidity(summary.getMinInHumidity());
		result.setMinInHumidityTime(summary.getMinInHumidityTime());
		result.setMaxPressure(summary.getMaxPressure());
		result.setMaxPressureTime(summary.getMaxPressureTime());
		result.setMinPressure(summary.getMinPressure());
		result.setMinPressureTime(summary.getMinPressureTime());
		result.setAvgPressure(summary.getAvgPressure());
		result.setDailyWindRun(summary.getDailyWindRun());
		result.setMaxAvg10MinWindSpeed(summary.getMaxAvg10MinWindSpeed());
		result.setMaxAvg10MinWindSpeedTime(summary.getMaxAvg10MinWindSpeedTime());
		result.setMaxAvg10MinWindSpeedDirection(summary.getMaxAvg10MinWindSpeedDirection());
		result.setAvgWindSpeed(summary.getAvgWindSpeed());
		result.setMaxWindSpeed(summary.getMaxWindSpeed());
		result.setMaxWindSpeedTime(summary.getMaxWindSpeedTime());
		result.setMaxWindSpeedDirection(summary.getMaxWindSpeedDirection());
		result.setDailyPrecipitation(summary.getDailyPrecipitation());
		result.setMaxPrecipitationRate(summary.getMaxPrecipitationRate());
		result.setMaxPrecipitationRateTime(summary.getMaxPrecipitationRateTime());
		result.setDailyUVDose(summary.getDailyUVDose());
		result.setMaxUVDose(summary.getMaxUVDose());
		result.setMaxUVDoseTime(summary.getMaxUVDoseTime());
		result.setWindPackets(summary.getWindPackets());
		result.setMaxSolar(summary.getMaxSolar());
		result.setMaxSolarTime(summary.getMaxSolarTime());
		result.setMinSunlinght(summary.getMinSunlinght());
		result.setDailySolarEnergy(summary.getDailySolarEnergy());
		result.setDailyET(summary.getDailyET());
		result.setMaxHeat(summary.getMaxHeat());
		result.setMaxHeatTime(summary.getMaxHeatTime());
		result.setMinHeat(summary.getMinHeat());
		result.setMinHeatTime(summary.getMinHeatTime());
		result.setAvgHeat(summary.getAvgHeat());
		result.setMaxTHSW(summary.getMaxTHSW());
		result.setMaxTHSWTime(summary.getMaxTHSWTime());
		result.setMinTHSW(summary.getMinTHSW());
		result.setMinTHSWTime(summary.getMinTHSWTime());
		result.setMaxTHW(summary.getMaxTHW());
		result.setMaxTHWTime(summary.getMaxTHWTime());
		result.setMinTHW(summary.getMinTHW());
		result.setMinTHWTime(summary.getMinTHWTime());
		result.setIntegratedHeatDD65(summary.getIntegratedHeatDD65());
		result.setIntegratedCoolDD65(summary.getIntegratedCoolDD65());
		LocalTime[] windDirectionDistribution = summary.getWindDirectionDistribution();
		List<WindDistributionEntry> windDistributions = new ArrayList<>();
		for(int i = 0;i<windDirectionDistribution.length;i++){
			WindDistributionEntry entry = new WindDistributionEntry();
			entry.setDate(result.getDate());
			entry.setStation(result.getStation());
			switch(i){
			case 0:
				entry.setDirection(WindDirection.N);
				break;
			case 1: 
				entry.setDirection(WindDirection.NNE);
				break;
			case 2:
				entry.setDirection(WindDirection.NE);
				break;
			case 3:
				entry.setDirection(WindDirection.ENE);
				break;
			case 4:
				entry.setDirection(WindDirection.E);
				break;
			case 5:
				entry.setDirection(WindDirection.ESE);
				break;
			case 6:
				entry.setDirection(WindDirection.SE);
				break;
			case 7:
				entry.setDirection(WindDirection.SSE);
				break;
			case 8:
				entry.setDirection(WindDirection.S);
				break;
			case 9:
				entry.setDirection(WindDirection.SSW);
				break;
			case 10:
				entry.setDirection(WindDirection.SW);
				break;
			case 11:
				entry.setDirection(WindDirection.WSW);
				break;
			case 12:
				entry.setDirection(WindDirection.W);
				break;
			case 13:
				entry.setDirection(WindDirection.WNW);
				break;
			case 14:
				entry.setDirection(WindDirection.NW);
				break;
			case 15:
				entry.setDirection(WindDirection.NNW);
				break;
			}
			entry.setTime(windDirectionDistribution[i]);
			windDistributions.add(new WindDistributionEntry());
		}
		result.setWindDirectionDistribution(windDistributions);
		return result;
	}

}
