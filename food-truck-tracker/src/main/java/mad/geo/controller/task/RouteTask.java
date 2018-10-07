package mad.geo.controller.task;

import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import mad.geo.model.AsyncTaskResult;
import mad.geo.service.TrackableService;
import mad.geo.utils.ExceptionAsyncTask;
import mad.geo.view.activity.MapsActivity;

/**
 * @author : Chenglong Ma
 */
public class RouteTask extends ExceptionAsyncTask<Integer> {
    private TrackableService trackableService;
    private List<LatLng> routes;
    private final MapsActivity activity;

    public RouteTask(MapsActivity activity) {
        this.activity = activity;
        trackableService = TrackableService.getSingletonInstance(activity);
    }

    @Override
    protected void doInBackground(Integer trackableId) {
        routes = trackableService.getRouteInfo(trackableId);
        if (routes.isEmpty()) {
            throw new IllegalArgumentException("There is no available route information");
        }
    }

    @Override
    protected void dealWithException(Exception e) {
        Toast.makeText(activity.getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void postExecute(AsyncTaskResult<Integer> result) {
        activity.onRequestRoutes(routes);
    }
}
