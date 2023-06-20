package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.blackout.SatelliteConstructor;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task3ExampleTests {
    @Test
    public void testElephantSatelliteMovement() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));

        SatelliteConstructor initialSatellite = controller.findSatelliteById("Satellite1");
        Angle initialSatellitePosition = initialSatellite.getSatellitePosition();

        controller.simulate(5);

        SatelliteConstructor updatedSatellite = controller.findSatelliteById("Satellite1");
        Angle updatedSatellitePosition = updatedSatellite.getSatellitePosition();

        assertNotEquals(initialSatellitePosition, updatedSatellitePosition);

    }

    @Test
    public void testElephantSatelliteMovementSpeed() {
        BlackoutController controller = new BlackoutController();
        Angle initialPosition = Angle.fromDegrees(90);
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, initialPosition);
        controller.simulate(5);

        SatelliteConstructor updatedSatellite = controller.findSatelliteById("Satellite1");
        double observedSpeedPerMinute = (100.24441423972662 - initialPosition.toDegrees()) / 5;
        Angle expectedPosition = Angle.fromDegrees(initialPosition.toDegrees() + 5 * observedSpeedPerMinute);
        assertEquals(expectedPosition, updatedSatellite.getSatellitePosition());
    }

    @Test
    public void testElephantSatelliteLooping() {
        // Create an ElephantSatellite
        BlackoutController controller = new BlackoutController();
        Angle initialPosition = Angle.fromDegrees(350);
        controller.createSatellite("Satellite1", "ElephantSatellite", 1000 + RADIUS_OF_JUPITER, initialPosition);
        controller.simulate(15);

        SatelliteConstructor updatedSatellite = controller.findSatelliteById("Satellite1");
        double updatedPosition = updatedSatellite.getSatellitePosition().toDegrees();
        assertTrue(updatedPosition >= 360 || updatedPosition < initialPosition.toDegrees());
    }
}
