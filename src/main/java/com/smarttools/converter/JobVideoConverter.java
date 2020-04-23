package com.smarttools.converter;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class JobVideoConverter {

	private static Logger log=Logger.getLogger(JobVideoConverter.class);
	
	//Se ejecuta con crontab

    public static void main(String args[]) {
    	try {
       	 VideoConverter imagenConverter = new VideoConverter();
       	 final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
         executorService.scheduleAtFixedRate(imagenConverter,0,12, TimeUnit.SECONDS);
            
       }catch (Exception e){
           log.error(e);
       } 
    }

} 
