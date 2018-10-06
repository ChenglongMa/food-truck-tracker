package mad.geo.model;

import java.text.DateFormat;
import java.util.Locale;

import mad.geo.model.interfaces.Trackable;

/**
 * GeoTracking
 *
 * @author : Charles Ma
 * @date : 30-08-2018
 * @time : 11:45
 * @description :
 */
public abstract class AbstractTrackable implements Trackable {
    protected int id;
    protected String name;
    protected String description;
    protected String webSiteUrl;//
    protected String category;
    protected String image;//

    public int getId() {
        return id;
    }

    public String getIdString() {
        return String.valueOf(id);
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "ID: %d\nName: %s\nCategory: %s\nWeb Site: %s\nDescription: %s",
                id, name, category, webSiteUrl, description);
    }
}
