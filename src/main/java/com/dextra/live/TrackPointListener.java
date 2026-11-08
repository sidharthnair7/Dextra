package com.dextra.live;

/** Anything that wants every tracked frame (the attempt recorder, the light check). Called on the tracker thread. */
public interface TrackPointListener {

    void onPoint(TrackPoint point);
}
