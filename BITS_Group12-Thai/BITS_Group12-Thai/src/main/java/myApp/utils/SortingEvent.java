package myApp.utils;

import javafx.event.Event;
import javafx.event.EventType;

public class SortingEvent extends javafx.event.Event {
    public static final EventType<SortingEvent> SORTING_EVENT_TYPE = new EventType<>(ANY, "SORTING_EVENT");

    private String sortingQuery;

    public SortingEvent(String sortingQuery) {
        super(SORTING_EVENT_TYPE);
        this.sortingQuery = sortingQuery;
    }

    public String getSortingQuery() {
        return sortingQuery;
    }
}
