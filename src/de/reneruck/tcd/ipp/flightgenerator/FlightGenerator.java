package de.reneruck.tcd.ipp.flightgenerator;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class FlightGenerator {

	private static long oneHourInMs = 3600000;
	private static List<String> flightStatements = new LinkedList<>();

	public static void main(String[] args) {
		SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		Calendar calendar = Calendar.getInstance();

		try {
			Date parsedDate = formater.parse("28.10.2012 21:00");
			Date now = new Date(System.currentTimeMillis());

			calendar.setTime(parsedDate);

			if (now.after(parsedDate)) {
				System.err.println("Choose a date in the Future");
			} else {
				while (now.before(calendar.getTime())) {

					if (calendar.get(Calendar.HOUR_OF_DAY) == 9) {
						calendar.add(Calendar.HOUR_OF_DAY, -12);
						checkForDaylightSavingJump(calendar);
					} else {
						calendar.add(Calendar.HOUR_OF_DAY, -3);
					}
					System.out.println(formater.format(calendar.getTime()));
					generateSQLStatement(calendar, flightStatements);
					printToFile(flightStatements);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static void printToFile(List<String> flightStatements) {
		try {
			FileUtils.writeLines(new File("flightStatements.txt"), flightStatements);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void generateSQLStatement(Calendar calendar, List<String> flightStatements) {
		long timeInMillis = calendar.getTimeInMillis() + oneHourInMs;
		String statement = "insert into flight (Date,DepartureAirport,ArrivalAirport) values(" + calendar.getTimeInMillis() + ",0,1);";
		String statementBack = "insert into flight (Date,DepartureAirport,ArrivalAirport) values(" + timeInMillis + ",1,0);";
		flightStatements.add(statement);
		flightStatements.add(statementBack);
	}

	private static void checkForDaylightSavingJump(Calendar calendar) {
		if(calendar.get(Calendar.HOUR_OF_DAY) % 3 != 0)
		{
			calendar.add(Calendar.HOUR_OF_DAY, -1);
			if(calendar.get(Calendar.HOUR_OF_DAY) % 3 != 0) {
				calendar.add(Calendar.HOUR_OF_DAY, 2);
			}
		}
	}
}
