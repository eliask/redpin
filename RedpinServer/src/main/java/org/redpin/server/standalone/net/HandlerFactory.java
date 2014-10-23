/**
 * Filename: HandlerFactory.java (in org.redpin.server.standalone.net) This file
 * is part of the Redpin project.
 *
 * Redpin is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 *
 * Redpin is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Redpin. If not, see <http://www.gnu.org/licenses/>.
 *
 * (c) Copyright ETH Zurich, Pascal Brogle, Philipp Bolliger, 2010, ALL RIGHTS
 * RESERVED.
 *
 * www.redpin.org
 */
package org.redpin.server.standalone.net;

import org.redpin.server.standalone.net.RequestHandler.RequestType;

public class HandlerFactory {

    private static UnknownHandler unkownHandler;

    private static SetMapHandler setMapHandler;

    private static SetFingerprintHandler setFingerprintHandler;

    private static GetMapListHandler getMapListHandler;

    private static RemoveMapHandler removeMapHandler;

    private static UpdateLocationHandler updateLocationHandler;

    private static GetLocationHandler getLocationHandler;

    private static GetLocationListHandler getLocationListHandler;

    private static RemoveLocationHandler removeLocationHandler;

    /**
     * Finds the correct {@link IHandler} for the request type. if no
     * {@link IHandler} can be found, the {@link UnknownHandler} is returned
     *
     * @param type Request type
     * @return appropriate handler
     */
    public static IHandler findHandler(RequestType type) {

        switch (type) {
            case setFingerprint:
                return getSetFingerprintHandler();
            case getMapList:
                return getGetMapHandler();
            case setMap:
                return getSetMapHandler();
            case removeMap:
                return getRemoveMapHandler();
            case getLocation:
                return getGetLocationHandler();
            case getLocationList:
                return getGetLocationListHandler();
            case updateLocation:
                return getUpdateLocationHandler();
            case removeLocation:
                return getRemoveLocationHandler();
            default:
                return getUnknownHandler();
        }
    }

    private static UnknownHandler getUnknownHandler() {
        if (unkownHandler == null) {
            unkownHandler = new UnknownHandler();
        }
        return unkownHandler;
    }

    private static SetMapHandler getSetMapHandler() {
        if (setMapHandler == null) {
            setMapHandler = new SetMapHandler();
        }
        return setMapHandler;
    }

    private static SetFingerprintHandler getSetFingerprintHandler() {
        if (setFingerprintHandler == null) {
            setFingerprintHandler = new SetFingerprintHandler();
        }

        return setFingerprintHandler;
    }

    public static GetMapListHandler getGetMapHandler() {
        if (getMapListHandler == null) {
            getMapListHandler = new GetMapListHandler();
        }

        return getMapListHandler;
    }

    public static RemoveMapHandler getRemoveMapHandler() {
        if (removeMapHandler == null) {
            removeMapHandler = new RemoveMapHandler();
        }

        return removeMapHandler;
    }

    public static UpdateLocationHandler getUpdateLocationHandler() {
        if (updateLocationHandler == null) {
            updateLocationHandler = new UpdateLocationHandler();
        }

        return updateLocationHandler;
    }

    public static GetLocationHandler getGetLocationHandler() {
        if (getLocationHandler == null) {
            getLocationHandler = new GetLocationHandler();
        }

        return getLocationHandler;
    }

    public static GetLocationListHandler getGetLocationListHandler() {
        if (getLocationListHandler == null) {
            getLocationListHandler = new GetLocationListHandler();
        }

        return getLocationListHandler;
    }

    public static RemoveLocationHandler getRemoveLocationHandler() {
        if (removeLocationHandler == null) {
            removeLocationHandler = new RemoveLocationHandler();
        }

        return removeLocationHandler;
    }

    private HandlerFactory() {
    }

}
