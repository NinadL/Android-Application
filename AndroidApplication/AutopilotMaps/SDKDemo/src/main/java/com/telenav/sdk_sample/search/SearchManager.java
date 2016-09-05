package com.telenav.sdk_sample.search;

import java.io.File;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.support.annotation.IntDef;
import com.telenav.entity.bindings.android.EntityException;
import com.telenav.entity.bindings.android.EntityService;
import com.telenav.entity.bindings.android.EntityServiceConfig;
import com.telenav.entity.bindings.android.EntityServiceContext;
import com.telenav.entity.bindings.android.cloud.CloudEntityService;
import com.telenav.entity.bindings.android.embedded.EmbeddedEntityService;
import com.telenav.entity.service.model.common.BBox;
import com.telenav.entity.service.model.common.GeoPoint;
import com.telenav.entity.service.model.common.Polygon;
import com.telenav.entity.service.model.v4.EntityCategoryRequest;
import com.telenav.entity.service.model.v4.EntityCategoryResponse;
import com.telenav.entity.service.model.v4.EntityDetailRequest;
import com.telenav.entity.service.model.v4.EntityDetailResponse;
import com.telenav.entity.service.model.v4.EntityRgcRequest;
import com.telenav.entity.service.model.v4.EntitySearchRequest;
import com.telenav.entity.service.model.v4.EntitySearchResponse;
import com.telenav.entity.service.model.v4.EntitySuggestionRequest;
import com.telenav.entity.service.model.v4.EntitySuggestionResponse;
import com.telenav.entity.service.model.v4.MobilityModel;
import com.telenav.sdk_sample.application.PreferenceTypes;
import com.telenav.sdk_sample.application.SdkSampleApplication;
import com.telenav.sdk_sample.ui.map.MapActivity;

/**
 * Defines a class used for search
 * Copyright reserved by Telenav.Inc
 */
public class SearchManager {

    /**
     * search types
     */
    public static final byte SEARCH_ONLINE = 0;

    public static final byte SEARCH_OFFLINE = 1;

    private static final String CLOUD_SEARCH_URL = "https://restapi.telenav.com";

    /**
     * entity search instance
     */
    private static SearchManager instance;

    /**
     * the embedded entity service that will perform the offline search
     */
    private EntityService embeddedEntityService;

    /**
     * the cloud entity service that will perform the online search
     */
//    private EntityService cloudEntityService;

    /**
     * the entity service context
     */
    private EntityServiceContext entityServiceContext;

    /**
     * the search type that will be performed
     */
    private byte searchType;

    /**
     * creates an object of this type
     */
    private SearchManager() {
        // default search type
        searchType = SEARCH_ONLINE;

        // embedded service
        EntityServiceConfig embeddedServiceConfig = EntityServiceConfig.createEmbeddedEntityServiceConfig(MapActivity.DEVELOPER_API_KEY, MapActivity.DEVELOPER_SECRET_API_KEY,
                SdkSampleApplication.getInstance().getApplicationPreferences().getStringPreference(PreferenceTypes.K_MAP_DATA_PATH) + File.separator + "onboard_search");
        try {
            embeddedEntityService = new EmbeddedEntityService(embeddedServiceConfig);
        } catch (EntityException e) {
            embeddedEntityService = null;
        }

        // cloud service
        EntityServiceConfig cloudServiceConfig = EntityServiceConfig.createCloudEntityServiceConfig(MapActivity.DEVELOPER_API_KEY, MapActivity.DEVELOPER_SECRET_API_KEY,
                CLOUD_SEARCH_URL);
//        cloudEntityService = new CloudEntityService(null, cloudServiceConfig);

        // entity service context
        entityServiceContext = new EntityServiceContext();
        entityServiceContext.setLocalTime(new Date());
        entityServiceContext.setMobilityModel(MobilityModel.drive);
    }

    /**
     * @return an instance of this type
     */
    public static SearchManager getInstance() {
        if (instance == null) {
            synchronized (SearchManager.class) {
                if (instance == null) {
                    instance = new SearchManager();
                }
            }
        }
        return instance;
    }

    /**
     * @return current search type
     */
    public byte getSearchType() {
        return searchType;
    }

    /**
     * sets the search type that will be performed
     * @param searchType search type
     */
    public void setSearchType(@SearchType byte searchType) {
        this.searchType = searchType;
    }

