package edu.unh.cs.cs619.bulletzone.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import edu.unh.cs.cs619.bulletzone.model.Direction;
import edu.unh.cs.cs619.bulletzone.model.IllegalTransitionException;
import edu.unh.cs.cs619.bulletzone.model.LimitExceededException;
import edu.unh.cs.cs619.bulletzone.model.TankDoesNotExistException;
import edu.unh.cs.cs619.bulletzone.model.entities.VehicleEntity;
import edu.unh.cs.cs619.bulletzone.repository.GameRepository;
import edu.unh.cs.cs619.bulletzone.util.BooleanWrapper;
import edu.unh.cs.cs619.bulletzone.util.BuildWrapper;
import edu.unh.cs.cs619.bulletzone.util.GridWrapper;
import edu.unh.cs.cs619.bulletzone.util.IntArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.InventoryWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongArrayWrapper;
import edu.unh.cs.cs619.bulletzone.util.LongWrapper;
import edu.unh.cs.cs619.bulletzone.util.MoveWrapper;
import edu.unh.cs.cs619.bulletzone.util.ResultWrapper;
import edu.unh.cs.cs619.bulletzone.util.ValidityWrapper;

@RestController
@RequestMapping(value = "/games")
class GamesController {

    private static final Logger log = LoggerFactory.getLogger(GamesController.class);

    private final GameRepository gameRepository;

    @Autowired
    public GamesController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @RequestMapping(method = RequestMethod.POST, value = "{username}/join", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    ResponseEntity<LongArrayWrapper> join(@PathVariable String username) {
        VehicleEntity[] vehicles;
        try {
            vehicles = gameRepository.join(username);
            log.info("Player joined: tankId={} minerId={} username={}", vehicles[0].getId(),
                    vehicles[1].getId(), username);
            long[] vArr = new long[3];
            vArr[0] = vehicles[0].getId();
            vArr[1] = vehicles[1].getId();
            vArr[2] = vehicles[2].getId();

            return new ResponseEntity<>(
                    new LongArrayWrapper(vArr),
                    HttpStatus.CREATED
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequestMapping(method = RequestMethod.GET, value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public
    @ResponseBody
    ResponseEntity<GridWrapper> grid() {
        return new ResponseEntity<>(new GridWrapper(gameRepository.getGrid()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/turn/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<MoveWrapper> turn(@PathVariable long vehicleId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {

        return new ResponseEntity<>(
                new MoveWrapper(gameRepository.turn(vehicleId, Direction.fromByte(direction)),
                        gameRepository.getFrontMoveValidity(vehicleId),
                        gameRepository.getBackMoveValidity(vehicleId),
                        gameRepository.getCanBuild(vehicleId),
                        gameRepository.getCanDismantle(vehicleId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/move/{direction}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<MoveWrapper> move(@PathVariable long vehicleId, @PathVariable byte direction)
            throws TankDoesNotExistException, LimitExceededException, IllegalTransitionException {

        return new ResponseEntity<>(
                new MoveWrapper(gameRepository.move(vehicleId, Direction.fromByte(direction)),
                        gameRepository.getFrontMoveValidity(vehicleId),
                        gameRepository.getBackMoveValidity(vehicleId),
                        gameRepository.getCanBuild(vehicleId),
                        gameRepository.getCanDismantle(vehicleId)),

                HttpStatus.OK
        );

    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/fire", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long vehicleId)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<>(
                new BooleanWrapper(gameRepository.fire(vehicleId, 1)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/fire/{bulletType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> fire(@PathVariable long vehicleId, @PathVariable int bulletType)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<>(
                new BooleanWrapper(gameRepository.fire(vehicleId, bulletType)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{minerId}/mine/{terrainId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> mine(@PathVariable long minerId, @PathVariable long terrainId)
            throws TankDoesNotExistException, InterruptedException {
        return new ResponseEntity<>(
                new BooleanWrapper(gameRepository.mine(minerId, terrainId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/eject", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BooleanWrapper> eject(@PathVariable long vehicleId)
            throws TankDoesNotExistException, LimitExceededException {
        return new ResponseEntity<>(
                new BooleanWrapper(gameRepository.eject(vehicleId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/dismantle", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BuildWrapper> dismantle(@PathVariable long vehicleId)
            throws TankDoesNotExistException, LimitExceededException, InterruptedException {
        return new ResponseEntity<>(
                new BuildWrapper(gameRepository.dismantle(vehicleId),
                        gameRepository.canBuild(vehicleId),
                        gameRepository.canDismantle(vehicleId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{vehicleId}/build/{structureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<BuildWrapper> build(@PathVariable long vehicleId, @PathVariable long structureId)
            throws TankDoesNotExistException, LimitExceededException, InterruptedException {
        return new ResponseEntity<>(
                new BuildWrapper(gameRepository.build(vehicleId, structureId),
                        gameRepository.canBuild(vehicleId),
                        gameRepository.canDismantle(vehicleId)),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{currId}/getValidity", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<ValidityWrapper> getValidity(@PathVariable long currId)
            throws TankDoesNotExistException {
        boolean canMoveForward = gameRepository.getFrontMoveValidity(currId);
        boolean canMoveBackward = gameRepository.getBackMoveValidity(currId);
        boolean canBuild = gameRepository.getCanBuild(currId);
        boolean canDismantle = gameRepository.getCanDismantle(currId);
        boolean canMine = gameRepository.getCanMine(currId);
        boolean canEject = gameRepository.getPoweredUp(currId);

        System.out.println("canMoveForward: "+canMoveForward);
        System.out.println("canMoveBackward: "+canMoveBackward);
        System.out.println("canBuild: "+canBuild);
        System.out.println("canDismantle: "+canDismantle);
        System.out.println("canMine: "+canMine);
        System.out.println("canEject: "+canEject);

        return new ResponseEntity<>(
                new ValidityWrapper(canMoveForward, canMoveBackward, canBuild, canDismantle, canMine, canEject),
                HttpStatus.OK
        );
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "{username}/leave", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    HttpStatus leave(@PathVariable String username)
            throws TankDoesNotExistException {
        //System.out.println("Games Controller leave() called, tank ID: "+tankId);
        gameRepository.leave(username);

        return HttpStatus.OK;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{username}/devAddResources", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    HttpStatus devAddResources(@PathVariable String username) {
        gameRepository.devAddResources(username);
        return HttpStatus.OK;
    }

    @RequestMapping(method = RequestMethod.PUT, value = "{username}/{tankId}/get-inventory")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    ResponseEntity<InventoryWrapper> getInventory(@PathVariable String username, @PathVariable long tankId) {
        try {
            int[] i = gameRepository.getInventory(username);
            boolean poweredUp = gameRepository.getPoweredUp(tankId);
            return new ResponseEntity<>(
                    new InventoryWrapper(i, poweredUp),
                    HttpStatus.OK
            );
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String handleBadRequests(Exception e) {
        return e.getMessage();
    }
}
