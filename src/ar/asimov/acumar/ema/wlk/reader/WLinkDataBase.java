package ar.asimov.acumar.ema.wlk.reader;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import ar.asimov.acumar.ema.wlk.data.DailySummaryData;
import ar.asimov.acumar.ema.wlk.data.DailyWeatherData;

public class WLinkDataBase {
	
	private Path path;
	private Map<YearMonth,WLinkFileReader> readers;
	private List<YearMonth> availables;
	private Instant lastModified;
	
	public WLinkDataBase(String path) {
		this(Paths.get(path));
	}
	
	public WLinkDataBase(Path path){
		this.path = path;
		this.lastModified = null;
		try {
			DirectoryStream<Path> ds = Files.newDirectoryStream(path,"*.wlk");
			for(Path p : ds){
				YearMonth date = YearMonth.parse(FilenameUtils.removeExtension(this.path.getFileName().toString()),DateTimeFormatter.ofPattern("y-M"));
				this.getAvailables().add(date);
				Instant localLastModified = Files.getLastModifiedTime(path).toInstant();
				if(this.lastModified == null || this.lastModified.isBefore(localLastModified)) this.lastModified = localLastModified;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private List<YearMonth> getAvailables(){
		if(null == this.availables){
			this.availables = new ArrayList<>();
		}
		return this.availables;
	}
	
	private Map<YearMonth,WLinkFileReader> getReaders(){
		if(this.readers == null){
			this.readers = new HashMap<>();
		}
		return this.readers;
	}
	
	private boolean isAvailable(YearMonth date){
		return this.availables.contains(date);
	}
	
	private WLinkFileReader getReader(YearMonth date){
		try{
			if(this.isAvailable(date)){
				if(!this.getReaders().containsKey(date)){
					String fileName = date.format(DateTimeFormatter.ofPattern("y-M"));
					this.getReaders().put(date, new WLinkFileReader(Paths.get(this.path.toString(),fileName,".wlk")));
				}
				return this.getReaders().get(date);
			}else{
				return null;
				
			}
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	
	private boolean contains(LocalDate date){
		if(this.availables.contains(YearMonth.from(date))){
			WLinkFileReader reader = this.getReader(YearMonth.from(date));
			return reader.contains(date);
		}else{
			return false;
		}
	}
	
	public DailySummaryData fetchSummary(LocalDate date) throws IOException{
		return this.getReader(YearMonth.from(date)).readDay(date.getDayOfMonth());
	}
	
	public DailyWeatherData fetchData(LocalDate date,Integer recordIndex) throws IOException{
		return this.getReader(YearMonth.from(date)).read(date.getDayOfMonth(), recordIndex);
	}
	
	public Integer getRecordsInDay(LocalDate date){
		if(this.contains(date)){
			return this.getReader(YearMonth.from(date)).recordsInDay(date.getDayOfMonth());
		}else{
			return 0;
		}
	}
	
	
		
}
