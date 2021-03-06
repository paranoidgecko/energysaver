package org.moxhu.esavegame.business.test;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

import org.moxhu.MoxhuObject;
import org.moxhu.esavegame.dao.EnergySaverDAO;
import org.moxhu.esavegame.dao.MongoDBEnergySaverDAO;
import org.moxhu.esavegame.domain.PowerTransaction;
import org.moxhu.exception.GeneralException;
import org.moxhu.util.config.Configurations;
import org.moxhu.util.db.mongodb.MongoDBConnectionManager;
import org.moxhu.web.app.ContextApplication;
import org.moxhu.web.app.exception.BadRequestException;
import org.moxhu.web.app.exception.NotFoundException;
import org.moxhu.web.app.exception.RequestException;
import org.moxhu.web.app.exception.UnauthorizedRequestException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;


public class CompleteLogExporter implements MoxhuObject{

	public static final String PROPERTIES_FILE = "exporter.properties";
	
	DB db = null;
	
	public void exportToFile(){
		
		Properties props = new Properties();
		try{
			props.load(this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE));
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
			}
			try {
			ContextApplication.getInstance().start();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			Configurations conf = new Configurations(props, PROPERTIES_FILE); 
		
			
					String[] plugwiseArray = { "025F6C03",
					"00728967",
					"00728981",
					"00728983",
					"00728C99",
					"00728D6F",
					"00728E46",
					"00728E66",
					"007290C9",
					"007296AB",
					"00729808",
					"00729886",
					"00729903",
					"00729915",
					"00729D6B",
					"00729D8D",
					"00729DD1",
					"0072A23E",
					"0072A278",
					"0072A2A1",
					"0072B401",
					"0072B645",
					"00AF6325",
					"00C3CEBA",
					"00D327F5",
					"00D327FF",
					"00D328D9",
					"00D33D78",
					"00D33D8C",
					"00D368EB",
					"01A44514",
					"01A4453C",
					"01A44573",
					"02588111",
					"0258816B",
					"02588B35",
					"02588BAE",
					"02588BD4",
					"025F6A28",
					"025F6A2C",
					"025F6A4F",
					"025F6AB0",
					"025F6AC3",
					"025F6BFC",
					"025F6C03",
					"025F6C11",
					"02604664",
					"026048B7",
					"026048ED",
					"026048F8",
					"02604908",
					"02604938",
					"0260493A",
					"02604952",
					"026049C0",
					"026049F1",
					"02604A05",
					"02604A24",
					"02604A9C",
					"0261AFF2",
					"0261B009",
					"0261B1C2",
					"0261B2C4",
					"0261B2EF",
					"0261B3E9",
					"0261B42C",
					"0261B464",
					"0261B561",
					"0261B570",
					"0261B6E1",
					"0261B6E4",
					"0261B6EA",
					"02620810"
		
		};
String plugwisedevice = conf.getConfiguration("plugid");

int countPlugs = 0;
	//while(countPlugs<plugwiseArray.length) {
plugwisedevice = plugwiseArray[countPlugs];

			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(GregorianCalendar.DAY_OF_WEEK, GregorianCalendar.MONDAY);
			int day = calendar.get(GregorianCalendar.DAY_OF_MONTH);
			int month = calendar.get(GregorianCalendar.MONTH);
			int year = calendar.get(GregorianCalendar.YEAR);
			GregorianCalendar calendar2 = new GregorianCalendar(year, month,
			        day);
			calendar2.set(GregorianCalendar.DAY_OF_MONTH, day-1);
			long defaultStartTime = calendar2.getTime().getTime()-86400000;
			long startTime = conf.getConfiguration("starttime", defaultStartTime);
			long endTime = startTime + 86400000;
			endTime = conf.getConfiguration("endtime", endTime);
			Vector<PowerTransaction> transactions = new Vector<PowerTransaction>(); 
			try {
				LOGGER.debug("Starting exporter");
				
				db = MongoDBConnectionManager.getInstance().getDBConnection();
				LOGGER.debug("Connected");
				
				transactions = this.getPowerTransactionsForTimePeriod(plugwisedevice ,
						startTime,  endTime);
			} catch (RequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnauthorizedRequestException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		
		
		PrintWriter pw = null;
		float totalWatts = 1;
		String line = "";
		String formatedDate = "";
		Format formatter;

		// The year
		formatter = new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss Z");    // 02
		for (int index = 0; index < transactions.size(); index++) {
			formatedDate = formatter.format(new Date(transactions.get(index).getTime()));
			line += transactions.get(index).getTime() + "," + formatedDate + "," + transactions.get(index).getPlugId() + ","+ transactions.get(index).getWatts() +"\n";
			//line += transactions.get(index).getWatts() +"\n";
			//LOGGER.debug(transactions.get(index).getTime() + "," + transactions.get(index).getPlugId() + ","+ transactions.get(index).getWatts() +"\n");
			totalWatts += transactions.get(index).getWatts();
		}
		float average = totalWatts / transactions.size();
		line +="Size="+transactions.size()+ "\n"+"TotalWatts=" + totalWatts+"\n"+"Average="+average+"\n";
		LOGGER.debug("Size="+transactions.size()+ "\n"+"TotalWatts=" + totalWatts+"\n"+"Average="+average+"\n");
		try {
			pw = new PrintWriter(new FileWriter(props.getProperty("export.file", "/home/jmunoza/wee/HTI/graduationproject/experiments/logs_complete2/"+plugwisedevice+"_"+startTime+".txt")));
				pw.println(line);
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			pw.close();
		}
		
	//	countPlugs++;
	//}//while countPlugs
		/*
		 
		 String[] ids = TimeZone.getAvailableIDs(-6 * 60 * 60 * 1000);
		 
		  for(String timeZone : ids){
			System.out.println(timeZone);
		}
		 SimpleTimeZone cdt = new SimpleTimeZone(-6 * 60 * 60 * 1000, ids[12]);
		Calendar calendar = new GregorianCalendar(cdt);
		System.out.println(calendar.getTime());
		calendar.setTime(new Date("Tue May 04 19:35:42 CDT 2010"));
//		calendar.HOUR
		calendar.
		System.out.println(calendar.getTime());*/
		
		
	}
	
	
	public Vector<PowerTransaction> getPowerTransactionsForTimePeriod(String plugId,
			long startTime, long endTime) throws BadRequestException, NotFoundException, RequestException, UnauthorizedRequestException{
		
		Vector<PowerTransaction> vectorPowTrans = null;
		DBCollection trans = db.getCollection("powertransactions");
		//db.powertransactions.find({'plugid':'729903','time':{$gt:134005680000},'time':{$lt:1340143200000}}). 
		//BasicDBObject query = new BasicDBObject("plugid", plugId);
		//query.append("time", new BasicDBObject("$gt",startTime));
		//query.append("time", new BasicDBObject("$lt", endTime));
		BasicDBObject query = new BasicDBObject("plugid", plugId).append("time", new BasicDBObject("$lt", endTime).append("$gt",startTime));
		LOGGER.debug(query);
        try{
        	DBCursor cursor = trans.find(query).sort(new BasicDBObject("time", 1));
        	vectorPowTrans = new Vector<PowerTransaction>(cursor.size());
        	
        	while (cursor.hasNext()) {
        		//LOGGER.info("Cursor has more");
        		BasicDBObject object = (BasicDBObject)cursor.next();
        		PowerTransaction powerTrans = new PowerTransaction(); 
        		powerTrans.setPlugId(plugId);
        		powerTrans.setTime(object.getLong("time"));
        		powerTrans.setWatts(Float.parseFloat(object.getString("power")));
        		vectorPowTrans.add(powerTrans);
			}
		cursor.close();
        }catch (NoSuchElementException nse){
        	LOGGER.debug(nse.getMessage());
        	nse.fillInStackTrace();
        	 throw new NotFoundException("PowerTrans Vector not found" + query );
        }catch (Exception e){
        	LOGGER.error("[DAO] ERROR while getting PowerTrans Vector " + query, e);
        	e.fillInStackTrace();
        	throw new RequestException(e.getMessage());
        }
		return vectorPowTrans;
	}
	
	public static void main(String[] args){
	
		CompleteLogExporter cvsExporter = new CompleteLogExporter();
		cvsExporter.exportToFile();
	}
}
