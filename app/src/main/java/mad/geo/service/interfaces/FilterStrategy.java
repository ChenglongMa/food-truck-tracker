package mad.geo.service.interfaces;

import java.util.List;

import mad.geo.model.AbstractTrackable;
import mad.geo.model.interfaces.Trackable;

/**
 * GeoTracking
 *
 * @author : Charles Ma
 * @date : 30-08-2018
 * @time : 17:19
 * @description :
 */
public interface FilterStrategy<T> {

    List<AbstractTrackable> filter(T... conditions);
}
