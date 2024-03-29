package myApp.utils;

import java.time.LocalDate;
import java.util.Comparator;

public class LocalDateComparator implements Comparator<LocalDate> {

    @Override
    public int compare(LocalDate date1, LocalDate date2) {
        return date1.compareTo(date2);
    }
}
