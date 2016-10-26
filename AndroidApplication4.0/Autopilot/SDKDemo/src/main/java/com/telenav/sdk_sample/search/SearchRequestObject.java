package com.telenav.sdk_sample.search;

import java.util.List;
import java.util.Locale;
import com.telenav.entity.service.model.common.BBox;
import com.telenav.entity.service.model.common.GeoPoint;
import com.telenav.entity.service.model.common.Polygon;
import com.telenav.entity.service.model.v4.EntityDetailLevel;
import com.telenav.entity.service.model.v4.SortType;
import com.telenav.entity.service.model.v4.SuggestionIntent;

/**
 * Defines a search request object
 */
public class SearchRequestObject {

    /**
     * the location at which the search is performed
     */
    private GeoPoint location;

    /**
     * the term according to which we perform the search
     */

    private String searchQuery;

    /**
     * sort type used for search results
     */
    private SortType sortType = SortType.best_match;

    /**
     * search results language
     */
    private String resultsLanguage = Locale.US.toString();

    /**
     * maximum results number
     */
    private int maximumResultsNumber = 10;

    /**
     * search bounding-box
     */
    private BBox boundingBox;

    /**
     * search categories
     */
    private List<String> categories;

    /**
     * the polygon in which the search is performed ; it defines some coordinates
     */
    private Polygon polygon;

    /**
     * suggestion search type
     */
    private SuggestionIntent suggestionSearchType = SuggestionIntent.around;

    /**
     * search details level (e.g. basic, full)
     */
    private EntityDetailLevel searchDetailsLevel = EntityDetailLevel.full;

    /**
     * entities ids
     */
    private List<String> entityIds;

    /**
     * @return search location
     */
    public GeoPoint getLocation() {
        return location;
    }

    /**
     * sets search location
     * @param location location
     */
    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    /**
     * @return search query
     */
    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * sets search query
     * @param searchQuery search query
     */
    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    /**
     * @return search results sort type
     */
    public SortType getSortType() {
        return sortType;
    }

    /**
     * sets search results sort type
     * @param sortType search results sort type
     */
    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    /**
     * @return search results language
     */
    public String getResultsLanguage() {
        return resultsLanguage;
    }

    /**
     * sets search results language
     * @param resultsLanguage search results language
     */
    public void setResultsLanguage(String resultsLanguage) {
        this.resultsLanguage = resultsLanguage;
    }

    /**
     * @return maximum results number
     */
    public int getMaximumResultsNumber() {
        return maximumResultsNumber;
    }

    /**
     * sets maximum results number
     * @param maximumResultsNumber maximum results number
     */
    public void setMaximumResultsNumber(int maximumResultsNumber) {
        this.maximumResultsNumber = maximumResultsNumber;
    }

    /**
     * @return search bounding-box
     */
    public BBox getBoundingBox() {
        return boundingBox;
    }

    /**
     * sets bounding-box inside which the search is performed
     * @param boundingBox bounding-box inside which the search is performed
     */
    public void setBoundingBox(BBox boundingBox) {
        this.boundingBox = boundingBox;
    }

    /**
     * @return search categories
     */
    public List<String> getCategories() {
        return categories;
    }

    /**
     * sets search categories
     * @param categories search categories
     */
    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    /**
     * @return search polygon
     */
    public Polygon getPolygon() {
        return polygon;
    }

    /**
     * sets the polygon in which the search is performed
     * @param polygon the polygon in which the search is performed
     */
    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }

    /**
     * @return suggestions search type
     */
    public SuggestionIntent getSuggestionSearchType() {
        return suggestionSearchType;
    }

    /**
     * sets suggestions search type
     * @param suggestionSearchType suggestions search type
     */
    public void setSuggestionSearchType(SuggestionIntent suggestionSearchType) {
        this.suggestionSearchType = suggestionSearchType;
    }

    /**
     * @return search details level
     */
    public EntityDetailLevel getSearchDetailsLevel() {
        return searchDetailsLevel;
    }

    /**
     * sets search details level
     * @param searchDetailsLevel search details level
     */
    public void setSearchDetailsLevel(EntityDetailLevel searchDetailsLevel) {
        this.searchDetailsLevel = searchDetailsLevel;
    }

    /**
     * @return entities ids
     */
    public List<String> getEntityIds() {
        return entityIds;
    }

    /**
     * sets the ids for which we search details
     * @param entitiesIds entities ids
     */
    public void setEntityIds(List<String> entitiesIds) {
        this.entityIds = entityIds;
    }
}