package edu.unh.cs.cs619.bulletzone.rest;

import org.androidannotations.rest.spring.annotations.Delete;
import org.androidannotations.rest.spring.annotations.Get;
import org.androidannotations.rest.spring.annotations.Path;
import org.androidannotations.rest.spring.annotations.Post;
import org.androidannotations.rest.spring.annotations.Put;
import org.androidannotations.rest.spring.annotations.Rest;
import org.androidannotations.rest.spring.annotations.RestService.*;
import org.androidannotations.rest.spring.api.RestClientErrorHandling;
import org.androidannotations.rest.spring.api.RestClientHeaders.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.BuildWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.IntArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.MoveWrapper;
import edu.unh.cs.cs619.bulletzone.util.ValidityWrapper;

// for localhost
@Rest(rootUrl = "http://10.21.179.66:61901/games",
// For stman1
// be sure to move it up to stman1 using scp or something of the like, and then just run it there.
//@Rest(rootUrl = "http://stman1.cs.unh.edu:61901/games",
        converters = {StringHttpMessageConverter.class, MappingJackson2HttpMessageConverter.class}
        // TODO: disable intercepting and logging
        // , interceptors = { HttpLoggerInterceptor.class }
)
public interface BulletZoneRestClient extends RestClientErrorHandling {
    void setRootUrl(String rootUrl);

    @Post("/{username}/join")
    LongArrayWrapper join(@Path String username) throws RestClientException;

    @Get("")
    GridWrapper grid();

    @Put("/{username}/{tankId}/get-inventory")
    InventoryWrapper getInventory(@Path String username, @Path long tankId);

    @Put("/account/register/{username}/{password}")
    BooleanWrapper register(@Path String username, @Path String password);

    @Put("/account/login/{username}/{password}")
    LongWrapper login(@Path String username, @Path String password);

    @Put("/{tankId}/move/{direction}")
    MoveWrapper move(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/turn/{direction}")
    MoveWrapper turn(@Path long tankId, @Path byte direction);

    @Put("/{tankId}/fire/1")
    BooleanWrapper fire(@Path long tankId);

    @Put("/{tankId}/eject")
    BooleanWrapper eject(@Path long tankId);

    @Put("/{tankId}/dismantle")
    BuildWrapper dismantle(@Path long tankId);

    @Put("/{tankId}/build/{structureId}")
    BuildWrapper build(@Path long tankId, @Path long structureId);

    @Put("/{tankId}/fire/{bulletType}")
    BooleanWrapper fire(@Path long tankId, @Path int bulletType);

    @Put("/{minerId}/mine/{terrainId}")
    BooleanWrapper mine(@Path long minerId, @Path long terrainId);

    @Delete("/{username}/leave")
    BooleanWrapper leave(@Path String username);

    @Put("/{currId}/getValidity")
    ValidityWrapper getValidity(@Path long currId);

    /**
     * This method is used to add 10 of each resource to a user's account.
     * @param username The username of the user to add resources to.
     * @return A BooleanWrapper containing the result of the operation.
     */
    @Put("/{username}/devAddResources")
    BooleanWrapper devAddResources(@Path String username);

//    @Put("/{vehicleId}/getHealth")
//    LongWrapper getUnitHealth(@Path long vehicleId);
}
