package ar.asimov.acumar.ema.model.helper;

import java.util.ArrayList;
import java.util.List;

import ar.asimov.acumar.ema.model.ExtraHumidity;
import ar.asimov.acumar.ema.model.ExtraTemperature;
import ar.asimov.acumar.ema.model.LeafTemperature;
import ar.asimov.acumar.ema.model.LeafWetness;
import ar.asimov.acumar.ema.model.NewSensor;
import ar.asimov.acumar.ema.model.SoilMoisture;
import ar.asimov.acumar.ema.model.SoilTemperature;
import ar.asimov.acumar.ema.model.Station;
import ar.asimov.acumar.ema.model.WeatherData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;

public class WeatherDataMapper {
	
	public static final WeatherData map(DailyWeatherData data,Station station){
		WeatherData result = new WeatherData();
		result.setDate(data.getDate());
		result.setStation(station);
		result.setStartTime(data.getStartTime());
		result.setEndTime(data.getEndTime());
		result.setOutsideTemperature(data.getOutsideTemperature());
		result.setMaxOutsideTemperature(data.getMaxOutsideTemperature());
		result.setMinOutsideTemperature(data.getMinOutsideTemperature());
		result.setInsideTemperature(data.getInsideTemperature());
		result.setPressure(data.getPressure());
		result.setOutsideHumidity(data.getOutsideHumidity());
		result.setInsideHumidity(data.getInsideHumidity());
		result.setPrecipitation(data.getPrecipitation());
		result.setMaxPrecipitationRate(data.getMaxPrecipitationRate());
		result.setWindSpeed(data.getWindSpeed());
		result.setMaxWindSpeed(data.getMaxWindSpeed());
		result.setWindSamplesNumber(data.getWindSamplesNumber());
		result.setSolarRadiation(data.getSolarRadiation());
		result.setMaxSolarRadiation(data.getMaxSolarRadiation());
		result.setUVIndex(data.getUVIndex());
		result.setMaxUVIndex(data.getMaxUVIndex());
		result.setExtraRadiation(data.getExtraRadiation());
		result.setForecast(data.getForecast());
		result.setET(data.getET());
		result.setIconFlags(data.getIconFlags());
		result.setRainCollectorType(data.getRainCollectorType());
		result.setWindDirection(data.getWindDirection());
		result.setMaxWindDirection(data.getMaxWindDirection());
		result.setMoreFlags(data.getMoreFlags());
		List<LeafTemperature> localLeafTemperature = new ArrayList<>();
		for (int i = 0; i < data.getLeafTemperature().size(); i++) {
			LeafTemperature lt = new LeafTemperature();
			lt.setStation(station);
			lt.setDate(result.getDate());
			lt.setStartTime(result.getStartTime());
			lt.setOrder(i);
			lt.setValue((int) data.getLeafTemperature(i));
			localLeafTemperature.add(lt);
		}
		result.setLeafTemperature(localLeafTemperature);
		List<NewSensor> localNewSensors = new ArrayList<>();
		for (int i = 0; i < data.getNewSensors().size(); i++) {
			NewSensor ns = new NewSensor();
			ns.setStation(station);
			ns.setDate(result.getDate());
			ns.setStartTime(result.getStartTime());
			ns.setOrder(i);
			ns.setValue((int) data.getNewSensor(i));
			localNewSensors.add(ns);
		}
		result.setNewSensors(localNewSensors);
		List<SoilTemperature> localSoilTemperature = new ArrayList<>();
		for (int i = 0; i < data.getSoilTemperature().size(); i++) {
			SoilTemperature st = new SoilTemperature();
			st.setStation(station);
			st.setDate(result.getDate());
			st.setStartTime(result.getStartTime());
			st.setOrder(i);
			st.setValue((int) data.getSoilTemperature(i));
			localSoilTemperature.add(st);
		}
		result.setSoilTemperature(localSoilTemperature);
		List<SoilMoisture> localSoilMoisture = new ArrayList<>();
		for (int i = 0; i < data.getSoilMoisture().size(); i++) {
			SoilMoisture sm = new SoilMoisture();
			sm.setStation(station);
			sm.setDate(result.getDate());
			sm.setStartTime(result.getStartTime());
			sm.setOrder(i);
			sm.setValue((int) data.getSoilMoisture(i));
			localSoilMoisture.add(sm);
		}
		result.setSoilMoisture(localSoilMoisture);
		List<LeafWetness> localLeafWetness = new ArrayList<>();
		for (int i = 0; i < data.getLeafWetness().size(); i++) {
			LeafWetness lw = new LeafWetness();
			lw.setStation(station);
			lw.setDate(result.getDate());
			lw.setStartTime(result.getStartTime());
			lw.setOrder(i);
			lw.setValue((int) data.getLeafWetness(i));
			localLeafWetness.add(lw);
		}
		result.setLeafWetness(localLeafWetness);
		List<ExtraTemperature> localExtraTemperature = new ArrayList<>();
		for (int i = 0; i < data.getExtraTemperature().size(); i++) {
			ExtraTemperature et = new ExtraTemperature();
			et.setStation(station);
			et.setDate(result.getDate());
			et.setStartTime(result.getStartTime());
			et.setOrder(i);
			et.setValue((int) data.getExtraTemperature(i));
			localExtraTemperature.add(et);
		}
		result.setExtraTemperature(localExtraTemperature);
		List<ExtraHumidity> localExtraHumidity = new ArrayList<>();
		for (int i = 0; i < data.getExtraHumidity().size(); i++) {
			ExtraHumidity eh = new ExtraHumidity();
			eh.setStation(station);
			eh.setDate(result.getDate());
			eh.setStartTime(result.getStartTime());
			eh.setOrder(i);
			eh.setValue((int) data.getExtraHumidity(i));
			localExtraHumidity.add(eh);
		}
		result.setExtraHumidity(localExtraHumidity);
		return result;
	}
	

}
