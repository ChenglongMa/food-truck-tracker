package mad.geo.model;

import java.util.Locale;

import mad.geo.model.interfaces.Trackable;

/**
 * The abstract class of trackable
 */
public abstract class AbstractTrackable extends AbstractUnique implements Trackable {
    protected int id;
    protected String name;
    protected String description;
    protected String url;
    protected String category;
    protected int image;

    public AbstractTrackable() {
        id = getUniqueIntId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
//        if (intIdSet.add(id)) {
//            intIdSet.remove(this.id);
//            this.id = id;
//        } else throw new IllegalArgumentException("This id has existed.");
    }

    public String getIdString() {
        return String.valueOf(id);
    }

    public String getName() {
        return name;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(),
                "ID: %d\nName: %s\nCategory: %s\nWeb Site: %s\nDescription: %s",
                id, name, category, url, description);
    }
}
