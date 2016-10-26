package com.telenav.sdk_sample.search;

/**
 * Contains some suggestion details received after a suggestion search
 */
public class SuggestionItem {

    /**
     * entity ID
     */
    private String entityId;

    /**
     * label
     */
    private String label;

    /**
     * query
     */
    private String query;

    /**
     * suggestion id
     */
    private String id;

    /**
     * @return query
     */
    public String getQuery() {
        return query;
    }

    /**
     * sets current query
     * @param query current query
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return entity id
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * sets entity id
     * @param entityId entity id
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    /**
     * @return current label
     */
    public String getLabel() {
        return label;
    }

    /**
     * sets current label
     * @param label current label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return suggestion id
     */
    public String getId() {
        return id;
    }

    /**
     * sets csuggestion id
     * @param id suggestion id
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return label;
    }
}