    /**
     * perform a search given the parameters
     * @param searchRequestObject search request object
     * @return a response that will contain the search results
     */
    public synchronized EntitySearchResponse search(SearchRequestObject searchRequestObject) {
        if (searchRequestObject != null) {
            EntitySearchRequest request = new EntitySearchRequest();
            String searchQuery = searchRequestObject.getSearchQuery();
            if (searchQuery != null) {
                request.setQuery(searchQuery);
            }
            GeoPoint location = searchRequestObject.getLocation();
            if (location != null) {
                request.setLocation(location);
            }
            List<String> categories = searchRequestObject.getCategories();
            if (categories != null) {
                request.setCategories(searchRequestObject.getCategories());
            }
            BBox boundingBox = searchRequestObject.getBoundingBox();
            if (boundingBox != null) {
                request.setBbox(boundingBox);
            }
            Polygon polygon = searchRequestObject.getPolygon();
            if (polygon != null) {
                request.setPolygon(polygon);
            }
            request.setSort(searchRequestObject.getSortType());
            request.setLocale(searchRequestObject.getResultsLanguage());
            request.setLimit(searchRequestObject.getMaximumResultsNumber());

            switch (searchType) {
                case SEARCH_ONLINE:
 //                   return cloudEntityService.search(request, entityServiceContext);
                default:
                    if (embeddedEntityService != null) {
                        return embeddedEntityService.search(request, entityServiceContext);
                    } else {
                        return null;
                    }
            }
        } else {
            return null;
        }
    }

    /**
     * get possible suggestions searched items
     * @param searchRequestObject search request object
     * @return a response that will contain the suggested results
     */
    public synchronized EntitySuggestionResponse getSuggestions(SearchRequestObject searchRequestObject) {
        if (searchRequestObject != null) {
            EntitySuggestionRequest request = new EntitySuggestionRequest();
            String searchQuery = searchRequestObject.getSearchQuery();
            if (searchQuery != null) {
                request.setQuery(searchQuery);
            }
            GeoPoint location = searchRequestObject.getLocation();
            if (location != null) {
                request.setLocation(location);
            }
            request.setLocale(searchRequestObject.getResultsLanguage());
            request.setLimit(searchRequestObject.getMaximumResultsNumber());
            request.setIntent(searchRequestObject.getSuggestionSearchType());

            switch (searchType) {
                case SEARCH_ONLINE:
//                    return cloudEntityService.suggestions(request, entityServiceContext);
                default:
                    if (embeddedEntityService != null) {
                        return embeddedEntityService.suggestions(request, entityServiceContext);
                    } else {
                        return null;
                    }
            }
        } else {
            return null;
        }
    }

    /**
     * get details for search result with a certain entity id
     * @param searchRequestObject search request object
     * @return the detailed response about that search result
     */
    public synchronized EntityDetailResponse getDetails(SearchRequestObject searchRequestObject) {
        if (searchRequestObject != null) {
            EntityDetailRequest request = new EntityDetailRequest();
            List<String> entitiesIds = searchRequestObject.getEntityIds();
            if (entitiesIds != null) {
                request.setEntityIds(entitiesIds);
            }
            request.setDetailLevel(searchRequestObject.getSearchDetailsLevel());
            request.setLocale(searchRequestObject.getResultsLanguage());

            switch (searchType) {
                case SEARCH_ONLINE:
 //                   return cloudEntityService.detail(request, entityServiceContext);
                default:
                    if (embeddedEntityService != null) {
                        return embeddedEntityService.detail(request, entityServiceContext);
                    } else {
                        return null;
                    }
            }
        } else {
            return null;
        }
    }

    /**
     * @param searchRequestObject search request object
     * @return a response that will contain the reverse-geocoded result
     */
    public synchronized EntityDetailResponse reverseGeocode(SearchRequestObject searchRequestObject) {
        EntityRgcRequest request = new EntityRgcRequest();
        GeoPoint location = searchRequestObject.getLocation();
        if (location != null) {
            request.setLocation(location);
        }
        request.setLocale(searchRequestObject.getResultsLanguage());

        switch (searchType) {
            case SEARCH_ONLINE:
//                return cloudEntityService.rgc(request, entityServiceContext);
            default:
                if (embeddedEntityService != null) {
                    return embeddedEntityService.rgc(request, entityServiceContext);
                } else {
                    return null;
                }
        }
    }

    /**
     * @return a response that will contain the reverse-geocoded result
     */
    public synchronized EntityCategoryResponse getCategories() {
        EntityCategoryRequest request = new EntityCategoryRequest();
        request.setLocale(Locale.US.toString());
        switch (searchType) {
            case SEARCH_ONLINE:
 //               return cloudEntityService.categories(request, entityServiceContext);
            case SEARCH_OFFLINE:
                if (embeddedEntityService != null) {
                    return embeddedEntityService.categories(request, entityServiceContext);
                }
                break;
        }
        return null;
    }

    // search type constants
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SEARCH_OFFLINE, SEARCH_ONLINE})
    public @interface SearchType {
    }
}