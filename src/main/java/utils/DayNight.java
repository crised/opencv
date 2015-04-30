package utils;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.TimeZone;

import static utils.Consts.*;

/**
 * Created by crised on 4/30/15.
 */
public class DayNight {

    private static final Logger LOG = LoggerFactory.getLogger(CL_TELEMATIC);


    private SunriseSunsetCalculator calculator;
    private Calendar now, sunrise, sunset;
    private boolean dayMode;

    public DayNight() {
        Location location = new Location(LATITUDE, LONGITUDE);
        calculator = new SunriseSunsetCalculator(location, TimeZone.getTimeZone("GMT-3:00"));
    }

    public boolean isDay() {
        now = Calendar.getInstance();
        sunrise = calculator.getOfficialSunriseCalendarForDate(now);
        sunset = calculator.getOfficialSunsetCalendarForDate(now);
        boolean isDay = now.after(sunrise) && now.before(sunset);
        if (isDay != dayMode) {
            if (isDay) LOG.info("Day mode");
            else LOG.info("Night Mode");
        }
        dayMode = isDay;
        return isDay;
    }

}
